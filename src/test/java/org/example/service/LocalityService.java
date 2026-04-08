package org.example.service;

import io.restassured.response.Response;
import org.example.client.LocalityClient;
import org.example.http.ApiClient;

public class LocalityService {
    private final LocalityClient localityClient;

    public LocalityService(LocalityClient localityClient) {
        this.localityClient = localityClient;
    }

    public static LocalityService defaultService() {
        return new LocalityService(new LocalityClient(ApiClient.defaultClient()));
    }

    public Response getLocality(String id) {
        return localityClient.getLocality(id);
    }
}
