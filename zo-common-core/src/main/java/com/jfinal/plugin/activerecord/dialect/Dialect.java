package com.jfinal.plugin.activerecord.dialect;

import com.common.util.StrKit;
import com.jfinal.dialect.date.Format;
import com.jfinal.dialect.date.Patten;
import com.jfinal.kit.JdbcUrlKit;
import com.jfinal.plugin.activerecord.*;
import com.jfinal.plugin.activerecord.builder.KeepByteAndShortModelBuilder;
import com.jfinal.plugin.activerecord.builder.KeepByteAndShortRecordBuilder;
import com.jfinal.plugin.activerecord.builder.i.IModelBuilder;

import java.math.BigInteger;
import java.sql.*;
import java.time.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * @author zhoump
 * @date 2026/7/14
 * @purpose
 */
public abstract class Dialect implements IMatchDatabase{

    // 指示 Generator、ModelBuilder、RecordBuilder 是否保持住 Byte、Short 类型
    protected boolean keepByteAndShort = false;
    protected ModelBuilder modelBuilder;
    protected RecordBuilder recordBuilder;

    public Dialect(){
        modelBuilder = ModelBuilder.newInstance(this);
        recordBuilder = RecordBuilder.newInstance(this);
    }
    /**
     * jdbc url
     */
    protected String jdbcUrl;

    /**
     * 数据库名称
     */
    protected String dbName;

    /**
     * 数据库用户
     */
    protected String userName;

    static List<Patten> pattenList = new ArrayList<>();

    static{
        //2020-10-11
        //年2020
        pattenList.add(Patten.by("yyyy","%Y"));
        //年2020
        pattenList.add(Patten.by("YYYY","%Y"));
        //年20
        pattenList.add(Patten.by("yy","%y"));
        //年20
        pattenList.add(Patten.by("YY","%y"));
        //月10
        pattenList.add(Patten.by("MM","%m"));
        //日11
        pattenList.add(Patten.by("dd","%d"));
        //星期几
        pattenList.add(Patten.by("E","%w"));
        //24小时制  小时
        pattenList.add(Patten.by("HH","%H"));
        //12小时制  小时
        pattenList.add(Patten.by("hh","%h"));
        //分钟
        pattenList.add(Patten.by("mm","%i"));
        //秒
        pattenList.add(Patten.by("ss","%s"));
        //毫秒
        pattenList.add(Patten.by("SSS","%f"));
    }

    /**
     * 获取方言标识
     * @return
     */
    public abstract String key();

    // Methods for common
    public abstract String forTableBuilderDoBuild(String tableName);

    public abstract String forPaginate(int pageNumber, int pageSize, StringBuilder findSql);

    /**
     * 通过偏移量直接构建分页sql
     * @param offset 偏移量
     * @param pageSize 查询多少数据
     * @param findSql 查询sql
     * @return sql
     */
    public abstract String forPaginateByOffset(int offset, int pageSize, StringBuilder findSql);

    // Methods for Model
    public abstract String forModelFindById(Table table, String columns);

    public abstract String forModelDeleteById(Table table);

    public abstract void forModelSave(Table table, Map<String, Object> attrs, StringBuilder sql, List<Object> paras);

    public abstract void forModelUpdate(Table table, Map<String, Object> attrs, Set<String> modifyFlag, StringBuilder sql, List<Object> paras);

    // Methods for DbPro. Do not delete the String[] pKeys parameter, the element of pKeys needs to trim()
    public abstract String forDbFindById(String tableName, String[] pKeys);

    public abstract String forDbDeleteById(String tableName, String[] pKeys);

    public abstract void forDbSave(String tableName, String[] pKeys, Record record, StringBuilder sql, List<Object> paras);

    public abstract void forDbUpdate(String tableName, String[] pKeys, Object[] ids, Record record, StringBuilder sql, List<Object> paras);

