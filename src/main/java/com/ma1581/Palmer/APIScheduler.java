package com.ma1581.Palmer;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.ma1581.Palmer.Reddit.RedditBaseResponse;
import com.ma1581.Palmer.Reddit.RedditChildren;
import com.ma1581.Palmer.Reddit.RedditData;
import com.ma1581.Palmer.Reddit.RedditService;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;
import redis.clients.jedis.Jedis;

import java.math.BigInteger;
import java.util.*;

@EnableScheduling
@Component
@Slf4j
public class APIScheduler {

    Jedis redisPool= RedisService.getInstance();

    Gson gson=new Gson();


    List<String> REDDIT_SUB_PHONES = new ArrayList<>(List.of( "iphone", "androidphones","PickAnAndroidForMe","suggestasmartphone","PickMeAPhone","phones"));

    private final RedditService redditService;
    private final KafkaService kafkaService;
    @Autowired
    APIScheduler(RedditService redditService,KafkaService kafkaService){
        this.redditService = redditService;this.kafkaService=kafkaService;
    }

    public void runTask() {
        HashMap response = redditService.getToken().getBody();
    }
    private String TIMESTAMP="TIMESTAMP";
    @Scheduled(fixedRate = 5000)
    public void redditSchedulerGroupOne() throws JsonProcessingException {
        log.info("Running Reddit:1 Scheduler");
        try {
            for (String subs : REDDIT_SUB_PHONES) {
                log.info("Fetching {} data", subs);
                String response = redditService.fetchNewSubPost(subs).getBody();
                log.info(response);
                ObjectMapper mapper = new ObjectMapper();
                RedditBaseResponse redditBaseResponse = mapper.readValue(response, RedditBaseResponse.class);
                List<RedditChildren> redditData= redditBaseResponse.getData().getChildren();

                long firstTimestamp;

                 String timestamp= redisPool.hget(TIMESTAMP, subs);
                 if(Objects.isNull(timestamp)){
                     long defaultTime=9999999999L;
                     redisPool.hset(TIMESTAMP, subs, String.valueOf(defaultTime));
                     timestamp= String.valueOf(defaultTime);
                 }

                long previouslastTimestamp= Long.parseLong(timestamp);

                firstTimestamp=redditData.getFirst().getData().getTimestamp();

                for(RedditChildren redditChildren:redditData){
                    if(redditChildren.getData().getTimestamp()<previouslastTimestamp){
                        log.info("Executing list of "+subs);
                        kafkaService.sendToKafka(redditChildren.getData());
                    }
                    else{
                        break;
                    }
                }
                redisPool.hset(TIMESTAMP, subs, String.valueOf(firstTimestamp));

            }
        } catch (HttpClientErrorException httpClientErrorException) {
            log.info("UnAuthorized :: {}", httpClientErrorException);
        } catch (ResourceAccessException resourceAccessException) {
            log.info("Something went wrong.Please check Internet Connectivity :: {}", resourceAccessException);
        }
    }

}