package com.common.json;

import com.common.json.kit.JSONKit;
import com.fasterxml.jackson.core.type.TypeReference;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author zhoump
 * @date 2026/5/18
 * @purpose
 */
public class Json {

    public Object toJson(Object javaObject) {
        if (javaObject == null) {
            return null;
        } else if (javaObject instanceof Json) {
            return javaObject;
        } else {
            return parseObject(JSONKit.object2Json(javaObject));
        }
    }

    public Object toJsonArray(Object javaObject) {
        if (javaObject == null) {
            return null;
        } else if (javaObject instanceof Json) {
            return javaObject;
        } else {
            return parseArray(JSONKit.object2Json(javaObject));
        }
    }

    public static JSONObject parseObject(String text) {
        return parseObject(text, new TypeReference<HashMap<String, Object>>() {
        });
    }

    public static JSONObject parseObject(String text, TypeReference<? extends Map> typeReference) {
        if (JSONKit.isBlank(text)) {
            return null;
        }
        return new JSONObject(JSONKit.json2Object(text, typeReference));
    }

    public static JSONArray parseArray(String text) {
        return parseArray(text, new TypeReference<List<Object>>() {
        });
    }

    public static JSONArray parseArray(String text, TypeReference<List<Object>> typeReference) {
        if (JSONKit.isBlank(text)) {
            return null;
        }
        return new JSONArray(JSONKit.json2Object(text, typeReference));
    }

}
