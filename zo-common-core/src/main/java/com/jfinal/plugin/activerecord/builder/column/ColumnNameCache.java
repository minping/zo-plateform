package com.jfinal.plugin.activerecord.builder.column;

import java.io.Serializable;

/**
 * @author zhoump
 * @date 2026/7/14
 * @purpose
 */
public class ColumnNameCache implements Serializable {

    private static final long serialVersionUID = 2630100146169044992L;

    /**
     * sql 中的字段名
     */
    private String sqlQueryName;

    /**
     * 数据库返回的查询字段名
     */
    private String dbReturnQueryName;


    public ColumnNameCache(String sqlQueryName, String dbReturnQueryName) {
        this.sqlQueryName = sqlQueryName;
        this.dbReturnQueryName = dbReturnQueryName;
    }

    public String getDbReturnQueryName() {
        return dbReturnQueryName;
    }

    public void setDbReturnQueryName(String dbReturnQueryName) {
        this.dbReturnQueryName = dbReturnQueryName;
    }


    public String getSqlQueryName() {
        return sqlQueryName;
    }

    public void setSqlQueryName(String sqlQueryName) {
        this.sqlQueryName = sqlQueryName;
    }
}

