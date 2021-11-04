package com.github.sparkzxl.database.plugins;

import com.baomidou.mybatisplus.extension.plugins.handler.TenantLineHandler;
import com.github.sparkzxl.core.context.RequestLocalContextHolder;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.StringValue;

import java.util.List;

/**
 * description: 多租户处理器
 *
 * @author zhouxinlei
 */
public class TenantLineHandlerInterceptor implements TenantLineHandler {

    private final String tenantIdColumn;
    private final List<String> ignoreTableList;

    public TenantLineHandlerInterceptor(String tenantIdColumn, List<String> ignoreTableList) {
        this.tenantIdColumn = tenantIdColumn;
        this.ignoreTableList = ignoreTableList;
    }

    @Override
    public Expression getTenantId() {
        return new StringValue(RequestLocalContextHolder.getTenant());
    }

    @Override
    public String getTenantIdColumn() {
        return tenantIdColumn;
    }

    @Override
    public boolean ignoreTable(String tableName) {
        return ignoreTableList.contains(tableName);
    }
}