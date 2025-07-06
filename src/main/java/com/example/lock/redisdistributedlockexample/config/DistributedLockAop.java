package com.example.lock.redisdistributedlockexample.config;

import com.example.lock.redisdistributedlockexample.lock.DistributedLock;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.el.lang.EvaluationContext;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.Arrays;

@Slf4j
@Order(1)
@Aspect
@Configuration
@RequiredArgsConstructor
public class DistributedLockAop {

    private static final String REDISSON_LOCK_PREFIX = "LOCK:";

    private final RedissonClient redissonClient;
    private final AopForTransaction aopForTransaction;

    @Around("@annotation(com.example.lock.redisdistributedlockexample.lock.DistributedLock)")
    public Object lock(final ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        final DistributedLock distributedLock = method.getAnnotation(DistributedLock.class);

        StandardEvaluationContext context = new StandardEvaluationContext();
        final String[] parameterNames = signature.getParameterNames();
        Arrays.stream(parameterNames).forEach(it -> log.info("param = {}", it));

        SpelExpressionParser parser = new SpelExpressionParser();
        if (parameterNames != null) {
            for (int i = 0; i < parameterNames.length; i++) {
                log.info("param = {} arg = {}", parameterNames[i], joinPoint.getArgs()[i]);
                context.setVariable(parameterNames[i], joinPoint.getArgs()[i]);
            }
        }

        String lockKey = REDISSON_LOCK_PREFIX + parser.parseExpression(distributedLock.key()).getValue(context, String.class);
        log.info("Locking with key: {}", lockKey);

        RLock rLock = redissonClient.getLock(lockKey);
        String lockName = rLock.getName();

        log.info("Lock name = {}", lockName);
        boolean available = false;
        try {
            available = rLock.tryLock(distributedLock.waitTime(), distributedLock.leaseTime(), distributedLock.timeUnit());
            log.info("available = {}", available);
            if (!available) {
                log.warn("Could not acquire lock: {}", lockName);
                throw new IllegalStateException("Could not acquire lock: " + lockName);
            }

            return aopForTransaction.proceed(joinPoint);
        } catch (InterruptedException e) {
            throw new IllegalStateException(e);
        } finally {
            log.info("🔚 Entered finally block");
            log.info("🔍 isHeldByCurrentThread: {}", rLock.isHeldByCurrentThread());

            if (available && rLock.isHeldByCurrentThread()) {
                rLock.unlock();
                log.info("🔓 Unlocking with key: {}", lockKey);
            } else {
                log.info("⚠️ Redisson lock already unlocked (or not acquired)");
            }
        }

    }
}
