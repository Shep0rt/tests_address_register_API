package org.example.tests.smoke;

import io.qameta.allure.Description;
import io.qameta.allure.Owner;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.restassured.response.Response;
import org.example.client.AtsGeonimsClient;
import org.example.config.AppConfig;
import org.example.http.ApiClient;
import org.example.http.ApiSpecificationFactory;
import org.example.service.AtsGeonimsService;
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
class AtsGeonimsSmokeTest {
    private static final String ATS_ID_VALID = "107989";
    private static final String STREET_VALID = "батыр";

    private AtsGeonimsService atsGeonimsService;

    @BeforeEach
    void setUp() {
        atsGeonimsService = AtsGeonimsService.defaultService();
    }

    @Test
    @DisplayName("GET /ats/{atsId}/geonims возвращает 200 для валидного запроса")
    @Description("Проверяет доступность эндпоинта при валидном API-ключе, atsId=107989 и street=батыр")
    @Severity(SeverityLevel.BLOCKER)
    void shouldReturn200ForValidRequest() {
        Response response = atsGeonimsService.getAtsGeonims(ATS_ID_VALID, STREET_VALID);

        assertThat(response.statusCode()).isEqualTo(200);
    }

    @Test
    @DisplayName("GET /ats/{atsId}/geonims возвращает валидный JSON-контракт")
    @Description("Проверяет наличие обязательных полей id, nameRu, nameKz, typeRu, typeKz, cato в первом объекте массива ответа при atsId=107989 и street=батыр")
    @Severity(SeverityLevel.CRITICAL)
    void shouldReturnValidJsonContract() {
        Response response = atsGeonimsService.getAtsGeonims(ATS_ID_VALID, STREET_VALID);

        assertThat(response.statusCode()).isEqualTo(200);
        assertThat(response.getContentType()).contains("application/json");

        List<Map<String, Object>> geonims = response.jsonPath().getList("$");
        assertThat(geonims).isNotNull().isNotEmpty();

        Map<String, Object> firstItem = geonims.get(0);
        assertThat(firstItem).containsKeys("id", "nameRu", "nameKz", "typeRu", "typeKz", "cato");
    }

    @Test
    @DisplayName("GET /ats/{atsId}/geonims без API-ключа отклоняется")
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
        AtsGeonimsService unauthService = new AtsGeonimsService(new AtsGeonimsClient(clientWithoutApiKey));

        Response response = unauthService.getAtsGeonims(ATS_ID_VALID, STREET_VALID);

        assertThat(Set.of(401, 403)).contains(response.statusCode());
    }

    @Test
    @DisplayName("GET /ats/{atsId}/geonims без обязательного atsId отклоняется")
    @Description("Проверяет, что запрос без обязательного path-параметра atsId возвращает ошибку клиента")
    @Severity(SeverityLevel.CRITICAL)
    void shouldReturnErrorWithoutAtsId() {
        Response response = atsGeonimsService.getAtsGeonimsWithoutAtsId(STREET_VALID);

        assertThat(response.statusCode()).isBetween(400, 499);
    }

    @Test
    @DisplayName("GET /ats/{atsId}/geonims без обязательного street отклоняется")
    @Description("Проверяет, что запрос без обязательного query-параметра street возвращает ошибку валидации")
    @Severity(SeverityLevel.CRITICAL)
    void shouldReturnValidationErrorWithoutStreet() {
        Response response = atsGeonimsService.getAtsGeonims(ATS_ID_VALID, null);

        assertThat(Set.of(400, 422)).contains(response.statusCode());
    }
}
