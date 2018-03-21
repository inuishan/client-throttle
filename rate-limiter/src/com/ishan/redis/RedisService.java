package com.ishan.redis;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Preconditions;
import com.ishan.base.ExceptionUtils;
import org.apache.commons.lang3.StringUtils;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.Protocol;
import redis.clients.jedis.exceptions.JedisConnectionException;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * @author ishanjain
 * @since 21/03/18
 */
public class RedisService {

    /**
     * Ideally this could be in a properties file or this could be read from a central store or could be an
     * initialization param. Hardcoding it for now.
     */
    private static final String HOST = "sample.redis.host";
    private static final int PORT = 7789;

    private static final JedisPool JEDIS_POOL;
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    private static final int MAX_RETRIES = 3;

    static {
        JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
        jedisPoolConfig.setMaxTotal(100);
        jedisPoolConfig.setMaxIdle(20);
        jedisPoolConfig.setBlockWhenExhausted(true);
        jedisPoolConfig.setMinIdle(5);
        //jedisPool = new JedisPool(jedisPoolConfig, HOST, PORT);
        JEDIS_POOL = new JedisPool(jedisPoolConfig, HOST, PORT, Protocol.DEFAULT_TIMEOUT,
                (int) TimeUnit.SECONDS.toMillis(5), null, Protocol.DEFAULT_DATABASE, null, false, null, null, null);
    }

    public static <T> T get(String key, Class<T> valueClass) {
        Preconditions.checkNotNull(key, "Key cannot be blank");


        String s = null;
        for (int i = 1; i <= MAX_RETRIES; i++) {
            try (Jedis jedis = JEDIS_POOL.getResource()) {
                s = jedis.get(key);
            } catch (JedisConnectionException e) {
                if (i == MAX_RETRIES) {
                    throw e;
                }
                continue;
            }
            break;
        }

        if (StringUtils.isBlank(s)) {
            return null;
        }
        try {
            return OBJECT_MAPPER.readValue(s, valueClass);
        } catch (IOException e) {
            throw ExceptionUtils.wrapInRuntimeExceptionIfNecessary(e);
        }
    }
}