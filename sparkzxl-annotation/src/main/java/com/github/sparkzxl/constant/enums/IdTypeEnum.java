package com.github.sparkzxl.constant.enums;

/**
 * description: id生成策略枚举
 *
 * @author zhouxinlei
 */
public enum IdTypeEnum {

    SIMPLE_UUID(1),
    RANDOM_UUID(2),
    OBJECT_Id(3),
    SNOWFLAKE_ID(4);

    private final int key;

    private IdTypeEnum(int key) {
        this.key = key;
    }

    public int getKey() {
        return this.key;
    }

}
