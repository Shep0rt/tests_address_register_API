package org.example.client;

import io.restassured.response.Response;
import org.example.http.ApiClient;

import java.util.HashMap;
import java.util.Map;

public class LocalityClient {
    private static final String LOCALITY_PATH = "/locality";

    private final ApiClient apiClient;

    public LocalityClient(ApiClient apiClient) {
        this.apiClient = apiClient;
    }

    public Response getLocality(String id) {
        Map<String, Object> queryParams = new HashMap<>();
        if (id != null && !id.isBlank()) {
            queryParams.put("id", id);
        }

        return apiClient.get(LOCALITY_PATH, queryParams);
    }
}
