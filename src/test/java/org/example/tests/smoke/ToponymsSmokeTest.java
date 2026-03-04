package org.example.tests.smoke;

import io.qameta.allure.Description;
import io.qameta.allure.Owner;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.restassured.response.Response;
import org.example.client.ToponymsClient;
import org.example.config.AppConfig;
import org.example.http.ApiClient;
import org.example.http.ApiSpecificationFactory;
import org.example.service.ToponymsService;
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
class ToponymsSmokeTest {
    private static final String ACCEPT_LANGUAGE_RU = "ru-RU";
    private static final String CITY_ASTANA = "Астана";

    private ToponymsService toponymsService;

    @BeforeEach
    void setUp() {
        toponymsService = ToponymsService.defaultService();
    }

    @Test
    @DisplayName("GET /toponyms возвращает 200")
    @Description("Проверяет доступность эндпоинта при валидном API-ключе и city=Астана")
    @Severity(SeverityLevel.BLOCKER)
    void shouldReturn200ForToponymsRequest() {
        Response response = toponymsService.getToponyms(CITY_ASTANA, ACCEPT_LANGUAGE_RU);

        assertThat(response.statusCode()).isEqualTo(200);
    }

    @Test
    @DisplayName("GET /toponyms возвращает валидный JSON-контракт")
    @Description("Проверяет JSON-ответ на наличие обязательных и заполненных полей для city=Астана")
    @Severity(SeverityLevel.CRITICAL)
    void shouldReturnValidContractForToponymsResponse() {
        Response response = toponymsService.getToponyms(CITY_ASTANA, ACCEPT_LANGUAGE_RU);

        assertThat(response.statusCode()).isEqualTo(200);
        assertThat(response.getContentType()).contains("application/json");

        List<Map<String, Object>> toponyms = response.jsonPath().getList("$");
        assertThat(toponyms).isNotNull().isNotEmpty();

        Map<String, Object> astanaItem = toponyms.stream()
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
    @DisplayName("GET /toponyms без API-ключа отклоняется")
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
        ToponymsService unauthService = new ToponymsService(new ToponymsClient(clientWithoutApiKey));

        Response response = unauthService.getToponyms(CITY_ASTANA, ACCEPT_LANGUAGE_RU);

        assertThat(Set.of(401, 403)).contains(response.statusCode());
    }

    @Test
    @DisplayName("GET /toponyms без обязательного city отклоняется")
    @Description("Проверяет, что запрос без обязательного параметра city возвращает ошибку валидации")
    @Severity(SeverityLevel.CRITICAL)
    void shouldReturnValidationErrorWithoutCity() {
        Response response = toponymsService.getToponyms(null, ACCEPT_LANGUAGE_RU);

        assertThat(Set.of(400, 422)).contains(response.statusCode());
    }
}
