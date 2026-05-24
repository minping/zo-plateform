package com.common.json;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Timestamp;
import java.util.Date;
import java.util.Map;

/**
 * @author zhoump
 * @date 2026/5/18
 * @purpose
 */
public interface IJson {
    /**
     * 获取Boolean类型的值
     *
     * @param key
     * @return
     */
    Boolean getBoolean(String key);

    /**
     * 获取boolean值
     *
     * @param key
     * @return
     */
    boolean getBooleanValue(String key);

    /**
     * 获取Integer对象
     *
     * @param key
     * @return
     */
    Integer getInteger(String key);

    /**
     * 获取int值
     *
     * @param key
     * @return
     */
    int getIntValue(String key);

    /**
     * 获取Long类型
     *
     * @param key
     * @return
     */
    Long getLong(String key);

    /**
     * 获取long值
     *
     * @param key
     * @return
     */
    long getLongValue(String key);

    /**
     * 获取Float对象
     *
     * @param key
     * @return
     */
    Float getFloat(String key);

    /**
     * 获取float值
     *
     * @param key
     * @return
     */
    float getFloatValue(String key);

    /**
     * 获取Double对象
     *
     * @param key
     * @return
     */
    Double getDouble(String key);

    /**
     * 获取double值
     *
     * @param key
     * @return
     */
    double getDoubleValue(String key);

    /**
     * 获取BigDecimal对象
     *
     * @param key
     * @return
     */
    BigDecimal getBigDecimal(String key);

    /**
     * 获取BigInteger对象
     *
     * @param key
     * @return
     */
    BigInteger getBigInteger(String key);

    /**
     * 获取字符串
     *
     * @param key
     * @return
     */
    String getString(String key);

    /**
     * 获取java.util.Date
     *
     * @param key
     * @return
     */
    Date getDate(String key);

    /**
     * 获取java.sql.Date
     *
     * @param key
     * @return
     */
    java.sql.Date getSqlDate(String key);

    /**
     * 获取Timestamp
     *
     * @param key
     * @return
     */
    Timestamp getTimestamp(String key);

    /**
     * fluentPut
     *
     * @param key
     * @param value
     * @return
     */
    JSONObject fluentPut(String key, Object value);


    /**
     * fluent putAll map
     *
     * @param m
     * @return
     */
    JSONObject fluentPutAll(Map<? extends String, ? extends Object> m);

    /**
     * 转换string
     *
     * @return
     */
    String toJSONString();

    /**
     * 获取JSONObject
     *
     * @param key
     * @return
     */
    JSONObject getJSONObject(String key);

    /**
     * 获取JSONArray
     *
     * @param key
     * @return
     */
    JSONArray getJSONArray(String key);
}
