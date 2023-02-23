package com.ritty27.ratelimit

import io.github.bucket4j.Bandwidth
import io.github.bucket4j.Bucket
import io.github.bucket4j.BucketConfiguration
import io.github.bucket4j.Refill
import io.github.bucket4j.distributed.proxy.ProxyManager
import org.springframework.stereotype.Component
import java.time.Duration
import java.util.function.Supplier

@Component
class RateLimiter(
    private val proxyManager: ProxyManager<String>
) {
    fun resolveBucket(key: String, capacity: Long, refillTokenPerMinute: Long): Bucket {
        val configSupplier = getConfigSupplier(capacity, refillTokenPerMinute)
        return proxyManager.builder().build(key, configSupplier)
    }

    private fun getConfigSupplier(capacity: Long, refillTokenPerMinute: Long): Supplier<BucketConfiguration> {
        val refill = Refill.intervally(refillTokenPerMinute, Duration.ofMinutes(1))
        val limit = Bandwidth.classic(capacity, refill)
        return Supplier {
            BucketConfiguration.builder()
                .addLimit(limit)
                .build()
        }
    }
}
