package com.jfinal.plugin.activerecord.dialect;

import com.alibaba.druid.DbType;
import com.jfinal.plugin.activerecord.dialect.sqlite.SqliteModelBuilder;
import com.jfinal.plugin.activerecord.dialect.sqlite.SqliteRecordBuilder;
import com.jfinal.plugin.constant.DBConstant;
import com.jfinal.plugin.druid.DruidPlugin;

/**
 * SqliteDialect.
 */
public class Sqlite3Dialect extends BaseNormalTypeDialect {

    public static final String KEY = "sqlite3";

    public Sqlite3Dialect() {
        this.modelBuilder = SqliteModelBuilder.newInstance(this);
        this.recordBuilder = SqliteRecordBuilder.newInstance(this);
    }

    @Override
    public String key() {
        return KEY;
    }


    @Override
    public DbType getDbType() {
        return DbType.sqlite;
    }

    @Override
    public boolean match(DruidPlugin druidPlugin) {
        String url = druidPlugin.getUrl();
        return url.startsWith("jdbc:sqlite:");
    }

    @Override
    public int getOrder() {
        return DBConstant.DialectOrder.SQLITE3_ORDER;
    }
}