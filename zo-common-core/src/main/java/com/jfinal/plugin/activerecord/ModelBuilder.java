package com.jfinal.plugin.activerecord;

import com.jfinal.holder.DBSpringContextHolder;
import com.jfinal.plugin.activerecord.builder.column.SqlColumnManager;
import com.jfinal.plugin.activerecord.builder.i.IModelBuilder;
import com.jfinal.plugin.activerecord.dialect.Dialect;

import java.io.IOException;
import java.io.InputStream;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

/**
 * @author zhoump
 * @date 2026/7/14
 * @purpose
 */
public class ModelBuilder {
    protected Dialect dialect;


    protected ModelBuilder(){

    }

    public static ModelBuilder newInstance(Dialect dialect){
        ModelBuilder modelBuilder = new ModelBuilder();
        modelBuilder.dialect = dialect;
        return modelBuilder;
    }
    @SuppressWarnings({"rawtypes"})
    public <T> List<T> build(ResultSet rs, Class<? extends Model> modelClass) throws SQLException, ReflectiveOperationException {
        return build(rs, modelClass, null);
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    public <T> List<T> build(ResultSet rs, Class<? extends Model> modelClass, IModelBuilder iModelBuilder) throws SQLException, ReflectiveOperationException {

        List<T> result = new ArrayList<T>();
        ResultSetMetaData rsmd = rs.getMetaData();
        int columnCount = rsmd.getColumnCount();
        String[] labelNames = new String[columnCount + 1];
        int[] types = new int[columnCount + 1];
        buildLabelNamesAndTypes(rs, labelNames, types);
        while (rs.next()) {
            Model<?> ar = modelClass.newInstance();
            Map<String, Object> attrs = ar._getAttrs();
            for (int i = 1; i <= columnCount; i++) {
                Object value;
                if (types[i] < Types.BLOB) {
                    value = rs.getObject(i);
                } else {
                    if (types[i] == Types.CLOB) {
                        value = handleClob(rs.getClob(i));
                    } else if (types[i] == Types.NCLOB) {
                        value = handleClob(rs.getNClob(i));
                    } else if (types[i] == Types.BLOB) {
                        value = handleBlob(rs.getBlob(i));
                    } else {
                        value = rs.getObject(i);
                    }
                }
                if (iModelBuilder != null) {
                    if (iModelBuilder.getEncColumns()!=null&&iModelBuilder.getEncColumns().size()>0) {
                        Set<String> encColumns = iModelBuilder.getEncColumns();
                        if (encColumns.contains(labelNames[i].toLowerCase())&&value!=null) {
                            // 加解密逻辑
                            //value=securitySingleton.decode(value.toString());
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

    public void buildLabelNamesAndTypes(ResultSetMetaData rsmd, String[] labelNames, int[] types) throws SQLException {
        for (int i=1; i<labelNames.length; i++) {
            labelNames[i] = rsmd.getColumnLabel(i);
            types[i] = rsmd.getColumnType(i);
        }
    }

    public void buildLabelNamesAndTypes(ResultSet rs, String[] labelNames, int[] types) throws SQLException {
        DBSpringContextHolder.getBean(SqlColumnManager.class).buildLabelNamesAndTypes(dialect,rs,labelNames,types);
    }


    public byte[] handleBlob(Blob blob) throws SQLException {
        if (blob == null)
            return null;

        InputStream is = null;
        try {
            is = blob.getBinaryStream();
            if (is == null)
                return null;
            byte[] data = new byte[(int) blob.length()];        // byte[] data = new byte[is.available()];
            if (data.length == 0)
                return null;
            is.read(data);
            return data;
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            if (is != null)
                try {
                    is.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
        }
    }

    public String handleClob(Clob clob) throws SQLException {
        if (clob == null)
            return null;

        return clob.getSubString(1,(int)clob.length());
    }

}
