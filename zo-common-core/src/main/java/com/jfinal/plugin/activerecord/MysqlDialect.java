package com.jfinal.plugin.activerecord;

import com.alibaba.druid.DbType;
import com.jfinal.plugin.constant.DBConstant;
import com.jfinal.plugin.druid.DruidPlugin;

/**
 * @author zhoump
 * @date 2026/7/14
 * @purpose
 */
public class MysqlDialect extends BaseMysqlTypeDialect {

    public static final String KEY = "mysql";

    @Override
    public String key() {
        return KEY;
    }

    @Override
    public boolean match(DruidPlugin druidPlugin) {
        String url = druidPlugin.getUrl();
        return url.startsWith("jdbc:mysql");
    }

    @Override
    public DbType getDbType() {
        return DbType.mysql;
    }

    @Override
    public int getOrder() {
        return DBConstant.DialectOrder.MYSQL_ORDER;
    }
}
