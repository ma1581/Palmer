package com.ma1581.Palmer.Reddit;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class RedditChildren {
    String kind;
    RedditData data;
}
