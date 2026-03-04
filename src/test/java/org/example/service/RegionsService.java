package org.example.service;

import io.restassured.response.Response;
import org.example.client.RegionsClient;
import org.example.http.ApiClient;

// Сервисный слой для сценариев работы с регионами.
public class RegionsService {
    private final RegionsClient regionsClient;

    public RegionsService(RegionsClient regionsClient) {
        this.regionsClient = regionsClient;
    }

    public static RegionsService defaultService() {
        return new RegionsService(new RegionsClient(ApiClient.defaultClient()));
    }

    public Response getRegions(String region, String acceptLanguage) {
        return regionsClient.getRegions(region, acceptLanguage);
    }
}
