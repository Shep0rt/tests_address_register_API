package org.example.client;

import io.restassured.response.Response;
import org.example.http.ApiClient;

public class AtsByIdClient {
    private static final String ATS_BY_ID_PATH = "/ats/%s";

    private final ApiClient apiClient;

    public AtsByIdClient(ApiClient apiClient) {
        this.apiClient = apiClient;
    }

    public Response getAtsById(String atsId) {
        if (atsId == null || atsId.isBlank()) {
            return apiClient.get("/ats/");
        }

        return apiClient.get(ATS_BY_ID_PATH.formatted(atsId));
    }
}
