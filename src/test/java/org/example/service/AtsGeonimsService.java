package org.example.service;

import io.restassured.response.Response;
import org.example.client.AtsGeonimsClient;
import org.example.http.ApiClient;

public class AtsGeonimsService {
    private final AtsGeonimsClient atsGeonimsClient;

    public AtsGeonimsService(AtsGeonimsClient atsGeonimsClient) {
        this.atsGeonimsClient = atsGeonimsClient;
    }

    public static AtsGeonimsService defaultService() {
        return new AtsGeonimsService(new AtsGeonimsClient(ApiClient.defaultClient()));
    }

    public Response getAtsGeonims(String atsId, String street) {
        return atsGeonimsClient.getAtsGeonims(atsId, street);
    }

    public Response getAtsGeonimsWithoutAtsId(String street) {
        return atsGeonimsClient.getAtsGeonimsWithoutAtsId(street);
    }
}
