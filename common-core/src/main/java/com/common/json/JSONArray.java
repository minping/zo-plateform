package com.common.json;

import com.common.json.kit.JSONKit;

import java.io.Serializable;
import java.util.*;

/**
 * @author zhoump
 * @date 2026/5/18
 * @purpose
 */
public class JSONArray extends Json implements List<Object>, IJsonArray, Cloneable, Serializable {

    private static final long serialVersionUID = 6715414935903715348L;
    private final List<Object> list;

    public JSONArray() {
        this.list = new ArrayList();
    }

    public JSONArray(List<Object> list) {
        if (list == null) {
            throw new IllegalArgumentException("list is null.");
        } else {
            this.list = list;
        }
    }

    public JSONArray(int initialCapacity) {
        this.list = new ArrayList(initialCapacity);
    }

    @Override
    public int size() {
        return this.list.size();
    }

    @Override
    public boolean isEmpty() {
        return this.list.isEmpty();
    }

    @Override
    public boolean contains(Object o) {
        return this.list.contains(o);
    }

    @Override
    public Iterator<Object> iterator() {
        return this.list.iterator();
    }

    @Override
    public Object[] toArray() {
        return this.list.toArray();
    }

    @Override
    public <T> T[] toArray(T[] a) {
        return this.list.toArray(a);
    }

    @Override
    public boolean add(Object o) {
        return this.list.add(o);
    }

    @Override
    public boolean remove(Object o) {
        return this.list.remove(o);
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        return this.list.containsAll(c);
    }

    @Override
    public boolean addAll(Collection<?> c) {
        return this.list.addAll(c);
    }

    @Override
    public boolean addAll(int index, Collection<?> c) {
        return this.list.addAll(index, c);
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        return this.list.removeAll(c);
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        return this.list.retainAll(c);
    }

    @Override
    public void clear() {
        this.list.clear();
    }

    @Override
    public Object get(int index) {
        Object value = this.list.get(index);
        if (value instanceof Map) {
            return new JSONObject((Map) value);
        }
        return value;
    }

    @Override
    public String getString(int index) {
        Object value = this.get(index);
        return TypeUtils.castToString(value);
    }

    @Override
    public Object set(int index, Object element) {
        if (index == -1) {
            this.list.add(element);
            return null;
        } else if (this.list.size() > index) {
            return this.list.set(index, element);
        } else {
            for (int i = this.list.size(); i < index; ++i) {
                this.list.add((Object) null);
            }
            this.list.add(element);
            return null;
        }
    }

    @Override
    public void add(int index, Object element) {
        this.list.add(index, element);
    }

    @Override
    public Object remove(int index) {
        return this.list.remove(index);
    }

    @Override
    public int indexOf(Object o) {
        return this.list.indexOf(o);
    }

    @Override
    public int lastIndexOf(Object o) {
        return this.list.lastIndexOf(o);
    }

    @Override
    public ListIterator<Object> listIterator() {
        return this.list.listIterator();
    }

    @Override
    public ListIterator<Object> listIterator(int index) {
        return this.list.listIterator(index);
    }

    @Override
    public List<Object> subList(int fromIndex, int toIndex) {
        return this.list.subList(fromIndex, toIndex);
    }

    @Override
    public JSONArray fluentAdd(Object e) {
        this.list.add(e);
        return this;
    }

    @Override
    public JSONArray fluentRemove(Object o) {
        this.list.remove(o);
        return this;
    }

    @Override
    public JSONArray fluentAddAll(Collection<? extends Object> c) {
        this.list.addAll(c);
        return this;
    }

    @Override
    public JSONArray fluentAddAll(int index, Collection<? extends Object> c) {
        this.list.addAll(index, c);
        return this;
    }

    @Override
    public JSONArray fluentRemoveAll(Collection<?> c) {
        this.list.removeAll(c);
        return this;
    }

    @Override
    public JSONArray fluentRetainAll(Collection<?> c) {
        this.list.retainAll(c);
        return this;
    }

    @Override
    public JSONArray fluentClear() {
        this.list.clear();
        return this;
    }

    @Override
    public JSONArray fluentSet(int index, Object element) {
        this.set(index, element);
        return this;
    }

    @Override
    public JSONArray fluentAdd(int index, Object element) {
        this.list.add(index, element);
        return this;
    }

    @Override
    public JSONArray fluentRemove(int index) {
        this.list.remove(index);
        return this;
    }

    @Override
    public JSONObject getJSONObject(int index) {
        Object value = this.list.get(index);
        if (value instanceof JSONObject) {
            return (JSONObject) value;
        } else {
            return value instanceof Map ? new JSONObject((Map) value) : (JSONObject) toJson(value);
        }
    }

    @Override
    public JSONArray getJSONArray(int index) {
        Object value = this.list.get(index);
        if (value instanceof JSONArray) {
            return (JSONArray) value;
        } else {
            return value instanceof List ? new JSONArray((List) value) : (JSONArray) toJsonArray(value);
        }
    }

    @Override
    public String toJSONString() {
        return JSONKit.object2Json(this.list);
    }

    @Override
    public String toString() {
        return this.toJSONString();
    }

    @Override
    public Object clone() {
        return new JSONArray(new ArrayList(this.list));
    }
}
