package org.example.service;

import io.restassured.response.Response;
import org.example.client.GeonimBuildingsClient;
import org.example.http.ApiClient;

public class GeonimBuildingsService {
    private final GeonimBuildingsClient geonimBuildingsClient;

    public GeonimBuildingsService(GeonimBuildingsClient geonimBuildingsClient) {
        this.geonimBuildingsClient = geonimBuildingsClient;
    }

    public static GeonimBuildingsService defaultService() {
        return new GeonimBuildingsService(new GeonimBuildingsClient(ApiClient.defaultClient()));
    }

    public Response getGeonimBuildings(String geonimId, String number) {
        return geonimBuildingsClient.getGeonimBuildings(geonimId, number);
    }

    public Response getGeonimBuildingsWithoutGeonimId(String number) {
        return geonimBuildingsClient.getGeonimBuildingsWithoutGeonimId(number);
    }
}
