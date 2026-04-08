package org.example.tests.smoke;

import io.qameta.allure.Description;
import io.qameta.allure.Owner;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.restassured.response.Response;
import org.example.client.GeonimGroundsClient;
import org.example.config.AppConfig;
import org.example.http.ApiClient;
import org.example.http.ApiSpecificationFactory;
import org.example.service.GeonimGroundsService;
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
class GeonimGroundsSmokeTest {
    private static final String GEONIM_ID_VALID = "108553";
    private static final String NUMBER_VALID = "1";

    private GeonimGroundsService geonimGroundsService;

    @BeforeEach
    void setUp() {
        geonimGroundsService = GeonimGroundsService.defaultService();
    }

    @Test
    @DisplayName("GET /geonims/{geonimId}/grounds возвращает 200 для валидного запроса")
    @Description("Проверяет доступность эндпоинта при валидном API-ключе и geonimId=108553")
    @Severity(SeverityLevel.BLOCKER)
    void shouldReturn200ForValidRequest() {
        Response response = geonimGroundsService.getGeonimGrounds(GEONIM_ID_VALID, null);

        assertThat(response.statusCode()).isEqualTo(200);
    }

    @Test
    @DisplayName("GET /geonims/{geonimId}/grounds с number возвращает 200 для валидного запроса")
    @Description("Проверяет доступность эндпоинта при валидном API-ключе, geonimId=108553 и number=1")
    @Severity(SeverityLevel.BLOCKER)
    void shouldReturn200ForValidRequestWithNumber() {
        Response response = geonimGroundsService.getGeonimGrounds(GEONIM_ID_VALID, NUMBER_VALID);

        assertThat(response.statusCode()).isEqualTo(200);
    }

    @Test
    @DisplayName("GET /geonims/{geonimId}/grounds возвращает валидный JSON-контракт")
    @Description("Проверяет наличие обязательных полей id, number, cadastreNumber, rca в первом объекте массива ответа при geonimId=108553")
    @Severity(SeverityLevel.CRITICAL)
    void shouldReturnValidJsonContract() {
        Response response = geonimGroundsService.getGeonimGrounds(GEONIM_ID_VALID, null);

        assertThat(response.statusCode()).isEqualTo(200);
        assertThat(response.getContentType()).contains("application/json");

        List<Map<String, Object>> grounds = response.jsonPath().getList("$");
        assertThat(grounds).isNotNull().isNotEmpty();

        Map<String, Object> firstItem = grounds.get(0);
        assertThat(firstItem).containsKeys("id", "number", "cadastreNumber", "rca");
    }

    @Test
    @DisplayName("GET /geonims/{geonimId}/grounds без API-ключа отклоняется")
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
        GeonimGroundsService unauthService = new GeonimGroundsService(new GeonimGroundsClient(clientWithoutApiKey));

        Response response = unauthService.getGeonimGrounds(GEONIM_ID_VALID, null);

        assertThat(Set.of(401, 403)).contains(response.statusCode());
    }

    @Test
    @DisplayName("GET /geonims/{geonimId}/grounds без обязательного geonimId отклоняется")
    @Description("Проверяет, что запрос без обязательного path-параметра geonimId возвращает ошибку клиента")
    @Severity(SeverityLevel.CRITICAL)
    void shouldReturnErrorWithoutGeonimId() {
        Response response = geonimGroundsService.getGeonimGroundsWithoutGeonimId(null);

        assertThat(response.statusCode()).isBetween(400, 499);
    }
}
