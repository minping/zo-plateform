package com.zo.platform.starter.redis.serializer;

/**
 * @author zhoump
 * @date 2026/11/4
 * @purpose
 */

import cn.hutool.core.util.ReflectUtil;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.jsontype.impl.LaissezFaireSubTypeValidator;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

/**
 * 统一序列化器工厂，保证RedisTemplate和CacheManager序列化规则一致
 */
public class RedisSerializerFactory {

    /**
     * Key序列化器：纯字符串
     */
    public static StringRedisSerializer stringKeySerializer() {
        return new StringRedisSerializer();
    }

    /**
     * Value序列化器：Jackson JSON，带类型信息，支持所有实体类反序列化
     */
    public static GenericJackson2JsonRedisSerializer jsonValueSerializer() {
        GenericJackson2JsonRedisSerializer serializer = new GenericJackson2JsonRedisSerializer();
        ObjectMapper mapper = (ObjectMapper) ReflectUtil.getFieldValue(serializer, "mapper");
        // 对于空的对象转json的时候不抛出错误
        mapper.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
        // 设置输出时包含属性的风格 属性值为null的不参与序列化
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        // 设置输入时忽略JSON字符串中存在而Java对象实际没有的属性
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        // 允许属性名称没有引号
        mapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
        // 允许单引号
        mapper.configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true);
        //忽略没定义的属性
        mapper.configure(JsonParser.Feature.IGNORE_UNDEFINED,true);
        // 允许所有访问符 序列号
        mapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
        // 禁止get set 方法
        mapper.setVisibility(PropertyAccessor.SETTER, JsonAutoDetect.Visibility.NONE);
        mapper.setVisibility(PropertyAccessor.SETTER, JsonAutoDetect.Visibility.NONE);

        return serializer;
    }
}