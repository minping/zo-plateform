package com.db.druid;

import com.jfinal.plugin.druid.DruidPlugin;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;

/**
 * datasource 定义管理
 * @author wenjin
 */
@Component
public class DataSourceDefinitionManager implements IDataSourceDefinition {


    /**
     * 允许二开dataSource对象
     * @param druidPlugin
     * @return
     */
    @Override
    public DataSource dataSourceDefinition(DruidPlugin druidPlugin) {
        return druidPlugin.getDataSource();
    }
}