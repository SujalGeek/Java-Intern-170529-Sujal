package com.example.enotes.aspect;


import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Aspect
@Component
public class LoggingAspect {

    /**
     * This ONE method handles logging for both Controllers AND Services.
     * We use "||" (OR) to combine the Pointcuts.
     */
	@Around("execution(* com.example.enotes.controller..*(..)) || execution(* com.example.enotes.service..*(..))")
	public Object logExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable {
        
        // 1. Get Details
        Signature signature = joinPoint.getSignature();
        String className = signature.getDeclaringType().getSimpleName();
        String methodName = signature.getName();
        
        // 2. Log Start
        log.info("Calling :: {}.{}()", className, methodName);
        
        long start = System.currentTimeMillis();
        
        // 3. Run the actual method
        Object result = joinPoint.proceed();
        
        // 4. Calculate Duration
        long duration = System.currentTimeMillis() - start;
        
        // 5. Log End with Time
        log.info("Execution End :: {}.{}() :: {} ms", className, methodName, duration);
        
        return result;
    }
}