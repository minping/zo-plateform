package com.jfinal.plugin.activerecord;

import java.util.Map;
import java.util.Set;

/**
 * @author zhoump
 * @date 2026/6/24
 * @purpose
 */
public interface IRecordBuilder {
    /**
     * 构建单行数据
     *
     * @param record
     * @return
     */
    default <R> R buildRecord(Record record) {
        return (R) record;
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
