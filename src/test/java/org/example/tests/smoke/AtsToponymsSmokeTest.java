package org.example.tests.smoke;

import io.qameta.allure.Description;
import io.qameta.allure.Owner;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.restassured.response.Response;
import org.example.client.AtsToponymsClient;
import org.example.config.AppConfig;
import org.example.http.ApiClient;
import org.example.http.ApiSpecificationFactory;
import org.example.service.AtsToponymsService;
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
class AtsToponymsSmokeTest {
    private static final String ACCEPT_LANGUAGE_RU = "ru-RU";
    private static final String ATS_ID_VALID = "106724";
    private static final String CITY_VALID = "Сарыарка";

    private AtsToponymsService atsToponymsService;

    @BeforeEach
    void setUp() {
        atsToponymsService = AtsToponymsService.defaultService();
    }

    @Test
    @DisplayName("GET /ats/{atsId}/toponyms возвращает 200 для валидного запроса")
    @Description("Проверяет доступность эндпоинта при валидном API-ключе, atsId=106724 и city=Сарыарка")
    @Severity(SeverityLevel.BLOCKER)
    void shouldReturn200ForValidRequest() {
        Response response = atsToponymsService.getAtsToponyms(ATS_ID_VALID, CITY_VALID, ACCEPT_LANGUAGE_RU);

        assertThat(response.statusCode()).isEqualTo(200);
    }

    @Test
    @DisplayName("GET /ats/{atsId}/toponyms возвращает валидный JSON-контракт")
    @Description("Проверяет наличие обязательных полей localityId, region, name, path, children, rca в первом объекте массива ответа")
    @Severity(SeverityLevel.CRITICAL)
    void shouldReturnValidJsonContract() {
        Response response = atsToponymsService.getAtsToponyms(ATS_ID_VALID, CITY_VALID, ACCEPT_LANGUAGE_RU);

        assertThat(response.statusCode()).isEqualTo(200);
        assertThat(response.getContentType()).contains("application/json");

        List<Map<String, Object>> toponyms = response.jsonPath().getList("$");
        assertThat(toponyms).isNotNull().isNotEmpty();

        Map<String, Object> firstItem = toponyms.get(0);
        assertThat(firstItem).containsKeys("localityId", "region", "name", "path", "children", "rca");
    }

    @Test
    @DisplayName("GET /ats/{atsId}/toponyms без API-ключа отклоняется")
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
        AtsToponymsService unauthenticService = new AtsToponymsService(new AtsToponymsClient(clientWithoutApiKey));

        Response response = unauthenticService.getAtsToponyms(ATS_ID_VALID, CITY_VALID, ACCEPT_LANGUAGE_RU);

        assertThat(Set.of(401, 403)).contains(response.statusCode());
    }

    @Test
    @DisplayName("GET /ats/{atsId}/toponyms без обязательного atsId отклоняется")
    @Description("Проверяет, что запрос без обязательного path-параметра atsId возвращает ошибку клиента")
    @Severity(SeverityLevel.CRITICAL)
    void shouldReturnErrorWithoutAtsId() {
        Response response = atsToponymsService.getAtsToponymsWithoutAtsId(CITY_VALID, ACCEPT_LANGUAGE_RU);

        assertThat(response.statusCode()).isBetween(400, 499);
    }

    @Test
    @DisplayName("GET /ats/{atsId}/toponyms без обязательного city отклоняется")
    @Description("Проверяет, что запрос без обязательного query-параметра city возвращает ошибку валидации")
    @Severity(SeverityLevel.CRITICAL)
    void shouldReturnValidationErrorWithoutCity() {
        Response response = atsToponymsService.getAtsToponyms(ATS_ID_VALID, null, ACCEPT_LANGUAGE_RU);

        assertThat(Set.of(400, 422)).contains(response.statusCode());
    }
}
