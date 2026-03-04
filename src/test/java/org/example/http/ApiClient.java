package org.example.http;

import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

import java.util.Map;

import static io.restassured.RestAssured.given;

// Универсальный клиент для базовых HTTP-операций.
// Возвращает сырой Response, чтобы тесты выполняли свои доменные проверки.
public class ApiClient {
    private final RequestSpecification requestSpecification;

    public ApiClient(RequestSpecification requestSpecification) {
        this.requestSpecification = requestSpecification;
    }

    public static ApiClient defaultClient() {
        return new ApiClient(ApiSpecificationFactory.createDefault());
    }

    public Response get(String path) {
        return given(requestSpecification)
                .when()
                .get(path)
                .then()
                .extract()
                .response();
    }

    public Response get(String path, Map<String, ?> queryParams) {
        return given(requestSpecification)
                .queryParams(queryParams)
                .when()
                .get(path)
                .then()
                .extract()
                .response();
    }

    public Response get(String path, Map<String, ?> queryParams, Map<String, ?> headers) {
        return given(requestSpecification)
                .headers(headers)
                .queryParams(queryParams)
                .when()
                .get(path)
                .then()
                .extract()
                .response();
    }

    public Response post(String path, Object body) {
        return given(requestSpecification)
                .body(body)
                .when()
                .post(path)
                .then()
                .extract()
                .response();
    }

    public Response put(String path, Object body) {
        return given(requestSpecification)
                .body(body)
                .when()
                .put(path)
                .then()
                .extract()
                .response();
    }

    public Response delete(String path) {
        return given(requestSpecification)
                .when()
                .delete(path)
                .then()
                .extract()
                .response();
    }
}
