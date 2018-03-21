package com.ishan.base;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.util.concurrent.Striped;
import com.ishan.redis.RedisService;

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

    /**
     * This might be an overkill method, it depends on how costly the loading of client config from the store is.
     * It takes a lock on the client id and uses striped lock so that other client request can proceed.
     * The thread which successfully took the lock, puts it in the map, so that the threads which were waiting do not
     * need to load from database. This is a common pattern and should be abstracted out.
     *
     * @param clientId The client id for which the config is needed.
     * @return The {@link ClientConfig} config of client
     */
    public static ClientConfig getClientConfig(String clientId) {
        ClientConfig clientConfig = clientIdVsClientConfig.getIfPresent(clientId);
        if (clientConfig == null) {
            Lock lock = striped.get(clientId);
            boolean acquired = tryLockNoException(lock);
            try {
                //Checking if the thread that got the lock released it and put it in the cache
                clientConfig = clientIdVsClientConfig.getIfPresent(clientId);
                if (clientConfig == null) {
                    clientConfig = RedisService.get(clientId, ClientConfig.class);
                    clientIdVsClientConfig.put(clientId, clientConfig);
                    return clientConfig;
                }
            } finally {
                if (acquired) {
                    lock.unlock();
                }
            }
        }
        return clientConfig;
    }

    private static boolean tryLockNoException(Lock lock) {
        try {
            return lock.tryLock(60, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            return false;
        }
    }
}