    /**
     * 指示 Generator、ModelBuilder、RecordBuilder 是否保持住 Byte、Short 类型
     */
    public Dialect setKeepByteAndShort(boolean keepByteAndShort) {
        this.keepByteAndShort = keepByteAndShort;
        /**
         * 内部的 4 个 if 判断是为了避免替换掉用户通过 setModelBuilder(...)
         * setRecordBuilder(...) 配置的自定义 builder
         */
        if (keepByteAndShort) {
            if (modelBuilder.getClass() == ModelBuilder.class) {
                modelBuilder = KeepByteAndShortModelBuilder.me;
            }
            if (recordBuilder.getClass() == RecordBuilder.class) {
                recordBuilder = KeepByteAndShortRecordBuilder.me;
            }
        } else {
            if (modelBuilder.getClass() == KeepByteAndShortModelBuilder.class) {
                modelBuilder = ModelBuilder.newInstance(this);
            }
            if (recordBuilder.getClass() == KeepByteAndShortRecordBuilder.class) {
                recordBuilder = RecordBuilder.newInstance(this);
            }
        }
        return this;
    }

    /**
     * 指示 MetaBuilder 生成的 ColumnMeta.javaType 是否保持住 Byte、Short 类型
     * 进而 BaseModelBuilder 生成针对 Byte、Short 类型的获取方法：
     * getByte(String)、getShort(String)
     */
    public boolean isKeepByteAndShort() {
        return keepByteAndShort;
    }

    /**
     * 配置自定义 ModelBuilder
     *
     * 通过继承扩展 ModelBuilder 可以对 JDBC 到 java 数据类型进行定制化转换
     * 不同数据库从 JDBC 到 java 数据类型的映射关系有所不同
     *
     * 此外，还可以通过改变 ModelBuilder.buildLabelNamesAndTypes()
     * 方法逻辑，实现下划线字段名转驼峰变量名的功能
     */
    public Dialect setModelBuilder(ModelBuilder modelBuilder) {
        this.modelBuilder = modelBuilder;
        return this;
    }

    /**
     * 配置自定义 RecordBuilder
     *
     * 通过继承扩展 RecordBuilder 可以对 JDBC 到 java 数据类型进行定制化转换
     * 不同数据库从 JDBC 到 java 数据类型的映射关系有所不同
     *
     * 此外，还可以通过改变 RecordBuilder.buildLabelNamesAndTypes()
     * 方法逻辑，实现下划线字段名转驼峰变量名的功能
     */
    public Dialect setRecordBuilder(RecordBuilder recordBuilder) {
        this.recordBuilder = recordBuilder;
        return this;
    }

    @SuppressWarnings("rawtypes")
    public <T> List<T> buildModelList(ResultSet rs, Class<? extends Model> modelClass) throws SQLException, ReflectiveOperationException {
        return modelBuilder.build(rs, modelClass);
    }

    @SuppressWarnings("rawtypes")
    public <T> List<T> buildModelList(ResultSet rs, Class<? extends Model> modelClass, IModelBuilder iModelBuilder) throws SQLException, ReflectiveOperationException {
        return modelBuilder.build(rs, modelClass,iModelBuilder);
    }

    public List<Record> buildRecordList(Config config, ResultSet rs) throws SQLException {
        return recordBuilder.build(config, rs);
    }

    public List<Record> buildRecordList(Config config, ResultSet rs, IRecordBuilder iRecordBuilder) throws SQLException {
        return recordBuilder.build(config, rs, iRecordBuilder);
    }

    public List<Record> buildTreeRecordList(Config config, ResultSet rs, TreeInfo treeInfo) throws SQLException {
        return recordBuilder.buildTree(config, rs, treeInfo);
    }


    /**
     * mysql 数据库的  bigint unsigned 对应的 java 类型为 BigInteger
     * 但是 rs.getObject(1) 返回值为 Long 型，造成 model.save() 以后
     * model.getId() 时的类型转换异常
     */
    protected void processGeneratedBigIntegerKey(Model<?> model, String pKey, Object v) {
        if (v instanceof BigInteger) {
            model.set(pKey, (BigInteger) v);
        } else if (v instanceof Number) {
            Number n = (Number) v;
            model.set(pKey, BigInteger.valueOf(n.longValue()));
        } else {
            model.set(pKey, v);
        }
    }

    /**
     * 用于获取 Db.save(tableName, record) 以后自动生成的主键值，可通过覆盖此方法实现更精细的控制
     * 目前只有 PostgreSqlDialect，覆盖过此方法
     */
    public void getRecordGeneratedKey(PreparedStatement pst, Record record, String[] pKeys) throws SQLException {
        //        ResultSet rs = pst.getGeneratedKeys();
        //        for (String pKey : pKeys) {
        //            if (record.get(pKey) == null || isOracle()) {
        //                if (rs.next()) {
        //                    record.set(pKey, rs.getObject(1));    // It returns Long for int colType for mysql
        //                }
        //            }
        //        }
        //        rs.close();
    }

