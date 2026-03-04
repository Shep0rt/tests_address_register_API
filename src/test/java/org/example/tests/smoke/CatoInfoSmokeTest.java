package org.example.tests.smoke;

import io.qameta.allure.Description;
import io.qameta.allure.Owner;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.restassured.response.Response;
import org.example.client.CatoInfoClient;
import org.example.config.AppConfig;
import org.example.http.ApiClient;
import org.example.http.ApiSpecificationFactory;
import org.example.service.CatoInfoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@Tag("smoke")
@Owner("Pavel Michka")
class CatoInfoSmokeTest {
    private static final String CATO_VALID = "710000000";

    private CatoInfoService catoInfoService;

    @BeforeEach
    void setUp() {
        catoInfoService = CatoInfoService.defaultService();
    }

    @Test
    @DisplayName("GET /cato-info возвращает 200 для валидного запроса")
    @Description("Проверяет доступность эндпоинта при валидном API-ключе и cato=710000000")
    @Severity(SeverityLevel.BLOCKER)
    void shouldReturn200ForValidRequest() {
        Response response = catoInfoService.getCatoInfo(CATO_VALID);

        assertThat(response.statusCode()).isEqualTo(200);
    }

    @Test
    @DisplayName("GET /cato-info возвращает валидный JSON-контракт")
    @Description("Проверяет JSON-ответ на наличие обязательных и заполненных полей city, localityId и regionCode в ответе")
    @Severity(SeverityLevel.CRITICAL)
    void shouldReturnValidJsonContract() {
        Response response = catoInfoService.getCatoInfo(CATO_VALID);

        assertThat(response.statusCode()).isEqualTo(200);
        assertThat(response.getContentType()).contains("application/json");

        Object city = response.jsonPath().get("city");
        Object localityId = response.jsonPath().get("localityId");
        Object regionCode = response.jsonPath().get("regionCode");

        assertThat(city).as("Поле city не должно быть пустым").isNotNull();
        assertThat(response.jsonPath().getString("city").trim())
                .as("Поле city не должно быть пустой строкой")
                .isNotEmpty();

        assertThat(localityId)
                .as("Поле localityId не должно быть пустым")
                .isNotNull();
        assertThat(localityId)
                .as("Поле localityId должно быть числом")
                .isInstanceOf(Number.class);

        assertThat(regionCode).as("Поле regionCode не должно быть пустым").isNotNull();
        assertThat(response.jsonPath().getString("regionCode").trim())
                .as("Поле regionCode не должно быть пустой строкой")
                .isNotEmpty();
    }

    @Test
    @DisplayName("GET /cato-info без API-ключа отклоняется")
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
        CatoInfoService unauthService = new CatoInfoService(new CatoInfoClient(clientWithoutApiKey));

        Response response = unauthService.getCatoInfo(CATO_VALID);

        assertThat(Set.of(401, 403)).contains(response.statusCode());
    }

    @Test
    @DisplayName("GET /cato-info без обязательного cato отклоняется")
    @Description("Проверяет, что запрос без обязательного параметра cato возвращает ошибку валидации")
    @Severity(SeverityLevel.CRITICAL)
    void shouldReturnValidationErrorWithoutCato() {
        Response response = catoInfoService.getCatoInfo(null);

        assertThat(Set.of(400, 422)).contains(response.statusCode());
    }
}
