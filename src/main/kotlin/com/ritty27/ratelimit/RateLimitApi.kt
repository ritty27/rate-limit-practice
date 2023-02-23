package com.ritty27.ratelimit

import mu.KotlinLogging
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/resource")
class RateLimitApi(
    rateLimiter: RateLimiter
) {

    private val logger = KotlinLogging.logger { }
    private val bucket = rateLimiter.resolveBucket("ad-mail", 10, 5)

    @GetMapping
    fun getResource() {
        if (bucket.tryConsume(1)) {
            logger.info { "사용하고 남은 토큰의 수: ${bucket.availableTokens}" }
        } else {
            logger.info { "토큰의 수가 부족합니다." }
        }
    }

}