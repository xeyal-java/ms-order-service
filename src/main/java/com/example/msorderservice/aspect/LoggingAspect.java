package com.example.msorderservice.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Aspect
@Component
@Slf4j
public class LoggingAspect {

    @Pointcut("within(com.example.msorderservice.service..*)")
    public void serviceLayerPointcut() {}

    @Around("serviceLayerPointcut()")
    public Object logAround(ProceedingJoinPoint joinPoint) throws Throwable {
        String methodName = joinPoint.getSignature().getName();
        String className = joinPoint.getSignature().getDeclaringType().getSimpleName();
        Object[] args = joinPoint.getArgs();

        log.info("--- METHOD STARTED: {}.{}() | Arguments: {}", className, methodName, Arrays.toString(args));

        long start = System.currentTimeMillis();
        try {
            Object result = joinPoint.proceed();
            long executionTime = System.currentTimeMillis() - start;

            log.info("--- METHOD COMPLETED: {}.{}() | Execution Time: {} ms",
                    className, methodName, executionTime);
            return result;
        } catch (Exception e) {
            log.error("--- ERROR: {}.{}() | Reason: {}",
                    className, methodName, e.getMessage());
            throw e;
        }
    }
}