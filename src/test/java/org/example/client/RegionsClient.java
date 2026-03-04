package org.example.client;

import io.restassured.response.Response;
import org.example.http.ApiClient;

import java.util.HashMap;
import java.util.Map;

// Клиент эндпоинтов, связанных с регионами.
public class RegionsClient {
    private static final String REGIONS_PATH = "/regions";

    private final ApiClient apiClient;

    public RegionsClient(ApiClient apiClient) {
        this.apiClient = apiClient;
    }

    public Response getRegions(String region, String acceptLanguage) {
        Map<String, Object> queryParams = new HashMap<>();
        if (region != null && !region.isBlank()) {
            queryParams.put("region", region);
        }

        Map<String, Object> headers = new HashMap<>();
        if (acceptLanguage != null && !acceptLanguage.isBlank()) {
            headers.put("Accept-Language", acceptLanguage);
        }

        return apiClient.get(REGIONS_PATH, queryParams, headers);
    }
}
