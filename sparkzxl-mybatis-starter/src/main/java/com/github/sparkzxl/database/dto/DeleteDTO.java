package com.github.sparkzxl.database.dto;

import lombok.Data;

import java.util.List;

/**
 * description: 删除入参
 *
 * @author zhouxinlei
 */
@Data
public class DeleteDTO<ID> {

    private List<ID> ids;

}
