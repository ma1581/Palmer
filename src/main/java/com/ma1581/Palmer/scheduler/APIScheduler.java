package com.ma1581.Palmer.scheduler;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ma1581.Palmer.models.reddit.RedditBaseResponse;
import com.ma1581.Palmer.models.reddit.RedditChildren;
import com.ma1581.Palmer.service.KafkaService;
import com.ma1581.Palmer.service.RedditService;
import com.ma1581.Palmer.service.RedisService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;
import redis.clients.jedis.Jedis;

import java.util.*;

import static com.ma1581.Palmer.constants.Constants.*;

@EnableScheduling
@Component
@Slf4j
public class APIScheduler {

    Jedis redisPool= RedisService.getInstance();

    private final RedditService redditService;

    private final KafkaService kafkaService;

    @Autowired
    APIScheduler(RedditService redditService,KafkaService kafkaService){
        this.redditService = redditService;this.kafkaService=kafkaService;
    }

    @Scheduled(fixedRate = 5000)
    public void redditSchedulerGroupOne() throws JsonProcessingException {
        log.info("Running Reddit:1 Scheduler");
        try {
            for (String sub : REDDIT_SUB_PHONES) {
                log.info("Fetching {} data", sub);
                String response = redditService.fetchNewSubPost(sub).getBody();
                log.info(response);
                ObjectMapper mapper = new ObjectMapper();
                RedditBaseResponse redditBaseResponse = mapper.readValue(response, RedditBaseResponse.class);
                List<RedditChildren> redditData= redditBaseResponse.getData().getChildren();

                long currentFetchTimestamp;

                 String recentFetchTimestamp= redisPool.hget(RECENT_FETCH_TIMESTAMP, sub);
                 if(Objects.isNull(recentFetchTimestamp)) {
                     recentFetchTimestamp = String.valueOf(DEFAULT_FETCH_TIMESTAMP);
                 }

                currentFetchTimestamp=redditData.getFirst().getData().getTimestamp();
                redisPool.hset(RECENT_FETCH_TIMESTAMP, sub, String.valueOf(currentFetchTimestamp));
                for(RedditChildren redditChildren:redditData){
                    if(redditChildren.getData().getTimestamp()>Long.parseLong(recentFetchTimestamp)){
                        log.info("Sending to Kafka..."+sub);
                        kafkaService.sendToKafka(redditChildren.getData());
                    }
                    else{
                        log.info("Stopping message to Kafka..."+sub);
                        break;
                    }
                }

            }
        } catch (HttpClientErrorException httpClientErrorException) {
            log.info("UnAuthorized :: {}", httpClientErrorException);
        } catch (ResourceAccessException resourceAccessException) {
            log.info("Something went wrong.Please check Internet Connectivity :: {}", resourceAccessException);
        }
        finally {

        }
    }

}