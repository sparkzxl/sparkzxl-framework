package com.github.sparkzxl.gateway.properties;

import com.google.common.collect.Lists;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

import java.util.List;

/**
 * description: 网关插件属性配置
 *
 * @author zhoux
 */
@Slf4j
@Getter
@Setter
@ToString
public class GatewayPluginProperties {

    public static final String GATEWAY_PLUGIN_PROPERTIES_PREFIX = "spring.cloud.gateway.plugin";

    /**
     * Enable Or Disable Global Exception Json Handler
     */
    private boolean exceptionJsonHandler = false;

    @NestedConfigurationProperty
    private LogProperties logging = new LogProperties();
}
