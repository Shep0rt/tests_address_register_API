package org.example.http;

import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.config.HttpClientConfig;
import io.restassured.config.RestAssuredConfig;
import io.restassured.filter.log.LogDetail;
import io.restassured.specification.RequestSpecification;
import org.example.config.AppConfig;

import static io.restassured.config.RestAssuredConfig.config;

// Фабрика общей HTTP-спецификации для всех API-тестов.
public final class ApiSpecificationFactory {
    private ApiSpecificationFactory() {
    }

    public static RequestSpecification createDefault() {
        return create(AppConfig.load());
    }

    public static RequestSpecification create(AppConfig appConfig) {
        // Настройка таймаутов HTTP-клиента Rest Assured.
        RestAssuredConfig restAssuredConfig = config().httpClient(
                HttpClientConfig.httpClientConfig()
                        .setParam("http.connection.timeout", appConfig.connectTimeoutMs())
                        .setParam("http.socket.timeout", appConfig.readTimeoutMs())
                        .setParam("http.connection-manager.timeout", (long) appConfig.readTimeoutMs())
        );

        // Базовая спецификация: baseUri, JSON-заголовки, интеграция с Allure.
        RequestSpecBuilder builder = new RequestSpecBuilder()
                .setBaseUri(appConfig.baseUrl())
                .setRelaxedHTTPSValidation()
                .setConfig(restAssuredConfig)
                .setContentType("application/json")
                .setAccept("application/json")
                .log(LogDetail.URI)
                .addFilter(new AllureRestAssured());

        // Если API-ключ задан, автоматически добавляем X-API-KEY для всех запросов.
        if (!appConfig.apiKey().isBlank()) {
            builder.addHeader("X-API-KEY", appConfig.apiKey());
        }

        return builder.build();
    }
}
