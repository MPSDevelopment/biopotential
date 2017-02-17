package com.mpsdevelopment.biopotential.server.httpclient;

import com.mpsdevelopment.biopotential.server.cmp.analyzer.AnalysisSummary;
import com.mpsdevelopment.biopotential.server.cmp.machine.Pattern;
import com.mpsdevelopment.biopotential.server.settings.ServerSettings;
import com.mpsdevelopment.biopotential.server.utils.JsonUtils;
import com.mpsdevelopment.plasticine.commons.logging.Logger;
import com.mpsdevelopment.plasticine.commons.logging.LoggerUtil;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.CharEncoding;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.CookieStore;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.cookie.Cookie;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Configuration
@ComponentScan
@EnableAutoConfiguration
public class BioHttpClient {

    @Autowired
    private ServerSettings serverSettings;

    private static final Logger LOGGER = LoggerUtil.getLogger(BioHttpClient.class);

    private static final String ENCODING_NAME = "UTF-8";
    private static final String CONTENT_TYPE_NAME = "application/json;charset=UTF-8";

    private DefaultHttpClient httpClient;
    private String mainUrl;

    public BioHttpClient() {
        //LOGGER.info("  Start creating DEVICES HTTP CLIENT  settings = %s",settings);

        DefaultHttpClient client = new DefaultHttpClient();
        ClientConnectionManager mgr = client.getConnectionManager();
        HttpParams params = client.getParams();
        HttpProtocolParams.setContentCharset(params, "UTF-8");
        httpClient = new DefaultHttpClient(mgr, params);
        LOGGER.info("DEVICES HTTP CLIENT is %s ", httpClient);
        mainUrl = String.format("http://%s:%s", "localhost", 8098);
    }

    /*public String connectAsAdmin(String host, int port) {
        String json = null;
        if (host != null && port != 0 && StringUtils.isBlank(getToken())) {
            LOGGER.info("  Connect to host = %s port = %s", host, port);
            LOGGER.info("  TOKEN FROM HTTP CLIENT = %s ", getToken());
            mainUrl = String.format("http://%s:%s", host, port);

            String url = String.format("%s%s", mainUrl, ControllerAPI.USER_CONTROLLER_POST_LOGIN_REQUEST);
            String body = JsonUtils.getJson(new SecurityProperties.User().setLogin("admin").setPassword("admin"));
            json = executePostRequest(url, body);
        }
        return json;
    }*/

    public DefaultHttpClient getHttpClient() {
        return httpClient;
    }

    public Cookie getCookie() {
        return httpClient.getCookieStore().getCookies().get(0);
    }

    public String getToken() {
        String token = "";
        if (httpClient != null) {
            LOGGER.info(" HTTP CLIENT = %s ", httpClient);
            List<Cookie> cookieList = httpClient.getCookieStore().getCookies();
            if (!cookieList.isEmpty()) {
                token = cookieList.get(0).getValue();
            }
        }
        return token;
    }

