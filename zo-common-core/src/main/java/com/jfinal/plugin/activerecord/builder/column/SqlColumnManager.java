package com.jfinal.plugin.activerecord.builder.column;

import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.statement.SQLSelectItem;
import com.alibaba.druid.sql.ast.statement.SQLSelectStatement;
import com.common.util.StrKit;
import com.db.configuration.DBConfig;
import com.jfinal.plugin.activerecord.BaseDruidPooledResultSet;
import com.jfinal.plugin.activerecord.dialect.Dialect;
import com.jfinal.plugin.activerecord.dialect.Sqlite3Dialect;
import com.jfinal.plugin.constant.DBConstant;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Types;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author zhoump
 * @date 2026/7/14
 * @purpose
 */
@Slf4j
@Component
public class SqlColumnManager {

    DBConfig dbConfig;

    private Map<String, SqlColumnNameCache> sqlColumnNameCacheMap = new HashMap<>(16);

    @Autowired
    public void init(DBConfig dbConfig) {
        this.dbConfig = dbConfig;
    }

    /**
     * 构建数据库查询列
     *
     * @param rs
     * @param labelNames
     * @param types
     * @throws SQLException
     */
    public void buildLabelNamesAndTypes(Dialect dialect, ResultSet rs, String[] labelNames, int[] types) throws SQLException {
        switch (dbConfig.getColumnMapModel()) {
            case DBConstant.DBColumnMapModel.UPPER:
                upperOutWriter(rs, labelNames, types);
                break;
            case DBConstant.DBColumnMapModel.LOWER:
                lowerOutWriter(rs, labelNames, types);
                break;
            case DBConstant.DBColumnMapModel.DB:
                dbOutWriter(dialect, rs, labelNames, types);
                break;
            case DBConstant.DBColumnMapModel.ORIGINAL:
            default:
                originalOutWriter(dialect, rs, labelNames, types);
                break;
        }
    }

    /**
     * 根据sql定义的大小写原样输出
     *
     * @param rs
     * @param labelNames
     * @param types
     * @throws SQLException
     */
    private void originalOutWriter(Dialect dialect, ResultSet rs, String[] labelNames, int[] types) throws SQLException {
        this.dbOutWriter(dialect, rs, labelNames, types);
        String sql = ((BaseDruidPooledResultSet) rs).getSql();
        SqlColumnNameCache sqlColumnNameCache = sqlColumnNameCacheMap.get(sql);
        if (sqlColumnNameCache == null) {
            sqlColumnNameCache = analysis(dialect, sql, labelNames);
            sqlColumnNameCacheMap.put(sql, sqlColumnNameCache);
        }
        for (int i = 1; i < labelNames.length; i++) {
            labelNames[i] = sqlColumnNameCache.getColumnName(labelNames[i]);
        }
    }

    private SqlColumnNameCache analysis(Dialect dialect, String sql, String[] labelNames) {
        SqlColumnNameCache sqlColumnNameCache = new SqlColumnNameCache();
        if (StrKit.isNotBlank(sql) && sql.contains("@")) {
            return sqlColumnNameCache;
        }
        try {
            SQLStatement sqlStatement = SQLUtils.parseSingleStatement(sql, dialect.getDbType(), false);
            if (sqlStatement instanceof SQLSelectStatement) {
                SQLSelectStatement sqlSelectStatement = (SQLSelectStatement) sqlStatement;
                List<SQLSelectItem> selectItemList = sqlSelectStatement.getSelect().getFirstQueryBlock().getSelectList();
                selectItemList.forEach(sqlSelectItem -> {
                    String sqlQueryColumn = getSqlQueryColumn(sqlSelectItem);
                    if ("*".equals(sqlQueryColumn)) {
                        //select *  不解析
                        return;
                    }
                    for (int i = 1; i < labelNames.length; i++) {
                        if (StrKit.equalsIgnoreCase(labelNames[i], sqlQueryColumn)) {
                            sqlColumnNameCache.addColumnName(labelNames[i], sqlQueryColumn);
                            break;
                        }
                    }
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
            log.error("{}解析异常:{}", sql, e);
        }
        return sqlColumnNameCache;
    }

    /**
     * 去除特殊字符
     * - 别名优先 expr
     * - 别名处理 "、'、`
     * - 过滤 t.字段名
     * - 处理 非字母、下划线、数字的关键字  如：`  " ；
     * - 处理“*”表达式，全小写
     *
     * @param sqlSelectItem
     * @return
     */
    private String getSqlQueryColumn(SQLSelectItem sqlSelectItem) {
        String alias = sqlSelectItem.getAlias();
        String expr = sqlSelectItem.getExpr().toString();
        if (StrKit.isNotBlank(alias)) {
            //有别名，优先级最高； 去除",',`
            return filterSpecialChar(alias);
        }
        expr = filterSpecialChar(expr);
        return filterTableAlias(expr);
    }

    /**
     * 过滤  "、'、`
     *
     * @param column
     * @return
     */
    private String filterSpecialChar(String column) {
        return column.replaceAll("[\\\"'`']", "");
    }

    /**
     * 过滤 t.字段名
     *
     * @param column
     * @return
     */
    private String filterTableAlias(String column) {
        int index = column.indexOf(".");
        if (index > 0) {
            return column.substring(index + 1);
        }
        return column;
    }

    /**
     * 转小写输出
     *
     * @param rs
     * @param labelNames
     * @param types
     * @throws SQLException
     */
    private void lowerOutWriter(ResultSet rs, String[] labelNames, int[] types) throws SQLException {
        this.dbOutWriter(null, rs, labelNames, types);
        for (int i = 1; i < labelNames.length; i++) {
            labelNames[i] = labelNames[i].toLowerCase();
        }
    }

    /**
     * 转大写输出
     *
     * @param rs
     * @param labelNames
     * @param types
     * @throws SQLException
     */
    private void upperOutWriter(ResultSet rs, String[] labelNames, int[] types) throws SQLException {
        this.dbOutWriter(null, rs, labelNames, types);
        for (int i = 1; i < labelNames.length; i++) {
            labelNames[i] = labelNames[i].toUpperCase();
        }
    }

    /**
     * 根据数据库特性输出
     *
     * @param rs
     * @param labelNames
     * @param types
     * @throws SQLException
     */
    private void dbOutWriter(Dialect dialect, ResultSet rs, String[] labelNames, int[] types) throws SQLException {
        ResultSetMetaData rsmd = rs.getMetaData();
        for (int i = 1; i < labelNames.length; i++) {
            labelNames[i] = rsmd.getColumnLabel(i);
            types[i] = rsmd.getColumnType(i);
            if (dialect instanceof Sqlite3Dialect) {
                String columnTypeName = rsmd.getColumnTypeName(i);
                if (types[i] == Types.INTEGER) {
                    if (StrKit.equals("NUMERIC", columnTypeName)) {
                        types[i] = Types.NUMERIC;
                    }
                } else if (types[i] == Types.VARCHAR) {
                    if (StrKit.equals("TIMESTAMP", columnTypeName)) {
                        types[i] = Types.TIMESTAMP;
                    }
                }
            }
        }
    }
}
