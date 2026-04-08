package org.example.service;

import io.restassured.response.Response;
import org.example.client.GeonimGroundsClient;
import org.example.http.ApiClient;

public class GeonimGroundsService {
    private final GeonimGroundsClient geonimGroundsClient;

    public GeonimGroundsService(GeonimGroundsClient geonimGroundsClient) {
        this.geonimGroundsClient = geonimGroundsClient;
    }

    public static GeonimGroundsService defaultService() {
        return new GeonimGroundsService(new GeonimGroundsClient(ApiClient.defaultClient()));
    }

    public Response getGeonimGrounds(String geonimId, String number) {
        return geonimGroundsClient.getGeonimGrounds(geonimId, number);
    }

    public Response getGeonimGroundsWithoutGeonimId(String number) {
        return geonimGroundsClient.getGeonimGroundsWithoutGeonimId(number);
    }
}
