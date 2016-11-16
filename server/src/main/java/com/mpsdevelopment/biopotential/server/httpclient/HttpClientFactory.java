package com.mpsdevelopment.biopotential.server.httpclient;


public class HttpClientFactory {
    private static BioHttpClient bioHttpClient;

    public HttpClientFactory() {
    }

    public static BioHttpClient getInstance() {
        if (HttpClientFactory.bioHttpClient == null) {
            HttpClientFactory.bioHttpClient = new BioHttpClient();
        }
            return bioHttpClient;
    }
}
