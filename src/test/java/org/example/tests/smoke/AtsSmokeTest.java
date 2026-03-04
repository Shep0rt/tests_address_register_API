package org.example.tests.smoke;

import io.qameta.allure.Description;
import io.qameta.allure.Owner;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.restassured.response.Response;
import org.example.client.AtsClient;
import org.example.config.AppConfig;
import org.example.http.ApiClient;
import org.example.http.ApiSpecificationFactory;
import org.example.service.AtsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@Tag("smoke")
@Owner("Pavel Michka")
class AtsSmokeTest {
    private static final String ACCEPT_LANGUAGE_RU = "ru-RU";
    private static final String CITY_ASTANA = "Астана";

    private AtsService atsService;

    @BeforeEach
    void setUp() {
        atsService = AtsService.defaultService();
    }

    @Test
    @DisplayName("GET /ats возвращает 200 для валидного запроса")
    @Description("Проверяет доступность эндпоинта при валидном API-ключе и city=Астана")
    @Severity(SeverityLevel.BLOCKER)
    void shouldReturn200ForAtsRequest() {
        Response response = atsService.getAts(CITY_ASTANA, ACCEPT_LANGUAGE_RU);

        assertThat(response.statusCode()).isEqualTo(200);
    }

    @Test
    @DisplayName("GET /ats возвращает валидный JSON-контракт")
    @Description("Проверяет JSON-ответ на наличие обязательных полей для объекта с region.city=г. Астана")
    @Severity(SeverityLevel.CRITICAL)
    void shouldReturnValidContractForAtsResponse() {
        Response response = atsService.getAts(CITY_ASTANA, ACCEPT_LANGUAGE_RU);

        assertThat(response.statusCode()).isEqualTo(200);
        assertThat(response.getContentType()).contains("application/json");

        List<Map<String, Object>> atsItems = response.jsonPath().getList("$");
        assertThat(atsItems).isNotNull().isNotEmpty();

        Map<String, Object> astanaItem = atsItems.stream()
                .filter(item -> item.get("region") instanceof Map)
                .filter(item -> {
                    @SuppressWarnings("unchecked")
                    Map<String, Object> region = (Map<String, Object>) item.get("region");
                    return "г. Астана".equals(region.get("city"));
                })
                .findFirst()
                .orElse(null);

        assertThat(astanaItem)
                .as("В ответе должен быть объект с region.city = 'г. Астана'")
                .isNotNull();

        assertThat(astanaItem).containsKeys("localityId", "region", "name", "path", "children", "rca");

        @SuppressWarnings("unchecked")
        Map<String, Object> region = (Map<String, Object>) astanaItem.get("region");
        assertThat(region).containsKeys("city", "localityId", "regionCode");
    }

    @Test
    @DisplayName("GET /ats без API-ключа отклоняется")
    @Description("Проверяет, что запрос без X-API-KEY возвращает 401 или 403")
    @Severity(SeverityLevel.BLOCKER)
    void shouldReturnUnauthorizedWithoutApiKey() {
        AppConfig currentConfig = AppConfig.load();
        AppConfig configWithoutApiKey = new AppConfig(
                currentConfig.baseUrl(),
                currentConfig.connectTimeoutMs(),
                currentConfig.readTimeoutMs(),
                ""
        );

        ApiClient clientWithoutApiKey = new ApiClient(ApiSpecificationFactory.create(configWithoutApiKey));
        AtsService unauthService = new AtsService(new AtsClient(clientWithoutApiKey));

        Response response = unauthService.getAts(CITY_ASTANA, ACCEPT_LANGUAGE_RU);

        assertThat(Set.of(401, 403)).contains(response.statusCode());
    }

    @Test
    @DisplayName("GET /ats без обязательного city отклоняется")
    @Description("Проверяет, что запрос без обязательного параметра city возвращает ошибку валидации")
    @Severity(SeverityLevel.CRITICAL)
    void shouldReturnValidationErrorWithoutCity() {
        Response response = atsService.getAts(null, ACCEPT_LANGUAGE_RU);

        assertThat(Set.of(400, 422)).contains(response.statusCode());
    }
}