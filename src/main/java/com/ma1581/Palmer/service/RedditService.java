package com.ma1581.Palmer.service;


import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;
import redis.clients.jedis.Jedis;

import java.util.HashMap;
import java.util.Objects;

@Slf4j
@Service
public class RedditService {
    @Value("${reddit.client.secret.id}")
    String REDDIT_AUTH;

    String REDDIT_TOKEN_URL="https://www.reddit.com/api/v1/access_token";

    HttpEntity<MultiValueMap<String, String>> request;
    private RestTemplate restTemplate=new RestTemplate();

    Jedis redisPool= RedisService.getInstance();

    public void createToken(){
        log.info("Creating new Token");
        HashMap response= getToken().getBody();
        redisPool.set("REDDIT_TOKEN", (String) response.get("access_token"));
        redisPool.expire("REDDIT_TOKEN", (Integer) response.get("expires_in"));
    }
    public ResponseEntity<HashMap> getToken() {
        MultiValueMap<String, String> authentication=new LinkedMultiValueMap<>();

        String encodedAuthString = java.util.Base64.getEncoder().encodeToString(REDDIT_AUTH.getBytes());
        HttpHeaders headers = new HttpHeaders();

        headers.add("Authorization", "Basic " + encodedAuthString);
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        authentication.add("grant_type", "client_credentials");

         request = new HttpEntity<>(authentication, headers);

        return restTemplate.exchange(REDDIT_TOKEN_URL,
                HttpMethod.POST,
                request,
                HashMap.class
        );

}
public ResponseEntity<String> fetchNewSubPost(String sub) throws ResourceAccessException, HttpClientErrorException{
    String REDDIT_TOKEN=redisPool.get("REDDIT_TOKEN");
    if(Objects.isNull(REDDIT_TOKEN)){
        createToken();
    }
    HttpHeaders headers = new HttpHeaders();
    MultiValueMap<String, String> authentication=new LinkedMultiValueMap<>();

    headers.add("Authorization", "bearer " +      REDDIT_TOKEN   );
    authentication.add("grant_type", "client_credentials");

    request = new HttpEntity<>(authentication, headers);

    return restTemplate.exchange(
            "https://oauth.reddit.com/r/" + sub + "/new",
            HttpMethod.GET,
            request,
            String.class
    );


}
}

