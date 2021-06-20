package com.github.sparkzxl.database.dto;

import cn.hutool.core.util.StrUtil;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.github.pagehelper.PageHelper;
import com.github.sparkzxl.database.constant.EntityConstant;
import com.google.common.collect.Maps;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Map;

/**
 * description: 分页参数
 *
 * @author zhouxinlei
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "PageParams", description = "分页参数")
public class PageParams<T> extends PageDTO {

    @ApiModelProperty(value = "查询参数", required = true)
    private T model;

    @ApiModelProperty(value = "排序,默认createTime", allowableValues = "id,createTime,updateTime", example = "id")
    private String sort = EntityConstant.ID;

    @ApiModelProperty("扩展参数")
    private Map<String, String> map = Maps.newHashMap();

    /**
     * 构建分页参数
     */
    @JsonIgnore
    public void buildPage() {
        PageParams<T> params = this;
        //没有排序参数
        if (StrUtil.isEmpty(params.getSort())) {
            PageHelper.startPage(params.getPageNum(), params.getPageSize());
        }
        PageHelper.startPage(params.getPageNum(), params.getPageSize(), params.getSort());
    }

}
