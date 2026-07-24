package com.db.druid;

import com.jfinal.plugin.druid.DruidPlugin;

import javax.sql.DataSource;

/**
 * @author zhoump
 * @date 2026/7/15
 * @purpose
 */
public interface IDataSourceDefinition {

    /**
     * 定义datasource
     * @param druidPlugin
     * @return
     * @author wenjin
     */
    DataSource dataSourceDefinition(DruidPlugin druidPlugin);
}