    public String executeGetRequest(String uri) {
        HttpGet request = new HttpGet(uri);
        String json = null;
        HttpResponse response;
        try {
            response = httpClient.execute(request);
            json = getContextResponse(response);
            LOGGER.debug(" New Request JSON - %s", json);
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return json;
    }

    public byte[] executeGetBytesRequest(String uri) {
        HttpGet request = new HttpGet(uri);
        byte[] answer = null;
        HttpResponse response;
        try {
            response = httpClient.execute(request);
            answer = getContextAsByteArrayResponse(response);
            LOGGER.debug(" New Request array length - %s", answer.length);
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return answer;
    }

    /*public String executePostRequest(String uri, File file) {

        LOGGER.info(" POST Request  - %s", file.getName());

        String url = String.format("%s%s", mainUrl, uri);
        HttpPost request = new HttpPost(url);
        String json = null;
        HttpResponse response = null;
        if (file != null) {
            try {
                FileBody bin = new FileBody(file, "application/octet-stream");
                StringBody comment = new StringBody("A binary file of some kind", ContentType.TEXT_PLAIN);
                HttpEntity reqEntity = MultipartEntityBuilder.create().addPart("file", bin).addPart("comment", comment).build();
                request.setEntity(reqEntity);
                response = httpClient.execute(request);
                json = getContextResponse(response);
                LOGGER.info(" POST RESPONSE JSON - %s", json);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (ClientProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return json;
    }*/

    public String executePostRequest(String uri, String body) {
        String url = String.format("%s%s", mainUrl, uri);
        LOGGER.info("Url - %s", url);
        HttpPost request = new HttpPost(url);
        String json = null;
        HttpResponse response;
        if (StringUtils.isNotBlank(body)) {
            StringEntity entity;
            try {
                entity = new StringEntity(body, ENCODING_NAME);
                entity.setContentType(CONTENT_TYPE_NAME);
                request.setEntity(entity);
                response = httpClient.execute(request);
                json = getContextResponse(response);
                LOGGER.debug(" POST Request JSON - %s", json);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (ClientProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return json;
    }

    public String executePostRequest(String uri, File file, String body) {
        String url = String.format("%s%s", mainUrl, uri);
        LOGGER.info("Url - %s", url);
        HttpPost request = new HttpPost(url);
        String json = null;
        HttpResponse response;
        LOGGER.info(" POST Request  - %s", file.getName());

        HttpEntity entity = MultipartEntityBuilder
                .create()
                .addBinaryBody("file", file, ContentType.create("application/octet-stream"), file.getName())
                .addTextBody(body,CONTENT_TYPE_NAME)
                .build();

        request.setEntity(entity);
        try {
            response = httpClient.execute(request);
            json = getContextResponse(response);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return json;
    }

    public String executePostRequest(String uri, File file) {

        LOGGER.info(" POST Request  - %s", file.getName());

        String url = String.format("%s%s", mainUrl, uri);
        HttpPost request = new HttpPost(url);
        String json = null;
        HttpResponse response;
        if (file != null) {
            try {
                FileBody bin = new FileBody(file, "audio/wav;"+Charset.forName( "UTF-8" )/*"text/html; charset=utf-8"*//*ContentType.create("audio/wav", CharEncoding.UTF_8)*/);
                StringBody comment = new StringBody("A binary file of some kind", ContentType.TEXT_PLAIN);
                HttpEntity reqEntity = MultipartEntityBuilder.create().addPart("file", bin)/*.setContentType(ContentType.APPLICATION_FORM_URLENCODED)*/.addPart("comment", comment).build();
                request.setEntity(reqEntity);

                response = httpClient.execute(request);
                json = getContextResponse(response);
                LOGGER.info(" POST RESPONSE JSON - %s", json);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return json;
    }


    public String executePostRequest(String uri, String body, Map<String, String> params) {
        HttpPost request = new HttpPost(uri);
        String json = null;
        HttpResponse response;
        ArrayList<NameValuePair> postParameters;
        if (StringUtils.isNotBlank(body)) {
        UrlEncodedFormEntity entity;
        try {
            postParameters = new ArrayList<>();
            for (String key : params.keySet()) {
                postParameters.add(new BasicNameValuePair(key, params.get(key)));
            }
            entity = new UrlEncodedFormEntity(postParameters, ENCODING_NAME);
            request.setEntity(entity);
            response = httpClient.execute(request);
            json = getContextResponse(response);
            LOGGER.debug(" POST Request JSON - %s", json);
        } catch (IOException e) {
            e.printStackTrace();
        }
        }
        return json;
    }

    public String executePostRequest(String uri, Map<Pattern, AnalysisSummary> params) {
        String url = String.format("%s%s", mainUrl, uri);
        HttpPost request = new HttpPost(url);
        String json = null;
        HttpResponse response;
        ArrayList<NameValuePair> postParameters;
//        if (StringUtils.isNotBlank(body)) {
        UrlEncodedFormEntity entity;
        try {
            postParameters = new ArrayList<>();
            for (Pattern key : params.keySet()) {
                String keyString = JsonUtils.getJson(key);
                String valueString = JsonUtils.getJson(params.get(key));
                postParameters.add(new BasicNameValuePair(keyString, valueString));
            }
            entity = new UrlEncodedFormEntity(postParameters, ENCODING_NAME);
            request.setEntity(entity);
            response = httpClient.execute(request);
            json = getContextResponse(response);
            LOGGER.debug(" POST Request JSON - %s", json);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
//        }
        return json;
    }

    public String executePutRequest(String uri, String body) {
        String url = String.format("%s%s", mainUrl, uri);
        HttpPut request = new HttpPut(url);
//        HttpPut request = new HttpPut(uri);
        String json = null;
        HttpResponse response = null;
        if (StringUtils.isNotBlank(body)) {
            StringEntity entity;
            try {
                entity = new StringEntity(body, ENCODING_NAME);
                entity.setContentType(CONTENT_TYPE_NAME);
                request.setEntity(entity);
                response = httpClient.execute(request);
                json = getContextResponse(response);
                LOGGER.debug(" PUT Request JSON - %s", json);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (ClientProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return json;
    }

    public String executeDeleteRequest(String uri) {
        String url = String.format("%s%s", mainUrl, uri);
        HttpDelete request = new HttpDelete(url);
        String json = null;
        HttpResponse response;
        try {
            response = httpClient.execute(request);
            json = getContextResponse(response);
            LOGGER.debug(" Delete responce JSON - %s", json);
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return json;
    }

    private String getContextResponse(HttpResponse response) {
        StringWriter writer = new StringWriter();
        try {
            IOUtils.copy(response.getEntity().getContent(), writer, "UTF-8");
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return writer.toString();
    }

    private byte[] getContextAsByteArrayResponse(HttpResponse response) {
        byte[] answer = null;
        try {
            answer = IOUtils.toByteArray(response.getEntity().getContent());
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return answer;
    }

    /*private HttpStatus postObject(String url, Object object) throws URIException, UnsupportedEncodingException, IOException, HttpException {
        String fullUrl = String.format("http://%s:%s%s", serverSettings.getHost(), serverSettings.getPort(), url);
        LOGGER.info("Full url is %s", fullUrl);
        PostMethod method = new PostMethod();
        method.setURI(new URI(fullUrl, false, ENCODING_NAME));
        method.setRequestEntity(new StringRequestEntity(JsonUtils.getJson(object), CONTENT_TYPE_NAME, ENCODING_NAME));
        try {
            return HttpStatus.valueOf(httpClient.executeMethod(method));
        } finally {
            method.releaseConnection();
        }
    }*/
}