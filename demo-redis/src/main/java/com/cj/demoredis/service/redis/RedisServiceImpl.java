package com.cj.demoredis.service.redis;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * @author chen
 */
@Service
public class RedisServiceImpl implements RedisService {

    @Autowired
    private RedisTemplate redisTemplate;

    @Override
    public boolean set(String key, String value) {
        return (boolean) redisTemplate.execute(new RedisCallback() {
            @Override
            public Boolean doInRedis(RedisConnection redisConnection) throws DataAccessException {
                RedisSerializer stringSerializer = redisTemplate.getStringSerializer();
                return redisConnection.set(stringSerializer.serialize(key), stringSerializer.serialize(value));
            }
        });
    }

    @Override
    public String get(String key) {
        return (String) redisTemplate.execute(new RedisCallback() {
            @Override
            public String doInRedis(RedisConnection redisConnection) throws DataAccessException {
                RedisSerializer<String> stringSerializer = redisTemplate.getStringSerializer();
                byte[] serialize = redisConnection.get(stringSerializer.serialize(key));
                return stringSerializer.deserialize(serialize);
            }
        });
    }

    @Override
    public boolean expire(String key, long expireDate) {
        return redisTemplate.expire(key, expireDate, TimeUnit.MICROSECONDS);
    }

    @Override
    public boolean clearRedis(String key) {
        if (StringUtils.isBlank(key) || !hasRedisKey(key)) {
            return false;
        }
        Object execute = redisTemplate.execute((RedisCallback) redisConnection -> {
            RedisSerializer stringSerializer = redisTemplate.getStringSerializer();
            byte[] serialize = stringSerializer.serialize(key);
            return redisConnection.del(serialize);
        });
        return "1".equals(Objects.toString(execute));
    }

    @Override
    public boolean hasRedisKey(String key) {
        if (StringUtils.isEmpty(key)) {
            return false;
        }
        return (boolean) redisTemplate.execute(new RedisCallback() {
            @Override
            public Boolean doInRedis(RedisConnection redisConnection) throws DataAccessException {
                RedisSerializer stringSerializer = redisTemplate.getStringSerializer();
                byte[] serialize = stringSerializer.serialize(key);
                return redisConnection.exists(serialize);
            }
        });
    }
}
