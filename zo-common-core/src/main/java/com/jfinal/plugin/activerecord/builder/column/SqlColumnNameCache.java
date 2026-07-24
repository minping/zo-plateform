package com.jfinal.plugin.activerecord.builder.column;

import java.util.HashMap;
import java.util.Map;

/**
 * @author zhoump
 * @date 2026/7/14
 * @purpose
 */
public class SqlColumnNameCache {

    private Map<String,ColumnNameCache> columnNameCacheMap = new HashMap<>(16);

    public String getColumnName(String dbColumnName){
        ColumnNameCache columnNameCache = columnNameCacheMap.get(dbColumnName);
        return  columnNameCache == null ? dbColumnName.toLowerCase() : columnNameCache.getSqlQueryName();
    }

    public SqlColumnNameCache addColumnName(String dbColumnName,String sqlQueryColumn){
        columnNameCacheMap.put(dbColumnName,new ColumnNameCache(sqlQueryColumn,dbColumnName));
        return this;
    }

}
