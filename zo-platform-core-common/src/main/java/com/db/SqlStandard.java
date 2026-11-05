package com.db;

import cn.hutool.core.bean.BeanUtil;
import com.jfinal.kit.ClassKit;
import com.jfinal.kit.ReflectKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.IRecordBuilder;
import com.jfinal.plugin.activerecord.Model;
import com.jfinal.plugin.activerecord.Record;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * @author zhoump
 * @date 2026/6/24
 * @purpose
 */
public interface SqlStandard {

    /**
     * 获取完整执行sql
     *
     * @return: 执行sql
     * @author: zxl
     * @time: 2021/2/20 13:55
     */
    default String sql() {
        String sql = this.getSql();
        String namespace = this.getNameSpace();
        return namespace + "." + sql;
    }

    /**
     * 执行查询sql,返回数据集合
     *
     * @return: List<Map>
     * @author: zxl
     * @time: 2021/2/20 13:55
     */
    default List<Map> find() {
        return find(null, Record::getColumns, null);
    }

    /**
     * 执行查询sql,返回数据集合
     *
     * @param classes:返回bean类型class
     * @return: List<B>
     * @author: zxl
     * @time: 2021/2/20 13:55
     */
    default <B> List<B> find(Class<B> classes) {
        return find(null, classes);
    }

    /**
     * 执行查询sql,返回数据集合
     *
     * @param classes:返回bean类型class
     * @param increase:结果转换后续处理
     * @return: List<B>
     * @author: zxl
     * @time: 2021/2/20 13:55
     */
    default <B> List<B> find(Class<B> classes, Function<? super B, ? extends B> increase) {
        return find(null, classes, increase);
    }

    /**
     * 执行查询sql,返回数据集合
     *
     * @param bean:提供T对象（例如工厂）
     * @return: List<B>
     * @author: zxl
     * @time: 2021/2/20 13:55
     */
    default <B> List<B> find(Supplier<B> bean) {
        return find(null, bean);
    }

    /**
     * 执行查询sql,返回数据集合
     *
     * @param bean:提供T对象（例如工厂）
     * @param increase:结果转换后续处理
     * @return: List<B>
     * @author: zxl
     * @time: 2021/2/20 13:55
     */
    default <B> List<B> find(Supplier<B> bean, Function<? super B, ? extends B> increase) {
        return find(null, bean, increase);
    }

    /**
     * 执行查询sql,返回数据集合
     *
     * @param mapper:结果转换映射
     * @return: List<B>
     * @author: zxl
     * @time: 2021/2/20 13:55
     */
    default <B> List<B> find(Function<? super Record, ? extends B> mapper) {
        return find(null, mapper, null);
    }

    /**
     * 执行查询sql,返回数据集合
     *
     * @param mapper:结果转换映射
     * @param increase:结果转换后续处理
     * @return: List<B>
     * @author: zxl
     * @time: 2021/2/20 13:55
     */
    default <B> List<B> find(Function<? super Record, ? extends B> mapper, Function<? super B, ? extends B> increase) {
        return find(null, mapper, increase);
    }

    /**
     * 执行查询sql,返回数据集合
     *
     * @param param:sql参数
     * @return: List<Map>
     * @author: zxl
     * @time: 2021/2/20 13:55
     */
    default List<Map> find(Map param) {
        return find(param, Record::getColumns, null);
    }

    /**
     * 执行查询sql,返回数据集合
     *
     * @param param:sql参数
     * @param increase:结果转换后续处理
     * @return: List<Map>
     * @author: zxl
     * @time: 2021/2/20 13:55
     */
    default List<Map> find(Map param, Function<? super Map, ? extends Map> increase) {
        return find(param, Record::getColumns, increase);
    }

    /**
     * 执行查询sql,返回数据集合
     *
     * @param param:sql参数
     * @param classes:返回bean类型class
     * @return: List<B>
     * @author: zxl
     * @time: 2021/2/20 13:55
     */
    default <B> List<B> find(Map param, Class<B> classes) {
        return find(param, classes, null);
    }

