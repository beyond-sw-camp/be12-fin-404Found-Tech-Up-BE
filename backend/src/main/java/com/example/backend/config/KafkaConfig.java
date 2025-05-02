package com.example.backend.config;

import com.example.backend.notification.model.dto.RealTimeNotificationDto;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.*;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.util.HashMap;
import java.util.Map;

@EnableKafka
@Configuration
@ConditionalOnProperty(name = "kafka.enabled", havingValue = "true", matchIfMissing = true)
public class KafkaConfig {

//    private static final String BOOTSTRAP_SERVERS = "localhost:9092";
    private static final String BOOTSTRAP_SERVERS =
            "kafka-0.kafka-headless.default.svc.cluster.local:9092,kafka-1.kafka-headless.default.svc.cluster.local:9092";


    // 1. Topic 생성
    @Bean
    public NewTopic restockNotificationTopic() {
        return new NewTopic("restock-notifications", 1, (short) 2);
    }

    @Bean
    public NewTopic lowStockNotificationTopic() {
        return new NewTopic("low-stock-notifications", 1, (short) 2);
    }

    @Bean
    public NewTopic discountNotificationTopic() {
        return new NewTopic("discount-notifications", 1, (short) 2);
    }

    @Bean
    public NewTopic orderCompleteNotificationTopic() {
        return new NewTopic("order-complete-notifications", 1, (short) 2);
    }

    // 협업필터링 메트릭스 업데이트 위한 토픽
    @Bean
    public NewTopic reviewCreatedNotificationTopic() {
        return new NewTopic("review-created-notifications", 1, (short) 1);
    }

    // 2. Producer 설정 (RealTimeNotificationDto 전용)
    @Bean
    public ProducerFactory<String, RealTimeNotificationDto> realTimeProducerFactory() {
        Map<String, Object> configProps = new HashMap<>();
        configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVERS);
        configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);

        return new DefaultKafkaProducerFactory<>(configProps);
    }

    @Bean(name = "realTimeKafkaTemplate")
    public KafkaTemplate<String, RealTimeNotificationDto> realTimeKafkaTemplate() {
        return new KafkaTemplate<>(realTimeProducerFactory());
    }

    // 3. Consumer 설정 - RealTimeNotificationDto 전용
    @Bean
    public ConsumerFactory<String, RealTimeNotificationDto> realTimeNotificationConsumerFactory() {
        JsonDeserializer<RealTimeNotificationDto> deserializer = new JsonDeserializer<>(RealTimeNotificationDto.class);
        deserializer.addTrustedPackages("*");
        deserializer.setUseTypeMapperForKey(true);

        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVERS);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "notification-group");
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);

        return new DefaultKafkaConsumerFactory<>(props, new StringDeserializer(), deserializer);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, RealTimeNotificationDto> realTimeKafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, RealTimeNotificationDto> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(realTimeNotificationConsumerFactory());
        return factory;
    }

    // 새로운 String용 Producer 설정
    @Bean
    public ProducerFactory<String, String> stringProducerFactory() {
        Map<String, Object> configProps = new HashMap<>();
        configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVERS);
        configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        return new DefaultKafkaProducerFactory<>(configProps);
    }

    @Bean(name = "stringKafkaTemplate")
    public KafkaTemplate<String, String> stringKafkaTemplate() {
        return new KafkaTemplate<>(stringProducerFactory());
    }

    // 새로운 String용 Consumer 설정
    @Bean
    public ConsumerFactory<String, String> stringConsumerFactory() {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVERS);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "review-notification-group");
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);

        return new DefaultKafkaConsumerFactory<>(props, new StringDeserializer(), new StringDeserializer());
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, String> stringKafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, String> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(stringConsumerFactory());
        return factory;
    }
}
