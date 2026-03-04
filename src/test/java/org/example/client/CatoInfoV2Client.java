package org.example.client;

import io.restassured.response.Response;
import org.example.http.ApiClient;

import java.util.HashMap;
import java.util.Map;

public class CatoInfoV2Client {
    private static final String CATO_INFO_V2_PATH = "/cato-info-v2";

    private final ApiClient apiClient;

    public CatoInfoV2Client(ApiClient apiClient) {
        this.apiClient = apiClient;
    }

    public Response getCatoInfoV2(String cato) {
        Map<String, Object> queryParams = new HashMap<>();
        if (cato != null && !cato.isBlank()) {
            queryParams.put("cato", cato);
        }

        return apiClient.get(CATO_INFO_V2_PATH, queryParams);
    }
}
