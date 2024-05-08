package com.ma1581.Palmer.Reddit;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.io.Serializable;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class RedditData implements Serializable {
    String author;
    String title;
    @JsonProperty("selftext")
    String content;
    @JsonProperty("created_utc")
    long timestamp;
    String id;
    String kind;
}
