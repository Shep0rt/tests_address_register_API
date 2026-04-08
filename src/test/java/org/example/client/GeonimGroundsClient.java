package org.example.client;

import io.restassured.response.Response;
import org.example.http.ApiClient;

import java.util.HashMap;
import java.util.Map;

public class GeonimGroundsClient {
    private static final String GEONIM_GROUNDS_PATH = "/geonims/%s/grounds";
    private static final String GEONIM_GROUNDS_WITHOUT_ID_PATH = "/geonims/grounds";

    private final ApiClient apiClient;

    public GeonimGroundsClient(ApiClient apiClient) {
        this.apiClient = apiClient;
    }

    public Response getGeonimGrounds(String geonimId, String number) {
        Map<String, Object> queryParams = new HashMap<>();
        if (number != null && !number.isBlank()) {
            queryParams.put("number", number);
        }

        return apiClient.get(GEONIM_GROUNDS_PATH.formatted(geonimId), queryParams);
    }

    public Response getGeonimGroundsWithoutGeonimId(String number) {
        Map<String, Object> queryParams = new HashMap<>();
        if (number != null && !number.isBlank()) {
            queryParams.put("number", number);
        }

        return apiClient.get(GEONIM_GROUNDS_WITHOUT_ID_PATH, queryParams);
    }
}
