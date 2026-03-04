package org.example.service;

import io.restassured.response.Response;
import org.example.client.CatoInfoClient;
import org.example.http.ApiClient;

public class CatoInfoService {
    private final CatoInfoClient catoInfoClient;

    public CatoInfoService(CatoInfoClient catoInfoClient) {
        this.catoInfoClient = catoInfoClient;
    }

    public static CatoInfoService defaultService() {
        return new CatoInfoService(new CatoInfoClient(ApiClient.defaultClient()));
    }

    public Response getCatoInfo(String cato) {
        return catoInfoClient.getCatoInfo(cato);
    }
}
