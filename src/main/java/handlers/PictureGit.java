package handlers;

import services.MyLogger;
import org.apache.hc.client5.http.cookie.BasicCookieStore;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.springframework.http.*;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.io.*;

public class PictureGit {
    private String gitAddress;
    private String login;
    private String password;
    private RestTemplate restTemplate;
    private CloseableHttpClient httpClient;
    private BasicCookieStore cookieStore;
    private HttpComponentsClientHttpRequestFactory requestFactory;

    public PictureGit(String gitAddress, String login, String password) {
        this.cookieStore = new BasicCookieStore();
        this.httpClient = HttpClients.custom()
                .setDefaultCookieStore(this.cookieStore)
                .build();
        this.requestFactory = new HttpComponentsClientHttpRequestFactory(httpClient);
        this.restTemplate = new RestTemplate(requestFactory);
        this.gitAddress = gitAddress;
        this.login = login;
        this.password = password;
    }

    public boolean checkConnection() {
        if (!login()) {
            return false;
        }
        MyLogger.myInfo("Connection OK — session established");
        return true;
    }

    private boolean login() {
        MultiValueMap<String, String> loginData = new LinkedMultiValueMap<>();
        loginData.add("user_name", login);
        loginData.add("password", password);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(loginData, headers);

        try {
            ResponseEntity<String> response = restTemplate.postForEntity(
                    gitAddress + "/user/login", requestEntity, String.class);

            if (response.getStatusCode() == HttpStatus.OK && cookieStore.getCookies().size() >= 2) {
                MyLogger.myInfo("Session established");
                return true;
            }
        } catch (Exception e) {
            MyLogger.myError("Login failed: " + e.getMessage());
        }
        return false;
    }

    public InputStream getImageStream(String url) {
        ResponseEntity<byte[]> response = null;
        try {
            response = restTemplate.getForEntity(url, byte[].class);
        } catch (HttpClientErrorException e) {
            int statusCode = e.getStatusCode().value();
            if (statusCode == 401 || statusCode == 403) {
                MyLogger.myInfo("Session expired, re-logging...");
                if (login()) {
                    try {
                        response = restTemplate.getForEntity(url, byte[].class);
                    } catch (HttpClientErrorException e2) {
                        MyLogger.myError("Still failed after re-login: " + e2.getMessage());
                        return null;
                    }
                } else {
                    MyLogger.myError("Re-login failed");
                    return null;
                }
            } else {
                return null;
            }
        }

        if (response != null && response.getStatusCode() == HttpStatus.OK) {
            FileHandler fh = new FileHandler();
            return fh.saveAndReturnStream(response.getBody());
        }
        return null;
    }
}
