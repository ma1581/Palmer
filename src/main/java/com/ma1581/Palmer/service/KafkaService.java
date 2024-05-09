package com.ma1581.Palmer.service;

import com.ma1581.Palmer.models.reddit.RedditData;
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

     public void sendToKafka(RedditData redditData)  {
         sendMessage("test",redditData.toString());
     }

}
