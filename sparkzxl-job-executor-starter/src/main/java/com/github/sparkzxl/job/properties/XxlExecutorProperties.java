package com.github.sparkzxl.job.properties;

import static com.github.sparkzxl.job.properties.XxlExecutorProperties.XXL_JOB_PREFIX;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * description: xxl job属性配置类
 *
 * @author zhouxinlei
 */
@Data
@ConfigurationProperties(prefix = XXL_JOB_PREFIX)
public class XxlExecutorProperties {

    public static final String XXL_JOB_PREFIX = "xxl-job";

    private String accessToken;

    /**
     * xxl-job admin服务地址
     */
    private String adminAddresses;

    private String address;

    private String appName;

    private String ip;

    /**
     * 日志路径
     */
    private String logPath;

    /**
     * 日志保留天数
     */
    private int logRetentionDays;

    /**
     * 端口
     */
    private int port;

}
