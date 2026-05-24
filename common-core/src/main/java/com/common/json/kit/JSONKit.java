package com.common.json.kit;

import com.common.json.JSONArray;
import com.common.json.JSONObject;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * @author zhoump
 * @date 2026/5/18
 * @purpose
 */
public class JSONKit {
    private static ObjectMapper mapper = new ObjectMapper();

    static {
        // 对于空的对象转json的时候不抛出错误
        mapper.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
        // 设置输出时包含属性的风格 属性值为null的不参与序列化
        //        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        // 设置输入时忽略JSON字符串中存在而Java对象实际没有的属性
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        // 允许属性名称没有引号
        mapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
        // 允许单引号
        mapper.configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true);
    }

    /**
     * 获取有顺序的json对象
     * 缺点:会有部分性能损耗
     *
     * @param jsonStr
     * @return
     */
    public static JSONObject sortJSON(String jsonStr) {
        return new JSONObject(JSONObject.parseObject(jsonStr, new TypeReference<LinkedHashMap<String, Object>>() {
        }));
    }


    public static String object2Json(Object o) {
        if (o == null) {
            return null;
        }
        String s = null;

        try {
            s = mapper.writeValueAsString(o);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return s;
    }

    public static <T> List<String> listObject2ListJson(List<T> objects) {
        if (objects == null) {
            return null;
        }
        List<String> lists = new ArrayList<>();
        for (T t : objects) {
            lists.add(JSONKit.object2Json(t));
        }

        return lists;
    }

    public static <T> List<T> listJson2ListObject(List<String> jsons, Class<T> c) {
        if (jsons == null) {
            return null;
        }


        List<T> ts = new ArrayList<T>();
        for (String j : jsons) {
            ts.add(JSONKit.json2Object(j, c));
        }

        return ts;
    }

    public static <T> T json2Object(String json, Class<T> c) {
        if (isBlank(json)) {
            return null;
        }
        T t = null;
        try {
            t = mapper.readValue(json, c);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return t;
    }

    public static <T> T json2Object(String json, TypeReference<T> tr) {
        if (isBlank(json)) {
            return null;
        }
        T t = null;
        try {
            t = mapper.readValue(json, tr);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return t;
    }

    public static JSONObject by(String key, Object value) {
        JSONObject jsonObject = create();
        jsonObject.put(key, value);
        return jsonObject;
    }

    public static JSONObject create() {
        return new JSONObject();
    }

    public static JSONArray createArray() {
        return new JSONArray();
    }


    /**
     * 字符串为 null 或者内部字符全部为 ' ' '\t' '\n' '\r' 这四类字符时返回 true
     */
    public static boolean isBlank(String str) {
        if (str == null) {
            return true;
        }
        int len = str.length();
        if (len == 0) {
            return true;
        }
        for (int i = 0; i < len; i++) {
            switch (str.charAt(i)) {
                case ' ':
                case '\t':
                case '\n':
                case '\r':
                    break;
                default:
                    return false;
            }
        }
        return true;
    }

}
