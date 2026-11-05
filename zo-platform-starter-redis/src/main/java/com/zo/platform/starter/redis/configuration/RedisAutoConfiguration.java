package com.zo.platform.starter.redis.configuration;

import com.zo.platform.starter.redis.Receiver;
import com.zo.platform.starter.redis.RedisUtil;
import com.zo.platform.starter.redis.config.PlatformCacheConfig;
import com.zo.platform.starter.redis.config.RedisCacheProperties;
import com.zo.platform.starter.redis.lock.RedisLock;
import com.zo.platform.starter.redis.lock.RedisLockAspect;
import com.zo.platform.starter.redis.lock.RedisLockService;
import com.zo.platform.starter.redis.serializer.RedisSerializerFactory;
import org.springframework.boot.autoconfigure.AutoConfigureOrder;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.PatternTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.Topic;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

/**
 * @author zhoump
 * @date 2026/11/4
 * @purpose
 */
@Configuration
@AutoConfigureOrder(Integer.MIN_VALUE)
@ConditionalOnClass(RedisTemplate.class)
@EnableConfigurationProperties(RedisCacheProperties.class)
public class RedisAutoConfiguration {


    @Bean
    @ConditionalOnMissingBean(name = "redisTemplate")
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory factory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(factory);

        // Key、HashKey 使用字符串序列化
        StringRedisSerializer stringSerializer = new StringRedisSerializer();
        template.setKeySerializer(stringSerializer);
        template.setHashKeySerializer(stringSerializer);

        // Value、HashValue 使用 Jackson JSON 序列化（复用缓存同款序列化器）
        GenericJackson2JsonRedisSerializer jsonSerializer = RedisSerializerFactory.jsonValueSerializer();
        template.setValueSerializer(jsonSerializer);
        template.setHashValueSerializer(jsonSerializer);

        template.afterPropertiesSet();
        return template;
    }

    @Bean
    @ConditionalOnMissingBean
    public RedisUtil redisUtil(RedisTemplate<String, Object> redisTemplate) {
        return new RedisUtil(redisTemplate);
    }

    @Bean
    @ConditionalOnMissingBean({CachingConfigurerSupport.class})
    public PlatformCacheConfig platformCacheConfig(){
        return new PlatformCacheConfig();
    }

    @Bean("platformCacheManager")
    @ConditionalOnMissingBean
    public CacheManager cacheManager(RedisConnectionFactory factory,RedisCacheProperties cacheProperties){
        RedisCacheConfiguration redisCacheConfiguration = buildDefaultCacheConfig(cacheProperties);

        // 构建缓存管理器
        return RedisCacheManager.builder(factory)
                .cacheDefaults(redisCacheConfiguration)
                .build();
    }

    /**
     * 构建默认缓存配置
     */
    private RedisCacheConfiguration buildDefaultCacheConfig(RedisCacheProperties properties) {
        RedisCacheConfiguration config = RedisCacheConfiguration.defaultCacheConfig()
                // 过期时间（支持随机偏移防雪崩）
                .entryTtl(computeTtl(properties.getDefaultTtl(), properties))
                // 自定义Key前缀，解决默认双冒号问题
                .computePrefixWith(cacheName -> properties.getKeyPrefix() + cacheName + ":")
                // Key序列化：字符串
                .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(
                        RedisSerializerFactory.stringKeySerializer()))
                // Value序列化：JSON
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(
                        RedisSerializerFactory.jsonValueSerializer()));

        // 是否禁止缓存空值
        if (properties.isDisableCachingNullValues()) {
            config = config.disableCachingNullValues();
        }
        return config;
    }

    /**
     * 计算实际TTL，支持随机偏移
     */
    private Duration computeTtl(Duration baseTtl, RedisCacheProperties properties) {
        if (!properties.isRandomTtlEnabled()) {
            return baseTtl;
        }
        long baseSeconds = baseTtl.getSeconds();
        long offset = (long) (baseSeconds * properties.getRandomTtlFactor());
        long randomOffset = ThreadLocalRandom.current().nextLong(-offset, offset);
        return Duration.ofSeconds(Math.max(baseSeconds + randomOffset, 1));
    }

    /**
     * 注入消息容器
     * @param factory
     * @return
     */
    @Bean("platformRedisMessageListenerContainer")
    @ConditionalOnMissingBean
    public RedisMessageListenerContainer redisMessageListenerContainer(RedisConnectionFactory factory, ApplicationContext applicationContext){
        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(factory);
        Map<String,Object> receiverMap = applicationContext.getBeansWithAnnotation(Receiver.class);
        buildReceiver(receiverMap,container);
        return container;
    }

    private void buildReceiver(Map<String, Object> receiverMap, RedisMessageListenerContainer container) {
        if (receiverMap == null || receiverMap.isEmpty()) {
            return;
        }

        for (Object receiverBean : receiverMap.values()) {
            // 1. 获取注解，读取配置
            Receiver annotation = receiverBean.getClass().getAnnotation(Receiver.class);
            if (annotation == null) {
                continue;
            }
            String channel = annotation.channel();
            String methodName = annotation.method();
            boolean isPattern = annotation.pattern();

            // 2. 构建适配器
            MessageListenerAdapter adapter = new MessageListenerAdapter();
            adapter.setSerializer(RedisSerializerFactory.jsonValueSerializer());
            adapter.setDelegate(receiverBean);
            adapter.setDefaultListenerMethod(methodName);
            adapter.afterPropertiesSet();

            // 3. 根据类型选择主题
            Topic topic = isPattern ? new PatternTopic(channel) : new ChannelTopic(channel);

            // 4. 注册到容器
            container.addMessageListener(adapter, topic);
        }
    }

    @Bean
    @ConditionalOnMissingBean
    public RedisLockService redisLockService(RedisTemplate<String, Object> redisTemplate) {
        return new RedisLockService(redisTemplate);
    }

    @Bean
    @ConditionalOnMissingBean
    public RedisLockAspect redisLockAspect(RedisLockService redisLockService) {
        return new RedisLockAspect(redisLockService);
    }

}
