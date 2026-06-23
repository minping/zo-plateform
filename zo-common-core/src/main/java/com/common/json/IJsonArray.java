package com.common.json;

import java.util.Collection;

/**
 * @author zhoump
 * @date 2026/5/18
 * @purpose
 */
public interface IJsonArray {
    JSONArray fluentAdd(Object e);

    JSONArray fluentRemove(Object o);

    JSONArray fluentAddAll(Collection<? extends Object> c);

    JSONArray fluentAddAll(int index, Collection<? extends Object> c);

    JSONArray fluentRemoveAll(Collection<?> c);

    JSONArray fluentRetainAll(Collection<?> c);

    JSONArray fluentClear();

    JSONArray fluentSet(int index, Object element);

    JSONArray fluentAdd(int index, Object element);

    JSONArray fluentRemove(int index);

    JSONObject getJSONObject(int index);

    JSONArray getJSONArray(int index);

    String getString(int index);

    /**
     * 转换string
     *
     * @return
     */
    String toJSONString();
}
