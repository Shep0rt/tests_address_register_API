package org.example.tests.smoke;

import io.qameta.allure.Description;
import io.qameta.allure.Owner;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.restassured.response.Response;
import org.example.client.AtsByIdClient;
import org.example.config.AppConfig;
import org.example.http.ApiClient;
import org.example.http.ApiSpecificationFactory;
import org.example.service.AtsByIdService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@Tag("smoke")
@Owner("Pavel Michka")
class AtsByIdSmokeTest {
    private static final String ATS_ID_VALID = "106724";

    private AtsByIdService atsByIdService;

    @BeforeEach
    void setUp() {
        atsByIdService = AtsByIdService.defaultService();
    }

    @Test
    @DisplayName("GET /ats/{atsId} возвращает 200 для валидного запроса")
    @Description("Проверяет доступность эндпоинта при валидном API-ключе и atsId=106724")
    @Severity(SeverityLevel.BLOCKER)
    void shouldReturn200ForValidRequest() {
        Response response = atsByIdService.getAtsById(ATS_ID_VALID);

        assertThat(response.statusCode()).isEqualTo(200);
    }

    @Test
    @DisplayName("GET /ats/{atsId} возвращает валидный JSON-контракт")
    @Description("Проверяет наличие обязательных полей localityId, region, name, path, rca в ответе")
    @Severity(SeverityLevel.CRITICAL)
    void shouldReturnValidJsonContract() {
        Response response = atsByIdService.getAtsById(ATS_ID_VALID);

        assertThat(response.statusCode()).isEqualTo(200);
        assertThat(response.getContentType()).contains("application/json");

        Map<String, Object> ats = response.jsonPath().getMap("$");
        assertThat(ats).isNotNull();
        assertThat(ats).containsKeys("localityId", "region", "name", "path", "rca");
    }

    @Test
    @DisplayName("GET /ats/{atsId} без API-ключа отклоняется")
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
        AtsByIdService unauthenticService = new AtsByIdService(new AtsByIdClient(clientWithoutApiKey));

        Response response = unauthenticService.getAtsById(ATS_ID_VALID);

        assertThat(Set.of(401, 403)).contains(response.statusCode());
    }
}
