package org.example.client;

import io.restassured.response.Response;
import org.example.http.ApiClient;

import java.util.HashMap;
import java.util.Map;

public class ToponymsClient {
    private static final String TOPONYMS_PATH = "/toponyms";

    private final ApiClient apiClient;

    public ToponymsClient(ApiClient apiClient) {
        this.apiClient = apiClient;
    }

    public Response getToponyms(String city, String acceptLanguage) {
        Map<String, Object> queryParams = new HashMap<>();
        if (city != null && !city.isBlank()) {
            queryParams.put("city", city);
        }

        Map<String, Object> headers = new HashMap<>();
        if (acceptLanguage != null && !acceptLanguage.isBlank()) {
            headers.put("Accept-Language", acceptLanguage);
        }

        return apiClient.get(TOPONYMS_PATH, queryParams, headers);
    }
}
