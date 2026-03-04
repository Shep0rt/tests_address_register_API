package org.example.client;

import io.restassured.response.Response;
import org.example.http.ApiClient;

import java.util.HashMap;
import java.util.Map;

public class ToponymPathClient {
    private static final String TOPONYM_PATH = "/toponym-path";

    private final ApiClient apiClient;

    public ToponymPathClient(ApiClient apiClient) {
        this.apiClient = apiClient;
    }

    public Response getToponymPath(String localityId) {
        Map<String, Object> queryParams = new HashMap<>();
        if (localityId != null && !localityId.isBlank()) {
            queryParams.put("localityId", localityId);
        }

        return apiClient.get(TOPONYM_PATH, queryParams);
    }
}