    /**
     * 执行查询sql,返回数据集合
     *
     * @param param:sql参数
     * @param classes:返回bean类型class
     * @param increase:结果转换后续处理
     * @return: List<B>
     * @author: zxl
     * @time: 2021/2/20 13:55
     */
    default <B> List<B> find(Map param, Class<B> classes, Function<? super B, ? extends B> increase) {
        final Boolean isModel = ClassKit.equalsClass(classes, Model.class);
        final Boolean isRecord = classes.isAssignableFrom(Record.class);
        return find(param, record -> {
                    if (isModel) {
                        Model m = (Model) ReflectKit.newInstance(classes);
                        m.put(record);
                        return (B) m;
                    } else if (isRecord) {
                        return (B) record;
                    }
                    return BeanUtil.mapToBean(record.getColumns(), classes, true);
                },
                increase);
    }

    /**
     * 执行查询sql,返回数据集合
     *
     * @param param:sql参数
     * @param bean:提供T对象（例如工厂）
     * @return: List<B>
     * @author: zxl
     * @time: 2021/2/20 13:55
     */
    default <B> List<B> find(Map param, Supplier<B> bean) {
        return find(param, bean, null);
    }

    /**
     * 执行查询sql,返回数据集合
     *
     * @param param:sql参数
     * @param bean:提供T对象（例如工厂）
     * @param increase:结果转换后续处理
     * @return: List<B>
     * @author: zxl
     * @time: 2021/2/20 13:55
     */
    default <B> List<B> find(Map param, Supplier<B> bean, Function<? super B, ? extends B> increase) {
        return find(param, record -> {
            B result = bean.get();
            if (result instanceof Model) {
                ((Model<?>) result).put(record);
            } else if (result instanceof Record) {
                ((Record) bean).setColumns(record);
            } else {
                result = BeanUtil.fillBeanWithMap(record.getColumns(), result, true);
            }
            return result;
        }, increase);
    }

    /**
     * 执行查询sql,返回数据集合
     *
     * @param param:sql参数
     * @param mapper:结果转换映射
     * @param increase:结果转换后续处理
     * @return: List<B>
     * @author: zxl
     * @time: 2021/2/20 13:55
     */
    default <B> List<B> find(Map param, Function<? super Record, ? extends B> mapper, Function<? super B, ? extends B> increase) {
        List<B> beanList = new ArrayList<>();
        IRecordBuilder recordBuilder = new IRecordBuilder() {
            @Override
            public <R> R buildRecord(Record record) {
                B bean = mapper.apply(record);
                if (increase != null) {
                    bean = increase.apply(bean);
                }
                beanList.add(bean);
                return null;
            }
        };
        if (param != null) {
            Db.find(Db.getSqlPara(sql(), param), recordBuilder);
        } else {
            Db.find(Db.getSqlPara(sql()), recordBuilder);
        }
        return beanList;
    }

    /**
     * 执行查询sql,返回第一行数据
     *
     * @return: Map
     * @author: zxl
     * @time: 2021/2/20 13:55
     */
    default Map findFirst() {
        return findFirst(null, Record::getColumns);
    }

    /**
     * 执行查询sql,返回第一行数据
     *
     * @param classes:返回bean类型class
     * @return: Map
     * @author: zxl
     * @time: 2021/2/20 13:55
     */
    default <B> B findFirst(Class<B> classes) {
        return findFirst(null, classes);
    }

    /**
     * 执行查询sql,返回第一行数据
     *
     * @param bean:提供T对象（例如工厂）
     * @return: Map
     * @author: zxl
     * @time: 2021/2/20 13:55
     */
    default <B> B findFirst(Supplier<B> bean) {
        return findFirst(null, bean);
    }

