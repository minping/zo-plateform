package com.common.json;

import com.common.json.kit.JSONKit;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Timestamp;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * @author zhoump
 * @date 2026/5/18
 * @purpose
 */
public class JSONObject extends Json implements IJson, Map<String, Object>, Cloneable, Serializable {
    private static final long serialVersionUID = 1140721437187534960L;

    private static final int INIT = 16;

    private final Map<String, Object> map;


    public JSONObject() {
        this(16, false);
    }

    public JSONObject(Map<String, Object> map) {
        if (map == null) {
            throw new IllegalArgumentException("map is null.");
        } else {
            this.map = map;
        }
    }

    /**
     * 是否存在顺序
     *
     * @param ordered
     */
    public JSONObject(boolean ordered) {
        this(INIT, ordered);
    }

    /**
     * 初始容量
     *
     * @param initialCapacity
     */
    public JSONObject(int initialCapacity) {
        this(initialCapacity, false);
    }

    public JSONObject(int initialCapacity, boolean ordered) {
        if (ordered) {
            this.map = new LinkedHashMap(initialCapacity);
        } else {
            this.map = new HashMap(initialCapacity);
        }
    }

    @Override
    public Boolean getBoolean(String key) {
        Object value = this.map.get(key);
        return TypeUtils.castToBoolean(value);
    }

    @Override
    public boolean getBooleanValue(String key) {
        Boolean flag = this.getBoolean(key);
        return flag == null ? false : flag.booleanValue();
    }

    @Override
    public Integer getInteger(String key) {
        Object value = this.map.get(key);
        return TypeUtils.castToInt(value);
    }

    @Override
    public int getIntValue(String key) {
        Integer value = this.getInteger(key);
        return value == null ? 0 : value.intValue();
    }

    @Override
    public Long getLong(String key) {
        Object value = this.map.get(key);
        return TypeUtils.castToLong(value);
    }

    @Override
    public long getLongValue(String key) {
        Long value = this.getLong(key);
        return value == null ? 0 : value.longValue();
    }

    @Override
    public Float getFloat(String key) {
        Object value = this.map.get(key);
        return TypeUtils.castToFloat(value);
    }

    @Override
    public float getFloatValue(String key) {
        Float value = this.getFloat(key);
        return value == null ? 0 : value;
    }

    @Override
    public Double getDouble(String key) {
        Object value = this.map.get(key);
        return TypeUtils.castToDouble(value);
    }

    @Override
    public double getDoubleValue(String key) {
        Double value = this.getDouble(key);
        return value == null ? 0 : value.doubleValue();
    }

    @Override
    public BigDecimal getBigDecimal(String key) {
        Object value = this.map.get(key);
        return TypeUtils.castToBigDecimal(value);
    }

    @Override
    public BigInteger getBigInteger(String key) {
        Object value = this.map.get(key);
        return TypeUtils.castToBigInteger(value);
    }

    @Override
    public String getString(String key) {
        Object value = this.map.get(key);
        return TypeUtils.castToString(value);
    }

    @Override
    public Date getDate(String key) {
        Object value = this.map.get(key);
        return TypeUtils.castToDate(value);
    }

    @Override
    public java.sql.Date getSqlDate(String key) {
        Object value = this.map.get(key);
        return TypeUtils.castToSqlDate(value);
    }

    @Override
    public Timestamp getTimestamp(String key) {
        Object value = this.map.get(key);
        return TypeUtils.castToTimestamp(value);
    }

    @Override
    public JSONObject fluentPut(String key, Object value) {
        put(key, value);
        return this;
    }

    @Override
    public JSONObject fluentPutAll(Map<? extends String, ?> m) {
        this.putAll(m);

        return this;
    }

    @Override
    public String toJSONString() {
        return JSONKit.object2Json(this);
    }

    @Override
    public String toString() {
        return this.toJSONString();
    }

    @Override
    public void putAll(Map<? extends String, ?> m) {
        this.map.putAll(m);
    }

    @Override
    public void clear() {
        this.map.clear();
    }

    @Override
    public Object put(String key, Object value) {
        return this.map.put(key, value);
    }

    @Override
    public Object getOrDefault(Object key, Object defaultValue) {
        return this.map.getOrDefault(key, defaultValue);
    }

    @Override
    public int size() {
        return this.map.size();
    }

    @Override
    public boolean isEmpty() {
        return this.map.isEmpty();
    }

    @Override
    public boolean containsKey(Object key) {
        return this.map.containsKey(key);
    }

    @Override
    public boolean containsValue(Object value) {
        return this.map.containsValue(value);
    }

    @Override
    public Object get(Object key) {
        Object value = this.map.get(key);
        if (value instanceof Map) {
            return new JSONObject((Map) value);
        }
        if (value instanceof List) {
            return new JSONArray((List) value);
        }
        return value;
    }

    @Override
    public Object remove(Object key) {
        return this.map.remove(key);
    }

    @Override
    public Set<String> keySet() {
        return this.map.keySet();
    }

    @Override
    public Collection<Object> values() {
        return this.map.values();
    }

    @Override
    public Set<Entry<String, Object>> entrySet() {
        return this.map.entrySet();
    }

    @Override
    public void forEach(BiConsumer<? super String, ? super Object> action) {
        this.map.forEach(action);
    }

    @Override
    public void replaceAll(BiFunction<? super String, ? super Object, ?> function) {
        this.map.replaceAll(function);
    }

    @Override
    public Object putIfAbsent(String key, Object value) {
        return this.map.putIfAbsent(key, value);
    }

    @Override
    public boolean remove(Object key, Object value) {
        return this.map.remove(key, value);
    }

    @Override
    public boolean replace(String key, Object oldValue, Object newValue) {
        return this.map.replace(key, oldValue, newValue);
    }

    @Override
    public Object replace(String key, Object value) {
        return this.map.replace(key, value);
    }

    @Override
    public Object computeIfAbsent(String key, Function<? super String, ?> mappingFunction) {
        return this.map.computeIfAbsent(key, mappingFunction);
    }

    @Override
    public Object computeIfPresent(String key, BiFunction<? super String, ? super Object, ?> remappingFunction) {
        return this.map.computeIfPresent(key, remappingFunction);
    }

    @Override
    public Object compute(String key, BiFunction<? super String, ? super Object, ?> remappingFunction) {
        return this.map.compute(key, remappingFunction);
    }

    @Override
    public Object merge(String key, Object value, BiFunction<? super Object, ? super Object, ?> remappingFunction) {
        return this.map.merge(key, value, remappingFunction);
    }

    @Override
    public JSONObject getJSONObject(String key) {
        Object obj = this.get(key);
        if (obj == null) {
            return null;
        }
        if (obj instanceof Map) {
            return new JSONObject((Map<String, Object>) obj);
        } else if (obj instanceof String) {
            return parseObject((String) obj);
        }
        throw new JSONException("can not parse JSONObject [{" + obj.toString() + "}]");
    }

    @Override
    public JSONArray getJSONArray(String key) {
        Object value = this.map.get(key);
        if (value instanceof JSONArray) {
            return (JSONArray) value;
        } else if (value instanceof List) {
            return new JSONArray((List) value);
        } else {
            return value instanceof String ? parseArray((String) value) : (JSONArray) toJsonArray(value);
        }
    }

    @Override
    public Object clone() {
        return new JSONObject(this.map instanceof LinkedHashMap ? new LinkedHashMap(this.map) : new HashMap(this.map));
    }

}
