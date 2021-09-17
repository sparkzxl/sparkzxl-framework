package com.github.sparkzxl.database.base.controller;

import com.github.sparkzxl.database.base.service.SuperService;

/**
 * description: 基础controller
 *
 * @author zhouxinlei
 */
public interface BaseController<Entity> {

    /**
     * 获取实体的类型
     *
     * @return Class<Entity>
     */
    Class<Entity> getEntityClass();

    /**
     * 获取Service
     *
     * @return SuperService<Entity>
     */
    SuperService<Entity> getBaseService();

}
