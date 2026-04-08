package org.example.service;

import io.restassured.response.Response;
import org.example.client.AtsByIdClient;
import org.example.http.ApiClient;

public class AtsByIdService {
    private final AtsByIdClient atsByIdClient;

    public AtsByIdService(AtsByIdClient atsByIdClient) {
        this.atsByIdClient = atsByIdClient;
    }

    public static AtsByIdService defaultService() {
        return new AtsByIdService(new AtsByIdClient(ApiClient.defaultClient()));
    }

    public Response getAtsById(String atsId) {
        return atsByIdClient.getAtsById(atsId);
    }
}
