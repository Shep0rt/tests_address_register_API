package org.example.client;

import io.restassured.response.Response;
import org.example.http.ApiClient;

import java.util.HashMap;
import java.util.Map;

public class AtsToponymsClient {
    private static final String ATS_TOPONYMS_PATH = "/ats/%s/toponyms";
    private static final String ATS_TOPONYMS_WITHOUT_ID_PATH = "/ats/toponyms";

    private final ApiClient apiClient;

    public AtsToponymsClient(ApiClient apiClient) {
        this.apiClient = apiClient;
    }

    public Response getAtsToponyms(String atsId, String city, String acceptLanguage) {
        Map<String, Object> queryParams = new HashMap<>();
        if (city != null && !city.isBlank()) {
            queryParams.put("city", city);
        }

        Map<String, Object> headers = new HashMap<>();
        if (acceptLanguage != null && !acceptLanguage.isBlank()) {
            headers.put("Accept-Language", acceptLanguage);
        }

        return apiClient.get(ATS_TOPONYMS_PATH.formatted(atsId), queryParams, headers);
    }

    public Response getAtsToponymsWithoutAtsId(String city, String acceptLanguage) {
        Map<String, Object> queryParams = new HashMap<>();
        if (city != null && !city.isBlank()) {
            queryParams.put("city", city);
        }

        Map<String, Object> headers = new HashMap<>();
        if (acceptLanguage != null && !acceptLanguage.isBlank()) {
            headers.put("Accept-Language", acceptLanguage);
        }

        return apiClient.get(ATS_TOPONYMS_WITHOUT_ID_PATH, queryParams, headers);
    }
}
