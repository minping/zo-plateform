package com.jfinal.plugin.activerecord.dialect.sqlite;

import cn.hutool.core.date.DateUtil;
import com.jfinal.plugin.activerecord.CPI;
import com.jfinal.plugin.activerecord.Model;
import com.jfinal.plugin.activerecord.ModelBuilder;
import com.jfinal.plugin.activerecord.builder.i.IModelBuilder;
import com.jfinal.plugin.activerecord.dialect.Dialect;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author zhoump
 * @date 2026/7/14
 * @purpose
 */
public class SqliteModelBuilder extends ModelBuilder {



    public static SqliteModelBuilder newInstance(Dialect dialect) {
        SqliteModelBuilder modelBuilder = new SqliteModelBuilder();
        modelBuilder.dialect = dialect;
        return modelBuilder;
    }


    @Override
    public <T> List<T> build(ResultSet rs, Class<? extends Model> modelClass) throws SQLException, ReflectiveOperationException {
        return build(rs, modelClass, null);
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    @Override
    public <T> List<T> build(ResultSet rs, Class<? extends Model> modelClass, IModelBuilder iModelBuilder) throws SQLException, ReflectiveOperationException {

        List<T> result = new ArrayList<T>();
        ResultSetMetaData rsmd = rs.getMetaData();
        int columnCount = rsmd.getColumnCount();
        String[] labelNames = new String[columnCount + 1];
        int[] types = new int[columnCount + 1];
        buildLabelNamesAndTypes(rs, labelNames, types);
        while (rs.next()) {
            Model<?> ar = modelClass.newInstance();
            Map<String, Object> attrs = CPI.getAttrs(ar);
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
                        value = handleClob(rs.getClob(i));
                    } else if (types[i] == Types.NCLOB) {
                        value = handleClob(rs.getNClob(i));
                    } else if (types[i] == Types.BLOB) {
                        InputStream binaryStream = rs.getBinaryStream(i);
                        try {
                            value = IOUtils.toByteArray(binaryStream);
                        } catch (IOException e) {
                            e.printStackTrace();
                            value = null;
                        }
                    } else if (types[i] == Types.OTHER) {
                        value = rs.getString(i);
                    } else {
                        value = rs.getObject(i);
                    }
                }

                if (iModelBuilder != null) {
                    if (iModelBuilder.getEncColumns() != null && iModelBuilder.getEncColumns().size() > 0) {
                        Set<String> encColumns = iModelBuilder.getEncColumns();
                        if (encColumns.contains(labelNames[i].toLowerCase()) && value != null) {
                            // 加解密逻辑
                            //value = securitySingleton.decode(value.toString());
                        }

                    }
                    iModelBuilder.putValue(attrs, labelNames[i], value);
                } else {
                    attrs.put(labelNames[i].toLowerCase(), value);
                }
            }
            if (iModelBuilder != null) {
                Map<String, Object> map = iModelBuilder.buildRecord(attrs);
                if (map != null) {
                    ar._setAttrs(map);
                    result.add((T) ar);
                }
            } else {
                result.add((T) ar);
            }
        }
        return result;
    }
}

