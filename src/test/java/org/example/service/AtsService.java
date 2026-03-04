package org.example.service;

import io.restassured.response.Response;
import org.example.client.AtsClient;
import org.example.http.ApiClient;

public class AtsService {
    private final AtsClient atsClient;

    public AtsService(AtsClient atsClient) {
        this.atsClient = atsClient;
    }

    public static AtsService defaultService() {
        return new AtsService(new AtsClient(ApiClient.defaultClient()));
    }

    public Response getAts(String city, String acceptLanguage) {
        return atsClient.getAts(city, acceptLanguage);
    }
}
