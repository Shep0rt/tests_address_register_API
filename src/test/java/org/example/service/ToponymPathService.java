package org.example.service;

import io.restassured.response.Response;
import org.example.client.ToponymPathClient;
import org.example.http.ApiClient;

public class ToponymPathService {
    private final ToponymPathClient toponymPathClient;

    public ToponymPathService(ToponymPathClient toponymPathClient) {
        this.toponymPathClient = toponymPathClient;
    }

    public static ToponymPathService defaultService() {
        return new ToponymPathService(new ToponymPathClient(ApiClient.defaultClient()));
    }

    public Response getToponymPath(String localityId) {
        return toponymPathClient.getToponymPath(localityId);
    }
}
