package com.ishan.base;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.util.concurrent.Striped;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.Protocol;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;

/**
 * Provides the config for a client
 *
 * @author ishanjain
 * @since 21/03/18
 */
public class ClientConfigProvider {

    private static final Striped<Lock> striped = Striped.lock(50);
    private static final Cache<String, ClientConfig> clientIdVsClientConfig = CacheBuilder.newBuilder().build();


    public static ClientConfig getClientConfig(String clientId) {
        ClientConfig clientConfig = clientIdVsClientConfig.getIfPresent(clientId);
        if (clientConfig == null) {
            Lock lock = striped.get(clientId);
            boolean acquired = tryLockNoException(lock);
            //Checking if the thread that got the lock released it and put it in the cache
            clientConfig = clientIdVsClientConfig.getIfPresent(clientId);
            if (clientConfig == null) {

            }
        }
    }

    private static boolean tryLockNoException(Lock lock) {
        try {
            return lock.tryLock(60, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            return false;
        }
    }
}