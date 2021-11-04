package com.github.sparkzxl.cache;

import com.github.sparkzxl.cache.service.CaffeineCacheImpl;
import com.github.sparkzxl.cache.service.GeneralCacheService;
import com.github.sparkzxl.cache.support.CacheExceptionHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * description: 缓存自动配置类
 *
 * @author zhouxinlei
 */
@Configuration
@Import({CacheExceptionHandler.class})
public class CacheAutoConfiguration {

    @Bean
    public GeneralCacheService cacheCaffeineTemplate() {
        return new CaffeineCacheImpl();
    }

}