package com.zo.platform.starter.mq.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zo.platform.starter.console.version.Version;
import com.zo.platform.starter.mq.DeadLetterNotifier;
import com.zo.platform.starter.mq.MqConsumerBootstrap;
import com.zo.platform.starter.mq.MqDeadLetterHandler;
import com.zo.platform.starter.mq.MqSender;
import com.zo.platform.starter.mq.config.MqProperties;
import com.zo.platform.starter.mq.constant.MqConstant;
import com.zo.platform.starter.redis.RedisUtil;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author zhoump
 * @date 2026/11/24
 * @purpose
 */
@Configuration
@EnableConfigurationProperties(MqProperties.class)
@ConditionalOnClass(DefaultMQProducer.class)
@ConditionalOnProperty(prefix = "zo.rocketmq", value = "name-server", matchIfMissing = false)
public class MqAutoConfiguration {

    @Bean
    public Version mqVersion(){
        return new Version(MqConstant.VERSION_NAME,MqConstant.VERSION_CODE);
    }

    @Bean(destroyMethod = "shutdown")
    @ConditionalOnMissingBean
    public DefaultMQProducer defaultMQProducer(MqProperties properties) throws Exception {
        DefaultMQProducer producer = new DefaultMQProducer(properties.getProducerGroup());
        producer.setNamesrvAddr(properties.getNameServer());
        producer.setSendMsgTimeout(properties.getSendTimeout());
        producer.start();
        return producer;
    }

    @Bean
    @ConditionalOnMissingBean
    public MqSender mqSender(DefaultMQProducer producer, ObjectMapper objectMapper) {
        return new MqSender(producer, objectMapper);
    }

    @Bean
    @ConditionalOnMissingBean
    public MqConsumerBootstrap mqConsumerBootstrap(MqProperties properties,
                                                         ObjectMapper objectMapper,
                                                   RedisUtil redisUtil) {
        return new MqConsumerBootstrap(properties, objectMapper, redisUtil,null);
    }

    @Bean
    @ConditionalOnMissingBean(MqDeadLetterHandler.class)
    public DeadLetterNotifier deadLetterNotifier(){
        return new DeadLetterNotifier();
    }
}
