package com.github.sparkzxl.alarm.entity;

import java.io.Serializable;
import lombok.Data;

/**
 * description:
 *
 * @author zhouxinlei
 * @since 2022-01-05 15:59:26
 */
@Data
public class ExpressionTemplate implements Serializable {

    private static final long serialVersionUID = -8093477293872333358L;

    private String key;
    private String expression;

}
