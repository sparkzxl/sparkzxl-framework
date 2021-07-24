package com.github.sparkzxl.zookeeper.config;

import com.github.sparkzxl.zookeeper.aspect.ZkLockAspect;
import com.github.sparkzxl.zookeeper.lock.ZkDistributedLock;
import com.github.sparkzxl.zookeeper.properties.CuratorProperties;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * description:
 *
 * @author zhouxinlei
 */
@Configuration
@EnableConfigurationProperties(CuratorProperties.class)
public class CuratorConfig {

    @Bean(initMethod = "start")
    public CuratorFramework curatorFramework(CuratorProperties curatorProperties) {
        RetryPolicy retryPolicy = new ExponentialBackoffRetry(curatorProperties.getTimeout(), curatorProperties.getRetryCount());
        return CuratorFrameworkFactory.newClient(
                curatorProperties.getUrl(),
                curatorProperties.getSessionTimeoutMs(),
                curatorProperties.getConnectionTimeoutMs(),
                retryPolicy);
    }

    @Bean
    public ZkDistributedLock zkDistributedLock(CuratorFramework curatorFramework) {
        return new ZkDistributedLock(curatorFramework);
    }

    @Bean
    public ZkLockAspect zkLockAspect(ZkDistributedLock zkDistributedLock) {
        ZkLockAspect zkLockAspect = new ZkLockAspect();
        zkLockAspect.setZkDistributedLock(zkDistributedLock);
        return zkLockAspect;
    }
}
