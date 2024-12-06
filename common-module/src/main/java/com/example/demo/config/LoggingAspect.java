package com.example.demo.config;

import java.util.Arrays;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Component
@Aspect
@Slf4j
public class LoggingAspect {
    @Value("${spring.application.name}")
    private String serviceName;

    @Pointcut("execution(public * com.example.demo..*.*(..))")
    private void publicMethodsFromLoggingPackage() {
    }

    @Around(value = "publicMethodsFromLoggingPackage()")
    public Object logAround(ProceedingJoinPoint joinPoint) throws Throwable {
        String className = joinPoint.getSignature().getDeclaringTypeName();
        String methodName = joinPoint.getSignature().getName();
        Object[] args = joinPoint.getArgs();
        log.debug(">> {}.{}.{}() - Start", serviceName, className, methodName, Arrays.toString(args));
        long start = System.currentTimeMillis();
        try {
            Object result = joinPoint.proceed();
            return result;
        } catch (Throwable ex) {
            log.error("Exception occurred: Type={}, Detail={}",
                    ex.getClass().getName(), ex.getMessage(), ex);
            throw ex;
        } finally {
            long end = System.currentTimeMillis();
            log.debug("<< {}.{}.{}() - End in {}ms", serviceName, className, methodName, end - start);
        }
    }
}
