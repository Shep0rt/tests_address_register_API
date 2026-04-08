package org.example.client;

import io.restassured.response.Response;
import org.example.http.ApiClient;

import java.util.HashMap;
import java.util.Map;

public class GeonimBuildingsClient {
    private static final String GEONIM_BUILDINGS_PATH = "/geonims/%s/buildings";
    private static final String GEONIM_BUILDINGS_WITHOUT_ID_PATH = "/geonims/buildings";

    private final ApiClient apiClient;

    public GeonimBuildingsClient(ApiClient apiClient) {
        this.apiClient = apiClient;
    }

    public Response getGeonimBuildings(String geonimId, String number) {
        Map<String, Object> queryParams = new HashMap<>();
        if (number != null && !number.isBlank()) {
            queryParams.put("number", number);
        }

        return apiClient.get(GEONIM_BUILDINGS_PATH.formatted(geonimId), queryParams);
    }

    public Response getGeonimBuildingsWithoutGeonimId(String number) {
        Map<String, Object> queryParams = new HashMap<>();
        if (number != null && !number.isBlank()) {
            queryParams.put("number", number);
        }

        return apiClient.get(GEONIM_BUILDINGS_WITHOUT_ID_PATH, queryParams);
    }
}
