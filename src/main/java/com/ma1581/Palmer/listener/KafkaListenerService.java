package com.ma1581.Palmer.listener;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ma1581.Palmer.models.reddit.RedditData;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class KafkaListenerService {

//    @KafkaListener(topics = "test", groupId = "test-group")
    public void listen(String message) {
        log.info("Received Message: " + message);
        ObjectMapper mapper = new ObjectMapper();
        try {
            RedditData jsonString = mapper.readValue(message, RedditData.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        // Process the received message as needed
    }
}
