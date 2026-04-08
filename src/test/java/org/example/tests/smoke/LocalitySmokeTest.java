package org.example.tests.smoke;

import io.qameta.allure.Description;
import io.qameta.allure.Owner;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.restassured.response.Response;
import org.example.client.LocalityClient;
import org.example.config.AppConfig;
import org.example.http.ApiClient;
import org.example.http.ApiSpecificationFactory;
import org.example.service.LocalityService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@Tag("smoke")
@Owner("Pavel Michka")
class LocalitySmokeTest {
    private static final String ID_VALID = "106724";

    private LocalityService localityService;

    @BeforeEach
    void setUp() {
        localityService = LocalityService.defaultService();
    }

    @Test
    @DisplayName("GET /locality возвращает 200 для валидного запроса")
    @Description("Проверяет доступность эндпоинта при валидном API-ключе и id=106724")
    @Severity(SeverityLevel.BLOCKER)
    void shouldReturn200ForValidRequest() {
        Response response = localityService.getLocality(ID_VALID);

        assertThat(response.statusCode()).isEqualTo(200);
    }

    @Test
    @DisplayName("GET /locality возвращает валидный JSON-контракт")
    @Description("Проверяет наличие обязательных полей localityId, region, name, path и внутри region: city, localityId, regionCode для id=106724")
    @Severity(SeverityLevel.CRITICAL)
    void shouldReturnValidJsonContract() {
        Response response = localityService.getLocality(ID_VALID);

        assertThat(response.statusCode()).isEqualTo(200);
        assertThat(response.getContentType()).contains("application/json");

        Map<String, Object> locality = response.jsonPath().getMap("$");
        assertThat(locality).isNotNull();
        assertThat(locality).containsKeys("localityId", "region", "name", "path");

        Object regionObj = locality.get("region");
        assertThat(regionObj)
                .as("Поле region должно присутствовать")
                .isInstanceOf(Map.class);

        @SuppressWarnings("unchecked")
        Map<String, Object> region = (Map<String, Object>) regionObj;
        assertThat(region).containsKeys("city", "localityId", "regionCode");
    }

    @Test
    @DisplayName("GET /locality без API-ключа отклоняется")
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
        LocalityService unauthService = new LocalityService(new LocalityClient(clientWithoutApiKey));

        Response response = unauthService.getLocality(ID_VALID);

        assertThat(Set.of(401, 403)).contains(response.statusCode());
    }

    @Test
    @DisplayName("GET /locality без обязательного id отклоняется")
    @Description("Проверяет, что запрос без обязательного параметра id возвращает ошибку валидации")
    @Severity(SeverityLevel.CRITICAL)
    void shouldReturnValidationErrorWithoutId() {
        Response response = localityService.getLocality(null);

        assertThat(Set.of(400, 422)).contains(response.statusCode());
    }
}
