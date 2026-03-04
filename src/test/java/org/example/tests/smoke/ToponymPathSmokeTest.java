package org.example.tests.smoke;

import io.qameta.allure.Description;
import io.qameta.allure.Owner;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.restassured.response.Response;
import org.example.client.ToponymPathClient;
import org.example.config.AppConfig;
import org.example.http.ApiClient;
import org.example.http.ApiSpecificationFactory;
import org.example.service.ToponymPathService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@Tag("smoke")
@Owner("Pavel Michka")
class ToponymPathSmokeTest {
    private static final String LOCALITY_ID_VALID = "106724";

    private ToponymPathService toponymPathService;

    @BeforeEach
    void setUp() {
        toponymPathService = ToponymPathService.defaultService();
    }

    @Test
    @DisplayName("GET /toponym-path возвращает 200 для валидного запроса")
    @Description("Проверяет доступность эндпоинта при валидном API-ключе и localityId=106724")
    @Severity(SeverityLevel.BLOCKER)
    void shouldReturn200ForValidRequest() {
        Response response = toponymPathService.getToponymPath(LOCALITY_ID_VALID);

        assertThat(response.statusCode()).isEqualTo(200);
    }

    @Test
    @DisplayName("GET /toponym-path возвращает валидный JSON-контракт")
    @Description("Проверяет, что при localityId=106724 сервис возвращает строку в теле ответа")
    @Severity(SeverityLevel.CRITICAL)
    void shouldReturnValidJsonContract() {
        Response response = toponymPathService.getToponymPath(LOCALITY_ID_VALID);

        assertThat(response.statusCode()).isEqualTo(200);
        String pathValue = response.getBody().asString();
        assertThat(pathValue)
                .as("Тело ответа должно содержать строку")
                .isNotNull();
        assertThat(pathValue.trim())
                .as("Строка в теле ответа не должна быть пустой")
                .isNotEmpty();
    }

    @Test
    @DisplayName("GET /toponym-path без API-ключа отклоняется")
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
        ToponymPathService unauthService = new ToponymPathService(new ToponymPathClient(clientWithoutApiKey));

        Response response = unauthService.getToponymPath(LOCALITY_ID_VALID);

        assertThat(Set.of(401, 403)).contains(response.statusCode());
    }

    @Test
    @DisplayName("GET /toponym-path без обязательного localityId отклоняется")
    @Description("Проверяет, что запрос без обязательного параметра localityId возвращает ошибку валидации")
    @Severity(SeverityLevel.CRITICAL)
    void shouldReturnValidationErrorWithoutLocalityId() {
        Response response = toponymPathService.getToponymPath(null);

        assertThat(Set.of(400, 422)).contains(response.statusCode());
    }
}
