package com.ma1581.Palmer.service;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

public class RedisService {

    String LAST_UPDATE_TIME;

    static JedisPoolConfig poolConfig = new JedisPoolConfig();
    static JedisPool jedisPool = new JedisPool(poolConfig, "localhost", 6379);
    static Jedis jedis = jedisPool.getResource();
    public static Jedis getInstance(){
        return jedis;
    }
}
