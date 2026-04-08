package org.example.client;

import io.restassured.response.Response;
import org.example.http.ApiClient;

import java.util.HashMap;
import java.util.Map;

public class AtsGeonimsClient {
    private static final String ATS_GEONIMS_PATH = "/ats/%s/geonims";
    private static final String ATS_GEONIMS_WITHOUT_ID_PATH = "/ats/geonims";

    private final ApiClient apiClient;

    public AtsGeonimsClient(ApiClient apiClient) {
        this.apiClient = apiClient;
    }

    public Response getAtsGeonims(String atsId, String street) {
        Map<String, Object> queryParams = new HashMap<>();
        if (street != null && !street.isBlank()) {
            queryParams.put("street", street);
        }

        return apiClient.get(ATS_GEONIMS_PATH.formatted(atsId), queryParams);
    }

    public Response getAtsGeonimsWithoutAtsId(String street) {
        Map<String, Object> queryParams = new HashMap<>();
        if (street != null && !street.isBlank()) {
            queryParams.put("street", street);
        }

        return apiClient.get(ATS_GEONIMS_WITHOUT_ID_PATH, queryParams);
    }
}
