package com.ritty27.ratelimit

import io.github.bucket4j.distributed.proxy.ProxyManager
import io.github.bucket4j.grid.jcache.JCacheProxyManager
import org.redisson.config.Config
import org.redisson.jcache.configuration.RedissonConfiguration
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import javax.cache.CacheManager
import javax.cache.Caching

@Configuration
class RedisConfig(
    @Value("\${redis.nodes}") private val redisNode: String
) {
    private val rateLimitCacheKey = "rate-limiter-key"

    /**
     * single node:
     * config.useSingleServer().address = "redis://$redisNode"
     *
     * cluster node:
     * config.useClusterServers().addNodeAddress("redis://$redisNode")
     */

    @Bean
    fun config(): Config {
        val config = Config()
        config.useSingleServer().address = "redis://$redisNode"
        return config
    }

    @Bean
    fun cacheManager(config: Config): CacheManager {
        val manager = Caching.getCachingProvider("org.redisson.jcache.JCachingProvider").cacheManager
        manager.createCache(rateLimitCacheKey, RedissonConfiguration.fromConfig<String, Int>(config))

        return manager
    }

    @Bean
    fun proxyManager(cacheManager: CacheManager): ProxyManager<String> {
        return JCacheProxyManager(cacheManager.getCache(rateLimitCacheKey))
    }
}