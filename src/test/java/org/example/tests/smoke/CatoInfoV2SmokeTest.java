package org.example.tests.smoke;

import io.qameta.allure.Description;
import io.qameta.allure.Owner;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.restassured.response.Response;
import org.example.client.CatoInfoV2Client;
import org.example.config.AppConfig;
import org.example.http.ApiClient;
import org.example.http.ApiSpecificationFactory;
import org.example.service.CatoInfoV2Service;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@Tag("smoke")
@Owner("Pavel Michka")
class CatoInfoV2SmokeTest {
    private static final String CATO_VALID = "710000000";

    private CatoInfoV2Service catoInfoV2Service;

    @BeforeEach
    void setUp() {
        catoInfoV2Service = CatoInfoV2Service.defaultService();
    }

    @Test
    @DisplayName("GET /cato-info-v2 возвращает 200 для валидного запроса")
    @Description("Проверяет доступность эндпоинта при валидном API-ключе и cato=710000000")
    @Severity(SeverityLevel.BLOCKER)
    void shouldReturn200ForValidRequest() {
        Response response = catoInfoV2Service.getCatoInfoV2(CATO_VALID);

        assertThat(response.statusCode()).isEqualTo(200);
    }

    @Test
    @DisplayName("GET /cato-info-v2 возвращает валидный JSON-контракт")
    @Description("Проверяет JSON-ответ на наличие обязательных и заполненных полей city, path, localityId и regionCode")
    @Severity(SeverityLevel.CRITICAL)
    void shouldReturnValidJsonContract() {
        Response response = catoInfoV2Service.getCatoInfoV2(CATO_VALID);

        assertThat(response.statusCode()).isEqualTo(200);
        assertThat(response.getContentType()).contains("application/json");

        Object city = response.jsonPath().get("city");
        Object path = response.jsonPath().get("path");
        Object localityId = response.jsonPath().get("localityId");
        Object regionCode = response.jsonPath().get("regionCode");

        assertThat(city).as("Поле city не должно быть пустым").isNotNull();
        assertThat(response.jsonPath().getString("city").trim())
                .as("Поле city не должно быть пустой строкой")
                .isNotEmpty();

        assertThat(path).as("Поле path не должно быть пустым").isNotNull();
        assertThat(response.jsonPath().getString("path").trim())
                .as("Поле path не должно быть пустой строкой")
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
    @DisplayName("GET /cato-info-v2 без API-ключа отклоняется")
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
        CatoInfoV2Service unauthService = new CatoInfoV2Service(new CatoInfoV2Client(clientWithoutApiKey));

        Response response = unauthService.getCatoInfoV2(CATO_VALID);

        assertThat(Set.of(401, 403)).contains(response.statusCode());
    }

    @Test
    @DisplayName("GET /cato-info-v2 без обязательного cato отклоняется")
    @Description("Проверяет, что запрос без обязательного параметра cato возвращает ошибку валидации")
    @Severity(SeverityLevel.CRITICAL)
    void shouldReturnValidationErrorWithoutCato() {
        Response response = catoInfoV2Service.getCatoInfoV2(null);

        assertThat(Set.of(400, 422)).contains(response.statusCode());
    }
}
