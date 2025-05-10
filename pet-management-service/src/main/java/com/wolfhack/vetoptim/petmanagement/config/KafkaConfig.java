package com.wolfhack.vetoptim.petmanagement.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.*;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerializer;
import org.springframework.scheduling.annotation.EnableAsync;

import java.util.HashMap;
import java.util.Map;

@EnableAsync
@Configuration
public class KafkaConfig {

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    @Value("${kafka.topic.pet.created}")
    private String petCreatedTopic;

    @Value("${kafka.topic.pet.updated}")
    private String petUpdatedTopic;

    @Value("${kafka.topic.pet.deleted}")
    private String petDeletedTopic;

    @Value("${kafka.topic.appointment.created}")
    private String appointmentCreatedTopic;

    @Value("${kafka.topic.appointment.updated}")
    private String appointmentUpdatedTopic;

    @Value("${kafka.topic.appointment.deleted}")
    private String appointmentDeletedTopic;

    @Value("${kafka.topic.task.medical}")
    private String medicalTaskTopic;

    @Value("${kafka.topic.task.completed}")
    private String taskCompletedTopic;

    @Value("${kafka.topic.vaccination.reminder}")
    private String vaccinationReminderTopic;

    @Value("${kafka.topic.owner.created}")
    private String ownerCreatedTopic;

    @Value("${kafka.topic.resource.depleted}")
    private String resourceDepletedTopic;

    @Value("${kafka.topic.notification.appointment}")
    private String appointmentNotificationTopic;

    // Producer configuration
    @Bean
    public ProducerFactory<String, String> producerFactory() {
        Map<String, Object> configProps = new HashMap<>();
        configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        return new DefaultKafkaProducerFactory<>(configProps);
    }

    @Bean
    public KafkaTemplate<String, String> kafkaTemplate() {
        return new KafkaTemplate<>(producerFactory());
    }

    // JSON Producer configuration
    @Bean
    public ProducerFactory<String, Object> jsonProducerFactory() {
        Map<String, Object> configProps = new HashMap<>();
        configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        return new DefaultKafkaProducerFactory<>(configProps);
    }

    @Bean
    public KafkaTemplate<String, Object> jsonKafkaTemplate() {
        return new KafkaTemplate<>(jsonProducerFactory());
    }

    // Consumer configuration
    @Bean
    public ConsumerFactory<String, Object> consumerFactory() {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
        props.put(JsonDeserializer.TRUSTED_PACKAGES, "*");
        return new DefaultKafkaConsumerFactory<>(props);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, Object> kafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, Object> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory());
        return factory;
    }

    // Create Kafka topics programmatically
    @Bean
    public NewTopic petCreatedTopic() {
        return TopicBuilder.name(petCreatedTopic)
                .partitions(3)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic petUpdatedTopic() {
        return TopicBuilder.name(petUpdatedTopic)
                .partitions(3)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic petDeletedTopic() {
        return TopicBuilder.name(petDeletedTopic)
                .partitions(3)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic appointmentCreatedTopic() {
        return TopicBuilder.name(appointmentCreatedTopic)
                .partitions(3)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic appointmentUpdatedTopic() {
        return TopicBuilder.name(appointmentUpdatedTopic)
                .partitions(3)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic appointmentDeletedTopic() {
        return TopicBuilder.name(appointmentDeletedTopic)
                .partitions(3)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic medicalTaskTopic() {
        return TopicBuilder.name(medicalTaskTopic)
                .partitions(3)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic taskCompletedTopic() {
        return TopicBuilder.name(taskCompletedTopic)
                .partitions(3)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic vaccinationReminderTopic() {
        return TopicBuilder.name(vaccinationReminderTopic)
                .partitions(3)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic ownerCreatedTopic() {
        return TopicBuilder.name(ownerCreatedTopic)
                .partitions(3)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic resourceDepletedTopic() {
        return TopicBuilder.name(resourceDepletedTopic)
                .partitions(3)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic appointmentNotificationTopic() {
        return TopicBuilder.name(appointmentNotificationTopic)
                .partitions(3)
                .replicas(1)
                .build();
    }
}