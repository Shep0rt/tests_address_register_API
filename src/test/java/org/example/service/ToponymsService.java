package org.example.service;

import io.restassured.response.Response;
import org.example.client.ToponymsClient;
import org.example.http.ApiClient;

public class ToponymsService {
    private final ToponymsClient toponymsClient;

    public ToponymsService(ToponymsClient toponymsClient) {
        this.toponymsClient = toponymsClient;
    }

    public static ToponymsService defaultService() {
        return new ToponymsService(new ToponymsClient(ApiClient.defaultClient()));
    }

    public Response getToponyms(String city, String acceptLanguage) {
        return toponymsClient.getToponyms(city, acceptLanguage);
    }
}
