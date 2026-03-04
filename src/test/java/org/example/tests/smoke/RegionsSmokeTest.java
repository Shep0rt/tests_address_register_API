package org.example.tests.smoke;

import io.qameta.allure.Description;
import io.qameta.allure.Owner;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.restassured.response.Response;
import org.example.client.RegionsClient;
import org.example.config.AppConfig;
import org.example.http.ApiClient;
import org.example.http.ApiSpecificationFactory;
import org.example.service.RegionsService;
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
class RegionsSmokeTest {
    private static final String ACCEPT_LANGUAGE_RU = "ru-RU";

    private RegionsService regionsService;

    @BeforeEach
    void setUp() {
        // Инициализируем сервис с авторизацией через X-API-KEY из конфигурации.
        regionsService = RegionsService.defaultService();
    }

    @Test
    @DisplayName("GET /regions возвращает 200")
    @Description("Проверяет доступность эндпоинта /regions с валидным API-ключом")
    @Severity(SeverityLevel.BLOCKER)
    void shouldReturn200ForRegionsRequest() {
        Response response = regionsService.getRegions(null, ACCEPT_LANGUAGE_RU);

        assertThat(response.statusCode()).isEqualTo(200);
    }

    @Test
    @DisplayName("GET /regions возвращает валидный JSON-контракт")
    @Description("Проверяет JSON-ответ на наличие обязательных и заполненных полей city, localityId и regionCode у каждого элемента")
    @Severity(SeverityLevel.CRITICAL)
    void shouldReturnValidContractForRegionsResponse() {
        Response response = regionsService.getRegions(null, ACCEPT_LANGUAGE_RU);

        assertThat(response.statusCode()).isEqualTo(200);
        assertThat(response.getContentType()).contains("application/json");

        List<Map<String, Object>> regions = response.jsonPath().getList("$");
        assertThat(regions).isNotNull().isNotEmpty();

        for (Map<String, Object> region : regions) {
            assertThat(region).containsKeys("city", "localityId", "regionCode");

            assertThat(region.get("city")).as("Поле city не должно быть пустым").isNotNull();
            assertThat(region.get("city").toString().trim()).as("Поле city не должно быть пустой строкой").isNotEmpty();

            assertThat(region.get("regionCode")).as("Поле regionCode не должно быть пустым").isNotNull();
            assertThat(region.get("regionCode").toString().trim()).as("Поле regionCode не должно быть пустой строкой").isNotEmpty();

            assertThat(region.get("localityId"))
                    .as("Поле localityId не должно быть пустым")
                    .isNotNull()
                    .isInstanceOf(Number.class);
            assertThat(((Number) region.get("localityId")).intValue())
                    .as("Поле localityId должно быть положительным числом")
                    .isGreaterThan(0);
        }
    }

    @Test
    @DisplayName("GET /regions без API-ключа отклоняется")
    @Description("Проверяет, что при отсутствии X-API-KEY сервис возвращает код 401 или 403")
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
        RegionsService unauthService = new RegionsService(new RegionsClient(clientWithoutApiKey));

        Response response = unauthService.getRegions(null, ACCEPT_LANGUAGE_RU);

        assertThat(Set.of(401, 403)).contains(response.statusCode());
    }

    @Test
    @DisplayName("GET /regions с фильтром region=Астана возвращает непустой валидный массив")
    @Description("Проверяет, что endpoint корректно обрабатывает параметр region=Астана и возвращает непустой массив с валидными полями")
    @Severity(SeverityLevel.CRITICAL)
    void shouldReturn200ForAstanaRegionFilter() {
        Response response = regionsService.getRegions("Астана", ACCEPT_LANGUAGE_RU);

        assertThat(response.statusCode()).isEqualTo(200);
        assertThat(response.getContentType()).contains("application/json");

        List<Map<String, Object>> regions = response.jsonPath().getList("$");
        assertThat(regions).isNotNull().isNotEmpty();

        for (Map<String, Object> region : regions) {
            assertThat(region).containsKeys("city", "localityId", "regionCode");

            assertThat(region.get("city")).as("Поле city не должно быть пустым").isNotNull();
            assertThat(region.get("city").toString().trim()).as("Поле city не должно быть пустой строкой").isNotEmpty();

            assertThat(region.get("regionCode")).as("Поле regionCode не должно быть пустым").isNotNull();
            assertThat(region.get("regionCode").toString().trim()).as("Поле regionCode не должно быть пустой строкой").isNotEmpty();

            assertThat(region.get("localityId"))
                    .as("Поле localityId не должно быть пустым")
                    .isNotNull()
                    .isInstanceOf(Number.class);
            assertThat(((Number) region.get("localityId")).intValue())
                    .as("Поле localityId должно быть положительным числом")
                    .isGreaterThan(0);
        }
    }
}