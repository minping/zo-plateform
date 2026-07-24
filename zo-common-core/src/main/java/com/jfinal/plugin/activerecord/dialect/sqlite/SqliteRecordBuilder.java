package com.jfinal.plugin.activerecord.dialect.sqlite;

import cn.hutool.core.date.DateUtil;
import com.jfinal.kit.StringKit;
import com.jfinal.plugin.activerecord.*;
import com.jfinal.plugin.activerecord.dialect.Dialect;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Types;
import java.util.*;

/**
 * @author zhoump
 * @date 2026/7/14
 * @purpose
 */
public class SqliteRecordBuilder extends RecordBuilder {


    public static SqliteRecordBuilder newInstance(Dialect dialect) {
        SqliteRecordBuilder recordBuilder = new SqliteRecordBuilder();
        recordBuilder.dialect = dialect;
        return recordBuilder;
    }

    @Override
    public List<Record> build(Config config, ResultSet rs) throws SQLException {
        return build(config, rs, null);
    }

    @Override
    public List<Record> build(Config config, ResultSet rs, IRecordBuilder iRecordBuilder) throws SQLException {
        List<Record> result = new ArrayList<>();
        ResultSetMetaData rsmd = rs.getMetaData();
        int columnCount = rsmd.getColumnCount();
        String[] labelNames = new String[columnCount + 1];
        int[] types = new int[columnCount + 1];
        buildLabelNamesAndTypes(rs, labelNames, types);
        while (rs.next()) {
            Record record = new Record();
            CPI.setColumnsMap(record, config.getContainerFactory().getColumnsMap());
            Map<String, Object> columns = record.getColumns();
            for (int i = 1; i <= columnCount; i++) {
                Object value;
                if (types[i] == Types.NUMERIC) {
                    value = rs.getBigDecimal(i);
                } else if (types[i] < Types.DATE) {
                    value = rs.getObject(i);
                } else {
                    if (types[i] == Types.TIMESTAMP) {
                        try {
                            value = DateUtil.parse(rs.getString(i));
                        } catch (cn.hutool.core.date.DateException e) {
                            value = rs.getTimestamp(i);
                        }
                    } else if (types[i] == Types.DATE) {
                        value = rs.getDate(i);
                    } else if (types[i] == Types.CLOB) {
                        value = ModelBuilder.newInstance(null).handleClob(rs.getClob(i));
                    } else if (types[i] == Types.NCLOB) {
                        value = ModelBuilder.newInstance(null).handleClob(rs.getNClob(i));
                    } else if (types[i] == Types.BLOB) {
                        value = ModelBuilder.newInstance(null).handleBlob(rs.getBlob(i));
                    } else if (types[i] == Types.OTHER) {
                        value = rs.getString(i);
                    } else {
                        value = rs.getObject(i);
                    }
                }
                if (iRecordBuilder != null) {
                    if (iRecordBuilder.getEncColumns() != null && iRecordBuilder.getEncColumns().size() > 0) {
                        Set<String> encColumns = iRecordBuilder.getEncColumns();
                        if (encColumns.contains(labelNames[i]) && value != null) {
                            // 加解密逻辑
                            //value = securitySingleton.decode(value.toString());
                        }

                    }
                    iRecordBuilder.putValue(columns, labelNames[i], value);
                } else {
                    columns.put(labelNames[i], value);
                }
            }

            if (iRecordBuilder != null) {
                Record bdRecord = iRecordBuilder.buildRecord(record);
                if (bdRecord != null) {
                    result.add(bdRecord);
                }
            } else {
                result.add(record);
            }
        }
        return result;
    }

    public List<Record> buildTree(Config config, ResultSet rs, TreeInfo treeInfo) throws SQLException {
        List<Record> result = new ArrayList<>();
        Map<String, Record> recordMap = new HashMap<>();
        ResultSetMetaData rsmd = rs.getMetaData();
        int columnCount = rsmd.getColumnCount();
        String[] labelNames = new String[columnCount + 1];
        int[] types = new int[columnCount + 1];
        buildLabelNamesAndTypes(rs, labelNames, types);
        while (rs.next()) {
            Record record = new Record();
            record.setColumnsMap(config.getContainerFactory().getColumnsMap());
            Map<String, Object> columns = record.getColumns();
            for (int i = 1; i <= columnCount; i++) {
                Object value;
                if (types[i] < Types.BLOB) {
                    value = rs.getObject(i);
                } else {
                    if (types[i] == Types.CLOB) {
                        value = ModelBuilder.newInstance(dialect).handleClob(rs.getClob(i));
                    } else if (types[i] == Types.NCLOB) {
                        value = ModelBuilder.newInstance(dialect).handleClob(rs.getNClob(i));
                    } else if (types[i] == Types.BLOB) {
                        value = ModelBuilder.newInstance(dialect).handleBlob(rs.getBlob(i));
                    } else if (types[i] == Types.OTHER) {
                        value = rs.getString(i);
                    } else {
                        value = rs.getObject(i);
                    }
                }

                columns.put(labelNames[i], value);
            }
            String id = record.get(treeInfo.getIdKey());
            String pid = record.get(treeInfo.getpIdKey());
            if (StringKit.isBlank(pid) || !recordMap.containsKey(pid)) {
                //不存在父节点，则添加到根节点上
                record.set(treeInfo.getLevelKey(), 1);
                result.add(record);
            } else {
                Record fRecord = recordMap.get(pid);
                List<Record> children = fRecord.get(treeInfo.getChildKey());
                if (children == null) {
                    children = new ArrayList<>();
                    fRecord.set(treeInfo.getChildKey(), children);
                }
                record.set(treeInfo.getLevelKey(), fRecord.getInt(treeInfo.getLevelKey()) + 1);
                children.add(record);
            }
            recordMap.put(id, record);
        }
        return result;
    }
}
