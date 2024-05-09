package com.ma1581.Palmer.constants;

import lombok.Data;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Data
@Component
public class Constants {
//    public final static Integer TIMEOUT=10000;
    public static List<String>  REDDIT_SUB_PHONES = new ArrayList<>(List.of( "iphone", "androidphones","PickAnAndroidForMe","suggestasmartphone","PickMeAPhone","phones"));
    public static String RECENT_FETCH_TIMESTAMP ="TIMESTAMP";
    public static long DEFAULT_FETCH_TIMESTAMP =0;

}
