package com.example.lock.redisdistributedlockexample.lock;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.concurrent.TimeUnit;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface DistributedLock {

    String key();

    // 락 시간 단위
    TimeUnit timeUnit() default TimeUnit.SECONDS;

    // 락 대기 시간
    long waitTime() default 0L;

    // 락 유지 시간
    long leaseTime() default 6L;
}
