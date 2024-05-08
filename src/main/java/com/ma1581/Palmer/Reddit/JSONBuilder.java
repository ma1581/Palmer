package com.ma1581.Palmer.Reddit;

public class JSONBuilder {
    public Object createInstance(String className, String message) {
        try {
            Class<?> clazz = Class.forName("com.ma1581.Palmer.Reddit.RedditData");
            return clazz.getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public  String convertToJSON(String input) {
        StringBuilder jsonBuilder = new StringBuilder("{");

        String[] tokens = input.split(", ");

        for (String token : tokens) {
            String[] keyValue = token.split("=", 2);
            if (keyValue.length == 2) {
                String key = keyValue[0].trim();
                String value = keyValue[1].trim();
                jsonBuilder.append("\"").append(key).append("\":\"").append(value).append("\",");
            }
        }

        // Remove the trailing comma and close the JSON object
        if (jsonBuilder.charAt(jsonBuilder.length() - 1) == ',') {
            jsonBuilder.deleteCharAt(jsonBuilder.length() - 1);
        }
        jsonBuilder.append("}");

        return jsonBuilder.toString();
    }
}
