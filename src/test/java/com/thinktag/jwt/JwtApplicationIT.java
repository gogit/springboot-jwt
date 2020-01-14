package com.thinktag.jwt;

import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {
        JwtApplication.class}, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class JwtApplicationIT {

    @LocalServerPort
    private int port;


    private RestTemplate restTemplate = new RestTemplate();


    @Test
    public void testCreateToken() {
        HttpHeaders headers = new HttpHeaders();
        //headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
        HttpEntity<String> entity = new HttpEntity<String>("parameters", headers);
        Map<String, String> params = new HashMap<String, String>();
        ResponseEntity<String> token =
                this.restTemplate.postForEntity("http://localhost:" + port + "/api/token/create?username=John", entity,
                        String.class, params);
        System.out.println(token.toString());
        Assert.assertEquals(200, token.getStatusCode().value());
        Assert.assertTrue(token.getBody().chars().filter(c -> c == '.').count() == 2);
        Assert.assertTrue(token.getBody().length() > 10);
    }

    @Test
    public void testValidateToken() {
        HttpHeaders headers = new HttpHeaders();
        //headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
        HttpEntity<String> entity = new HttpEntity<String>("parameters", headers);
        Map<String, String> params = new HashMap<String, String>();
        ResponseEntity<String> token =
                restTemplate.postForEntity("http://localhost:" + port + "/api/token/create?username=John", entity,
                        String.class, params);

        ParameterizedTypeReference<HashMap<String, String>> responseType =
                new ParameterizedTypeReference<HashMap<String, String>>() {};

        RequestEntity<Void> request = RequestEntity.post(URI.create("http://localhost:" + port + "/api/token/validate?token="+token.getBody()))
                .accept(MediaType.APPLICATION_JSON).build();

        ResponseEntity<HashMap<String, String>> response = restTemplate.exchange(request, responseType);

        System.out.println(response.toString());
        //{"iss":"thinktag","exp":"1578925506","iat":"1578923706","username":"John"}
        System.out.println(response.toString());
        Assert.assertEquals(200, response.getStatusCode().value());
        Assert.assertTrue(response.getBody().containsKey("iss"));
        Assert.assertTrue(response.getBody().get("iss").equals("thinktag"));
        Assert.assertTrue(response.getBody().get("username").equals("John"));

        long issuedAt = Long.parseLong(response.getBody().get("iat"));
        long expAt = Long.parseLong(response.getBody().get("exp"));
        //check that the token is only valid for 1800 secs mentioned in application-test.properties
        Assert.assertTrue((expAt-issuedAt) <= 1800);
    }

}
