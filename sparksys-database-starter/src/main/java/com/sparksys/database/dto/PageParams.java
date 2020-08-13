package com.sparksys.database.dto;

import cn.hutool.core.util.StrUtil;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.github.pagehelper.PageHelper;
import com.google.common.collect.Maps;
import com.sparksys.database.constant.EntityConstant;
import com.sparksys.database.entity.SuperEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Map;

/**
 * 分页参数
 *
 * @author zuihou
 * @date 2020年02月14日16:19:36
 */
@Data
@ApiModel(value = "PageParams", description = "分页参数")
public class PageParams<T> {

    @ApiModelProperty(value = "查询参数", required = true)
    private T model;

    @ApiModelProperty(value = "当前页", example = "1")
    private int pageNum = 1;

    @ApiModelProperty(value = "页面大小", example = "10")
    private int pageSize = 10;

    @ApiModelProperty(value = "排序,默认createTime", allowableValues = "id,createTime,updateTime", example = "id")
    private String sort = EntityConstant.ID;

    @ApiModelProperty("扩展参数")
    private Map<String, String> map = Maps.newHashMap();

    /**
     * 构建分页参数
     *
     * @return void
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
