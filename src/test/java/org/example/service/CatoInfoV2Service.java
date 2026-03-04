package org.example.service;

import io.restassured.response.Response;
import org.example.client.CatoInfoV2Client;
import org.example.http.ApiClient;

public class CatoInfoV2Service {
    private final CatoInfoV2Client catoInfoV2Client;

    public CatoInfoV2Service(CatoInfoV2Client catoInfoV2Client) {
        this.catoInfoV2Client = catoInfoV2Client;
    }

    public static CatoInfoV2Service defaultService() {
        return new CatoInfoV2Service(new CatoInfoV2Client(ApiClient.defaultClient()));
    }

    public Response getCatoInfoV2(String cato) {
        return catoInfoV2Client.getCatoInfoV2(cato);
    }
}