    public boolean isOracle() {
        return false;
    }

    public boolean isTakeOverDbPaginate() {
        return false;
    }

    public Page<Record> takeOverDbPaginate(Connection conn, int pageNumber, int pageSize, Boolean isGroupBySql, String totalRowSql, StringBuilder findSql, Object... paras) throws SQLException {
        throw new RuntimeException("You should implements this method in " + getClass().getName());
    }

    public boolean isTakeOverModelPaginate() {
        return false;
    }

    @SuppressWarnings("rawtypes")
    public Page takeOverModelPaginate(Connection conn, Class<? extends Model> modelClass, int pageNumber, int pageSize, Boolean isGroupBySql, String totalRowSql, StringBuilder findSql, Object... paras) throws Exception {
        throw new RuntimeException("You should implements this method in " + getClass().getName());
    }

    public void fillStatement(PreparedStatement pst, List<Object> paras) throws SQLException {
        //        for (int i = 0, size = paras.size(); i < size; i++) {
        //            pst.setObject(i + 1, paras.get(i));
        //        }
        fillStatementHandleDateType(pst,paras);
    }

    public void fillStatement(PreparedStatement pst, Object... paras) throws SQLException {
        //        for (int i = 0; i < paras.length; i++) {
        //            pst.setObject(i + 1, paras[i]);
        //        }
        fillStatementHandleDateType(pst,paras);
    }

    public String getDefaultPrimaryKey() {
        return "id";
    }

