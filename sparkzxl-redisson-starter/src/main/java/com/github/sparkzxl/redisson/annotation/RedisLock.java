package com.github.sparkzxl.redisson.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * description: redis lock注解
 *
 * @author zhouxinlei
 */
@Target({ElementType.PARAMETER, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface RedisLock {

    /**
     * spel表达式
     */
    String expression() default "#p0";

    /**
     * key的前缀
     *
     * @return String
     */
    String keyPrefix() default "";

    /**
     * 获取锁等待时长（毫秒）
     *
     * @return int
     */
    long waitTime() default 50;

    /**
     * 获取锁后自动过期时长（毫秒）
     *
     * @return int
     */
    long leaseTime() default 10000;

    /**
     * 重试次数
     *
     * @return int
     */
    int tryCount() default 0;

    /**
     * 重试休眠时长（毫秒）
     *
     * @return int
     */
    long sleepTime() default 500;

}
