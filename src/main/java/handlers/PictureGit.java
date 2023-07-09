package handlers;

import org.apache.hc.client5.http.cookie.BasicCookieStore;
import org.apache.hc.client5.http.cookie.Cookie;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.springframework.http.*;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.io.*;

/**
 * Class for getting pictures. When the program receives a post-request with a JSON message in the body of this message,
 * there may be pictures in the form of links "/some/address/image.jpg"
 * To get these pictures, the address must be converted to the form "server://some/address/image.jpg".
 * Also, to send a request, you must have access, so first the authorization occurs, then the request to receive a picture.
 * This class actively uses the springframework, but the cookie manager is standard.
 *
 * <p>Author: Andrew Kantser</p>
 * <p>Date: 2023-07-03</p>
 *
 */
public class PictureGit {
    private String gitAddress;
    private String login;
    private String password;
    private RestTemplate restTemplate;

    private CloseableHttpClient httpClient;

    private BasicCookieStore basicCookieStore;

    private HttpComponentsClientHttpRequestFactory requestFactory;

    /**
     * To check authorization on the site (and correct operation), the site has a picture
     * with a known size, which is available only to this bot.
     * The method receives this picture and checks its size.
     *
     * @param gitAddress String i.e. http://192.168.1.162:3000
     * @param login String login.
     * @param password String password.
     * @throws ExceptionInInitializerError An initialization exception will be thrown for multiple reasons.
     *  - Unsuccessful interaction with the file system.
     *  - Wrong size image received.
     *  - Wrong login/password/address
     *
     */
    public PictureGit(String gitAddress, String login, String password) throws ExceptionInInitializerError{
        this.basicCookieStore = new BasicCookieStore();
        this.httpClient = HttpClients.custom()
                .setDefaultCookieStore(this.basicCookieStore)
                .build();

        // Создаем HttpComponentsClientHttpRequestFactory и используем ранее созданный httpClient
        this.requestFactory = new HttpComponentsClientHttpRequestFactory(httpClient);

        // Настраиваем RestTemplate для использования requestFactory
        this.restTemplate = new RestTemplate(requestFactory);

        this.gitAddress = gitAddress;
        this.login = login;
        this.password = password;
    }

    /**
     * To check authorization on the site (and correct operation), the site has a picture
     * with a known size, which is available only to this bot.
     * The method receives this picture and checks its size.
     * @return true if all right.
     *
     */
    public boolean checkConnection() {
        String urlPictureString = gitAddress + "/attachments/2264b68c-6547-46eb-a88e-52de52d9e938";

        MultiValueMap<String, String> loginData = new LinkedMultiValueMap<>();
        loginData.add("user_name", login);
        loginData.add("password", password);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        System.out.println("Header\n" + headers);

        HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(loginData, headers);

        ResponseEntity<String> responseEntity = this.restTemplate.postForEntity(gitAddress + "/user/login", requestEntity, String.class);


        System.out.println("TestConnect " + gitAddress + "/user/login");
        if (responseEntity.getStatusCode() == HttpStatus.OK) {
            System.out.println("200");
            //System.out.println(responseEntity);
        } else {
            throw new ExceptionInInitializerError("Cant found site");
        }

        int cookieCount = 0;
        for (Cookie arg : this.basicCookieStore.getCookies()) {
            System.out.println(arg);
            cookieCount++;
        }

        if (cookieCount < 2) {
            System.out.println("TestConnect " + gitAddress + "/user/login");
            throw new ExceptionInInitializerError("Cookie not found");
        }

        // Выполнение запроса на загрузку тестовой картинки
        ResponseEntity<byte[]> responseEntity2 = this.restTemplate.getForEntity(urlPictureString, byte[].class);
        HttpStatusCode statusCode = responseEntity2.getStatusCode();

        if (statusCode == HttpStatus.OK) {
            byte[] imageBytes = responseEntity2.getBody();
            FileHandler fh = new FileHandler();
            fh.saveAndCheckSize(imageBytes, 70000);
        } else {
            throw new ExceptionInInitializerError("Can't load test image");
        }

        return true;
    }

    /**
     * Gets the image at the specified address. (From the list of attachments in the message)
     * @return  a stream (or null)
     *
     */
    public InputStream getImageStream(String url) {
        HttpStatus statusCode;
        ResponseEntity<byte[]> responseEntityImg = null;
        try {
            responseEntityImg = restTemplate.getForEntity(url, byte[].class);
            statusCode = (HttpStatus) responseEntityImg.getStatusCode();
        } catch (HttpClientErrorException e) {
            statusCode = HttpStatus.NOT_FOUND;
        }

        if (statusCode == HttpStatus.OK) {
            FileHandler fh = new FileHandler();
            return fh.saveAndReturnStream(responseEntityImg.getBody());
        } else {
            System.out.println("Can`t load test image");
            return null;
        }

    }
}