    /**
     * 执行查询sql,返回第一行数据
     *
     * @param bean:返回bean对象
     * @return: Map
     * @author: zxl
     * @time: 2021/2/20 13:55
     */
    default <B> B findFirst(B bean) {
        return findFirst(null, bean);
    }

    /**
     * 执行查询sql,返回第一行数据
     *
     * @param mapper:结果转换映射
     * @return: Map
     * @author: zxl
     * @time: 2021/2/20 13:55
     */
    default <B> B findFirst(Function<? super Record, ? extends B> mapper) {
        return findFirst(null, mapper);
    }

    /**
     * 执行查询sql,返回第一行数据
     *
     * @param param:sql参数
     * @return: Map
     * @author: zxl
     * @time: 2021/2/20 13:55
     */
    default Map findFirst(Map param) {
        return findFirst(param, Record::getColumns);
    }

    /**
     * 执行查询sql,返回第一行数据
     *
     * @param param:sql参数
     * @param classes:返回bean类型class
     * @return: <B> B
     * @author: zxl
     * @time: 2021/2/20 13:55
     */
    default <B> B findFirst(Map param, Class<B> classes) {
        final Boolean isModel = ClassKit.equalsClass(classes, Model.class);
        final Boolean isRecord = classes.isAssignableFrom(Record.class);
        return findFirst(param, record -> {
            if (isModel) {
                Model m = (Model) ReflectKit.newInstance(classes);
                m.put(record);
                return (B) m;
            } else if (isRecord) {
                return (B) record;
            }
            return BeanUtil.mapToBean(record.getColumns(), classes, true);
        });
    }

    /**
     * 执行查询sql,返回第一行数据
     *
     * @param param:sql参数
     * @param bean:提供T对象（例如工厂）
     * @return: <B> B
     * @author: zxl
     * @time: 2021/2/20 13:55
     */
    default <B> B findFirst(Map param, Supplier<B> bean) {
        return findFirst(param, bean.get());
    }

    /**
     * 执行查询sql,返回第一行数据
     *
     * @param param:sql参数
     * @param bean:返回bean对象
     * @return: <B> B
     * @author: zxl
     * @time: 2021/2/20 13:55
     */
    default <B> B findFirst(Map param, B bean) {
        return findFirst(param, record -> {
                    if (bean instanceof Model) {
                        return (B) ((Model) bean).put(record);
                    } else if (bean instanceof Record) {
                        return (B) record;
                    }
                    return BeanUtil.fillBeanWithMap(record.getColumns(), bean, true);
                }
        );
    }

    /**
     * 执行查询sql,返回第一行数据
     *
     * @param param:sql参数
     * @param mapper:结果转换映射
     * @return: <B> B
     * @author: zxl
     * @time: 2021/2/20 13:55
     */
    default <B> B findFirst(Map param, Function<? super Record, ? extends B> mapper) {
        Record record;
        if (param != null) {
            record = Db.findFirst(Db.getSqlPara(sql(), param));
        } else {
            record = Db.findFirst(Db.getSql(sql()));
        }
        if (record != null) {
            return mapper.apply(record);
        }
        return null;
    }

    /**
     * 修改操作(不需参数)
     *
     * @return: 执行sql
     * @author: zxl
     * @time: 2021/2/20 13:55
     */
    default int update() {
        return Db.update(Db.getSql(sql()));
    }

    /**
     * 修改操作
     *
     * @param param:sql参数
     * @return: 执行sql
     * @author: zxl
     * @time: 2021/2/20 13:55
     */
    default int update(Map param) {
        return Db.update(Db.getSqlPara(sql(), param));
    }

    /**
     * sql的getter方法
     *
     * @return: sql
     * @author: zxl
     * @time: 2021/2/20 13:55
     */
    default String getSql() {
        return this.toString();
    }

    /**
     * nameSpace的getter方法
     *
     * @return: nameSpace
     * @author: zxl
     * @time: 2021/2/20 13:55
     */
    default String getNameSpace() {
        return NamespaceContext.newInstance().getNamespace(this.getClass());
    }
}
