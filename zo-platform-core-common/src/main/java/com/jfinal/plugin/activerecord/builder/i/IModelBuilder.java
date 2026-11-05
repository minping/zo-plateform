package com.jfinal.plugin.activerecord.builder.i;

import java.util.Map;
import java.util.Set;

/**
 * @author zhoump
 * @date 2026/7/14
 * @purpose
 */
public interface IModelBuilder {

    /**
     * 构建单行数据
     *
     * @param map
     * @return
     */
    default Map<String,Object> buildRecord(Map<String,Object> map) {
        return map;
    }

    /**
     * 构建单列数据
     *
     * @param columns
     * @param columnName
     * @return 返回对应columnName的值
     */
    default void putValue(Map columns, String columnName, Object columnValue) {
        columns.put(columnName, columnValue);
    }


    /**
     * 获取加密的列
     *
     * @return {@link Set}<{@link String}>
     */
    default Set<String>  getEncColumns(){
        return null;
    }
}
