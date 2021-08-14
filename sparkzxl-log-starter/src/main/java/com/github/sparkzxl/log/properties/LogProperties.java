package com.github.sparkzxl.log.properties;

import com.github.sparkzxl.constant.ConfigurationConstant;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

/**
 * description: 日志配置类
 *
 * @author zhouxinlei
 */
@Data
@ConfigurationProperties(prefix = ConfigurationConstant.LOG_PREFIX)
public class LogProperties {

    /**
     * 是否开启控制台输出
     */
    private boolean enableConsole = true;

    /**
     * 是否开启日志存储
     */
    private boolean storage = false;

    @NestedConfigurationProperty
    private FileProperties file = new FileProperties();

    @NestedConfigurationProperty
    private KafkaProperties kafka = new KafkaProperties();


    /**
     * description: 日志文件配置类
     *
     * @author zhouxinlei
     */
    @Getter
    @Setter
    public static class FileProperties {

        /**
         * 是否开启日志持久化
         */
        private boolean enable;

        /**
         * 是否开启日志json化存储
         */
        private boolean enableJson;

        private String name;

        private boolean cleanHistoryOnStart;

        private String totalSizeCap = "10GB";

        private String path = System.getProperty("user.home").concat("/logs");

        private int maxHistory = 7;

        private String maxFileSize = "10MB";

    }


    /**
     * description: kafka配置类
     *
     * @author zhouxinlei
     */
    @Getter
    @Setter
    public static class KafkaProperties {

        /**
         * kafka是否开启
         */
        private boolean enable;

        /**
         * kafka地址
         */
        private String servers;

        /**
         * kafka topic
         */
        private String topic;


    }

}
