package com.ma1581.Palmer.Reddit;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class RedditListing {
    String after;
    int dist;
    String modhash;
    String geoFilter;
    List<RedditChildren> children;
    String before;
}
