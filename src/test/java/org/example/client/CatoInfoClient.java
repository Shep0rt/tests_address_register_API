package org.example.client;

import io.restassured.response.Response;
import org.example.http.ApiClient;

import java.util.HashMap;
import java.util.Map;

public class CatoInfoClient {
    private static final String CATO_INFO_PATH = "/cato-info";

    private final ApiClient apiClient;

    public CatoInfoClient(ApiClient apiClient) {
        this.apiClient = apiClient;
    }

    public Response getCatoInfo(String cato) {
        Map<String, Object> queryParams = new HashMap<>();
        if (cato != null && !cato.isBlank()) {
            queryParams.put("cato", cato);
        }

        return apiClient.get(CATO_INFO_PATH, queryParams);
    }
}
