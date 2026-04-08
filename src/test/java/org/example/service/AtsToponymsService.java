package org.example.service;

import io.restassured.response.Response;
import org.example.client.AtsToponymsClient;
import org.example.http.ApiClient;

public class AtsToponymsService {
    private final AtsToponymsClient atsToponymsClient;

    public AtsToponymsService(AtsToponymsClient atsToponymsClient) {
        this.atsToponymsClient = atsToponymsClient;
    }

    public static AtsToponymsService defaultService() {
        return new AtsToponymsService(new AtsToponymsClient(ApiClient.defaultClient()));
    }

    public Response getAtsToponyms(String atsId, String city, String acceptLanguage) {
        return atsToponymsClient.getAtsToponyms(atsId, city, acceptLanguage);
    }

    public Response getAtsToponymsWithoutAtsId(String city, String acceptLanguage) {
        return atsToponymsClient.getAtsToponymsWithoutAtsId(city, acceptLanguage);
    }
}
