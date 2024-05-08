package com.ma1581.Palmer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ma1581.Palmer.Reddit.RedditData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class KafkaService {

     private final KafkaTemplate<String, String> kafkaTemplate;

     @Autowired
     public KafkaService(KafkaTemplate<String, String> kafkaTemplate) {
          this.kafkaTemplate = kafkaTemplate;
     }

     public void sendMessage(String topic, String message) {
          kafkaTemplate.send(topic, message);
     }

     void sendToKafka(RedditData redditData)  {
         sendMessage("test",redditData.toString());
     }

}
