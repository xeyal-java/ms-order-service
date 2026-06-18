package com.example.msorderservice.aspect;

import com.example.msorderservice.annotation.CacheEvict;
import com.example.msorderservice.annotation.RedisCache;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

import java.time.Duration;

@Aspect
@Component
@RequiredArgsConstructor
public class RedisCacheAspect {

    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper objectMapper;

    @Around("@annotation(redisCache)")
    public Object handleCaching(ProceedingJoinPoint joinPoint, RedisCache redisCache) throws Throwable {
        Object[] args = joinPoint.getArgs();
        String id = (args.length > 0 && args[0] != null) ? args[0].toString() : "default";
        String finalKey = redisCache.key() + id;

        Object cachedJson = redisTemplate.opsForValue().get(finalKey);

        if (cachedJson != null) {
            return objectMapper.readValue(cachedJson.toString(), Object.class);
        }

        Object result = joinPoint.proceed();
        if (result != null) {
            String json = objectMapper.writeValueAsString(result);
            redisTemplate.opsForValue().set(finalKey, json, Duration.ofSeconds(redisCache.ttl()));
        }

        return result;
    }

    @Around("@annotation(cacheEvict)")
    public Object handleEvict(ProceedingJoinPoint joinPoint, CacheEvict cacheEvict) throws Throwable {
        Object result = joinPoint.proceed();

        Object[] args = joinPoint.getArgs();
        String id = (args.length > 0 && args[0] != null) ? args[0].toString() : "";
        String finalKey = cacheEvict.key() + id;
        redisTemplate.delete(finalKey);

        return result;
    }
}