    public boolean isPrimaryKey(String colName, String[] pKeys) {
        for (String pKey : pKeys) {
            if (colName.equalsIgnoreCase(pKey)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 一、forDbXxx 系列方法中若有如下两种情况之一，则需要调用此方法对 pKeys 数组进行 trim():
     * 1：方法中调用了 isPrimaryKey(...)：为了防止在主键相同情况下，由于前后空串造成 isPrimaryKey 返回 false
     * 2：为了防止 tableName、colName 与数据库保留字冲突的，添加了包裹字符的：为了防止串包裹区内存在空串
     *   如 mysql 使用的 "`" 字符以及 PostgreSql 使用的 "\"" 字符
     * 不满足以上两个条件之一的 forDbXxx 系列方法也可以使用 trimPrimaryKeys(...) 方法让 sql 更加美观，但不是必须
     *
     * 二、forModelXxx 由于在映射时已经trim()，故不再需要调用此方法
     */
    public void trimPrimaryKeys(String[] pKeys) {
        for (int i = 0; i < pKeys.length; i++) {
            pKeys[i] = pKeys[i].trim();
        }
    }

    protected static class Holder {
        // "order\\s+by\\s+[^,\\s]+(\\s+asc|\\s+desc)?(\\s*,\\s*[^,\\s]+(\\s+asc|\\s+desc)?)*";
        private static final Pattern ORDER_BY_PATTERN = Pattern.compile(
                "order\\s+by\\s+[^,\\s]+(\\s+asc|\\s+desc)?(\\s*,\\s*[^,\\s]+(\\s+asc|\\s+desc)?)*",
                Pattern.CASE_INSENSITIVE | Pattern.MULTILINE);
    }

    public String replaceOrderBy(String sql) {
        return Holder.ORDER_BY_PATTERN.matcher(sql).replaceAll("");
    }

    /**
     * fillStatement 时处理日期类型
     */
    protected void fillStatementHandleDateType(PreparedStatement pst, List<Object> paras) throws SQLException {
        for (int i = 0, size = paras.size(); i < size; i++) {
            Object value = paras.get(i);
            if (value instanceof java.util.Date) {
                if (value instanceof Date) {
                    pst.setDate(i + 1, (Date) value);
                } else if (value instanceof Timestamp) {
                    pst.setTimestamp(i + 1, (Timestamp) value);
                } else {
                    // Oracle、SqlServer 中的 TIMESTAMP、DATE 支持 new Date() 给值
                    java.util.Date d = (java.util.Date) value;
                    pst.setTimestamp(i + 1, new Timestamp(d.getTime()));
                }
            } else if(value instanceof LocalDateTime){
                pst.setTimestamp(i+1, Timestamp.from(((LocalDateTime)value).toInstant(ZoneOffset.of("+8"))));
            } else if (value instanceof LocalDate){
                pst.setTimestamp(i+1,Timestamp.from( ((LocalDate)value).atStartOfDay(ZoneId.systemDefault()).toInstant()));
            }else if(value instanceof LocalTime){
                pst.setTime(i+1,Time.valueOf((LocalTime)value));
            } else {
                pst.setObject(i + 1, value);
            }
        }
    }

    /**
     * fillStatement 时处理日期类型
     */
    protected void fillStatementHandleDateType(PreparedStatement pst, Object... paras) throws SQLException {
        for (int i = 0; i < paras.length; i++) {
            Object value = paras[i];
            if (value instanceof java.util.Date) {
                if (value instanceof Date) {
                    pst.setDate(i + 1, (Date) value);
                } else if (value instanceof Timestamp) {
                    pst.setTimestamp(i + 1, (Timestamp) value);
                } else {
                    // Oracle、SqlServer 中的 TIMESTAMP、DATE 支持 new Date() 给值
                    java.util.Date d = (java.util.Date) value;
                    pst.setTimestamp(i + 1, new Timestamp(d.getTime()));
                }
            } else if(value instanceof LocalDateTime){
                pst.setTimestamp(i+1, Timestamp.from(((LocalDateTime)value).toInstant(ZoneOffset.of("+8"))));
            } else if (value instanceof LocalDate){
                pst.setTimestamp(i+1,Timestamp.from( ((LocalDate)value).atStartOfDay(ZoneId.systemDefault()).toInstant()));
            } else if(value instanceof LocalTime){
                pst.setTime(i+1,Time.valueOf((LocalTime)value));
            } else {
                pst.setObject(i + 1, value);
            }
        }
    }

    public String getJdbcUrl() {
        return jdbcUrl;
    }

    public Dialect setJdbcUrl(String jdbcUrl) {
        this.jdbcUrl = jdbcUrl;
        String dbJdbcUrl = jdbcUrl;

        int doubleSymbolIndex = dbJdbcUrl.indexOf("//");
        if (doubleSymbolIndex > 0) {
            dbJdbcUrl = dbJdbcUrl.substring(doubleSymbolIndex + 2);
            int symbolIndex = dbJdbcUrl.indexOf("/");
            if (symbolIndex > 0) {
                dbJdbcUrl = dbJdbcUrl.substring(symbolIndex + 1);
                int questionIndex = dbJdbcUrl.indexOf("?");
                if (questionIndex > 0) {
                    dbJdbcUrl = dbJdbcUrl.substring(0, questionIndex);
                }
                this.dbName = dbJdbcUrl;
            }
        }

        return this;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    /**
     * 获取数据库名
     * jdbc:mysql://[HOST]:[PORT]/[database_name]
     *
     * @return
     */
    public String getDbName() {
        return this.dbName;
    }

    /**
     * 时间格式化
     * @param columnName
     * @param patten
     * @return
     */
    public String dateFormat(String columnName,String patten){
        String analysis = Format.analysis(pattenList,patten);
        return StrKit.format("date_format({},'{}')",columnName,analysis);
    }



    public SqlPara leftLike(String columnName, String param) {
        SqlPara sqlPara = new SqlPara();
        sqlPara.setSql(columnName + " like concat(? , '%') ");
        sqlPara.addPara(param);
        return sqlPara;
    }

    public SqlPara rightLike(String columnName, String param) {
        SqlPara sqlPara = new SqlPara();
        sqlPara.setSql(columnName + " like concat(? , '%') ");
        sqlPara.addPara(param);
        return sqlPara;
    }

    public SqlPara allLike(String columnName, String param) {
        SqlPara sqlPara = new SqlPara();
        sqlPara.setSql(columnName + " like concat(? , '%') ");
        sqlPara.addPara(param);
        return sqlPara;
    }

    /**
     * 截取jdbc url 参数
     * @param url
     * @param name
     * @return
     */
    public String getParamByUrl(String url, String name) {
        return JdbcUrlKit.getParamByUrl(url, name);
    }
}
