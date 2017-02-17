package com.mpsdevelopment.biopotential.server.httpclient;


import com.mpsdevelopment.plasticine.commons.logging.Logger;
import com.mpsdevelopment.plasticine.commons.logging.LoggerUtil;

public class HttpClientFactory {

    private static final Logger LOGGER = LoggerUtil.getLogger(HttpClientFactory.class);

    private static BioHttpClient bioHttpClient;

    public HttpClientFactory() {
    }

    public static BioHttpClient getInstance() {
        if (HttpClientFactory.bioHttpClient == null) {
            HttpClientFactory.bioHttpClient = new BioHttpClient();
            LOGGER.info("Create BioHttpClient");

        }
            return bioHttpClient;
    }
}
