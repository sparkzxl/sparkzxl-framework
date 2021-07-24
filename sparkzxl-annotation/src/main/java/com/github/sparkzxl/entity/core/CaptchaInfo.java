package com.github.sparkzxl.entity.core;

import lombok.Data;

/**
 * description: 验证码信息
 *
 * @author zhouxinlei
 */
@Data
public class CaptchaInfo {

    /**
     * key
     */
    private String key;

    /**
     * 验证码
     */
    private String data;

}
