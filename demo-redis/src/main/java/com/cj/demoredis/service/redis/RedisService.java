package com.cj.demoredis.service.redis;

public interface RedisService {

    boolean set(String key,String value);

    String get(String key);

    boolean expire(String key,long expireDate);

    boolean clearRedis(String key);

    boolean hasRedisKey(String key);
}
