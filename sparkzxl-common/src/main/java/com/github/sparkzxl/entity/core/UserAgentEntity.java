package com.github.sparkzxl.entity.core;

import lombok.Data;

/**
 * description: UserAgent信息
 *
 * @author zhouxinlei
 */
@Data
public class UserAgentEntity {

    private String ua;

    /**
     * 请求ip
     */
    private String requestIp;

    /**
     * 请求地点
     */
    private String location;

    /**
     * 浏览器名称
     */
    private String browser;

    /**
     * 浏览器版本
     */
    private String browserVersion;

    /**
     * 操作系统
     */
    private String operatingSystem;

    /**
     * 是否是手机
     */
    private boolean mobile;

}
