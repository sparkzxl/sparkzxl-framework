package com.github.sparkzxl.datasource.provider;

import cn.hutool.core.text.StrFormatter;
import com.baomidou.dynamic.datasource.spring.boot.autoconfigure.DataSourceProperty;
import com.github.sparkzxl.core.support.TenantException;
import com.github.sparkzxl.core.util.ArgumentAssert;
import lombok.RequiredArgsConstructor;

import javax.sql.DataSource;
import java.util.List;
import java.util.function.Function;

/**
 * description: 数据库加载数据源实现类
 *
 * @author zhouxinlei
 */
@RequiredArgsConstructor
public class JdbcDataSourceProvider extends BaseDataSourceProvider {

    private final Function<String, List<DataSourceProperty>> function;

    @Override
    public DataSource loadSelectedDataSource(String tenantId) {
        List<DataSourceProperty> dataSourceProperties = function.apply(tenantId);
        DataSourceProperty dataSourceProperty = loadBalancerDataSource(dataSourceProperties);
        ArgumentAssert.notNull(dataSourceProperty, () -> new TenantException(StrFormatter.format("无此租户[{}]", tenantId)));
        return createDataSource(tenantId, dataSourceProperty);
    }
}
