package com.common.util;

import cn.hutool.core.collection.CollectionUtil;
import com.common.json.JSONObject;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.util.*;

/**
 * @author zhoump
 * @date 2026/6/21
 * @purpose
 */
public class TextValue  implements Serializable {

    /**
     * 多个值分隔符
     */
    public static final String SEPARATOR = "^";
    /**
     * 分隔符正则匹配字符串
     */
    public static final String SEPATATOR_REG = "\\^";

    private static final String TEXT = "text";

    private static final String VALUE = "value";

    private String text;
    private String value;

    public TextValue() {
    }

    public TextValue(String value, String text) {
        this.value = value;
        this.text = text;
    }

    /**
     * 获取连起来的text
     */
    public static String getText(List<TextValue> list) {
        StringBuilder re = new StringBuilder();
        if (list != null && list.size() != 0) {
            for (int i = 0; i < list.size(); ++i) {
                if (i > 0) {
                    re.append(SEPARATOR);
                }
                String text = list.get(i).getText();
                if (Objects.nonNull(text)){
                    re.append(text);
                }
            }

        }
        return re.toString();
    }

    /**
     * 获取连起来的value
     */
    public static String getValue(List<TextValue> list) {
        StringBuilder re = new StringBuilder();
        if (list != null && list.size() != 0) {
            for (int i = 0; i < list.size(); ++i) {
                if (i > 0) {
                    re.append(SEPARATOR);
                }
                String value = list.get(i).getValue();
                if (Objects.nonNull(value)){
                    re.append(value);
                }
            }
        }
        return re.toString();
    }

    /**
     * 拆分key value 为 list对象
     */
    public static List<TextValue> getList(String values, String texts) {
        if (values == null) {
            return new ArrayList();
        } else {
            String[] valuear = values.split(SEPATATOR_REG);
            String[] textar = texts.split(SEPATATOR_REG);
            List<TextValue> result = new ArrayList(valuear.length);
            for (int i = 0; i < valuear.length; ++i) {
                //value为空
                if (StringUtils.isBlank(textar[i])){
                    continue;
                }
                result.add(new TextValue(valuear[i], textar[i]));
            }

            return result;
        }
    }

    public static List<TextValue> getList(TextValue textValue) {
        if (textValue == null) {
            return new ArrayList<>();
        }
        return getList(textValue.getValue(), textValue.getText());
    }

    public static TextValue reduce(List<TextValue> list) {
        return new TextValue(getValue(list), getText(list));
    }

    public String getValue() {
        return this.value;
    }

    public String getText() {
        return this.text;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public void setText(String text) {
        this.text = text;
    }

    @Override
    public int hashCode() {
        if (this.value != null) {
            return value.hashCode();
        }
        return super.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (this.value != null && obj instanceof TextValue) {
            return this.value.equals(((TextValue) obj).getValue());
        }
        return super.equals(obj);
    }

    @Override
    public String toString() {
        return "{文本:" + this.text + ",值:" + this.value + "}";
    }


    public static boolean isEqual(List<TextValue> values1, List<TextValue> values2) {
        if (CollectionUtil.isEmpty(values1) || CollectionUtil.isEmpty(values2)) {
            return CollectionUtil.isEmpty(values1) && CollectionUtil.isEmpty(values2);
        }
        Set<TextValue> collect = new HashSet<>(values1);
        Set<TextValue> collect1 = new HashSet<>(values2);
        if (collect.size() != collect1.size()) {
            return false;
        }
        for (TextValue textValue : collect) {
            if (!collect1.contains(textValue)) {
                return false;
            }
        }
        return true;
    }

    public JSONObject toJSONObject(){
        JSONObject object = new JSONObject();
        object.put(TEXT,text);
        object.put(VALUE,value);
        return object;
    }
}
