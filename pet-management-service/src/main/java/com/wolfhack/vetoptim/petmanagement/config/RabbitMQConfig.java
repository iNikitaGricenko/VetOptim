package com.wolfhack.vetoptim.petmanagement.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;

@EnableAsync
@Configuration
public class RabbitMQConfig {

    @Value("${spring.rabbitmq.host}")
    private String rabbitMQHost;

    @Value("${spring.rabbitmq.port}")
    private int rabbitMQPort;

    @Value("${spring.rabbitmq.username}")
    private String rabbitMQUsername;

    @Value("${spring.rabbitmq.password}")
    private String rabbitMQPassword;

    @Value("${rabbitmq.exchange.pet}")
    private String petExchange;

    @Value("${rabbitmq.exchange.appointment}")
    private String appointmentExchange;

    @Value("${rabbitmq.exchange.task}")
    private String taskExchange;

    @Value("${rabbitmq.exchange.vaccination}")
    private String vaccinationExchange;

    @Value("${rabbitmq.exchange.owner}")
    private String ownerExchange;

    @Value("${rabbitmq.exchange.notification}")
    private String notificationExchange;

    @Value("${rabbitmq.routingKey.notification.appointment}")
    private String appointmentNotificationRoutingKey;

    @Value("${rabbitmq.routingKey.notification.resource.depletion}")
    private String resourceDepletionRoutingKey;

    @Bean
    public ConnectionFactory connectionFactory() {
        CachingConnectionFactory connectionFactory = new CachingConnectionFactory(rabbitMQHost, rabbitMQPort);
        connectionFactory.setUsername(rabbitMQUsername);
        connectionFactory.setPassword(rabbitMQPassword);
        return connectionFactory;
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        return new RabbitTemplate(connectionFactory);
    }

    @Bean
    public Binding appointmentCreatedBinding(@Value("${rabbitmq.queue.appointment.created}") String appointmentCreatedQueue) {
        return BindingBuilder.bind(
            new Queue(appointmentCreatedQueue)
        ).to(
            new TopicExchange(appointmentExchange)
        ).with("appointment.created");
    }

    @Bean
    public Binding appointmentUpdatedBinding(@Value("${rabbitmq.queue.appointment.updated}") String appointmentUpdatedQueue) {
        return BindingBuilder.bind(
            new Queue(appointmentUpdatedQueue)
        ).to(
            new TopicExchange(appointmentExchange)
        ).with("appointment.updated");
    }

    @Bean
    public Binding appointmentDeletedBinding(@Value("${rabbitmq.queue.appointment.deleted}") String appointmentDeletedQueue) {
        return BindingBuilder.bind(
            new Queue(appointmentDeletedQueue)
        ).to(
            new TopicExchange(appointmentExchange)
        ).with("appointment.deleted");
    }

    @Bean
    public Binding petCreatedBinding(@Value("${rabbitmq.queue.pet.created}") String petCreatedQueue) {
        return BindingBuilder.bind(
            new Queue(petCreatedQueue)
        ).to(
            new TopicExchange(petExchange)
        ).with("pet.created");
    }

    @Bean
    public Binding petUpdatedBinding(@Value("${rabbitmq.queue.pet.updated}") String petUpdatedQueue) {
        return BindingBuilder.bind(
            new Queue(petUpdatedQueue)
        ).to(
            new TopicExchange(petExchange)
        ).with("pet.updated");
    }

    @Bean
    public Binding petDeletedBinding(@Value("${rabbitmq.queue.pet.deleted}") String petDeletedQueue) {
        return BindingBuilder.bind(
            new Queue(petDeletedQueue)
        ).to(
            new TopicExchange(petExchange)
        ).with("pet.deleted");
    }

    @Bean
    public Binding medicalTaskBinding(@Value("${rabbitmq.queue.task.medical}") String medicalTaskQueue) {
        return BindingBuilder.bind(
            new Queue(medicalTaskQueue)
        ).to(
            new TopicExchange(taskExchange)
        ).with("task.medical");
    }

    @Bean
    public Binding taskCompletedBinding(@Value("${rabbitmq.queue.task.completed}") String taskCompletedQueue) {
        return BindingBuilder.bind(
            new Queue(taskCompletedQueue)
        ).to(
            new TopicExchange(taskExchange)
        ).with("task.completed");
    }

    @Bean
    public Binding vaccinationReminderBinding(@Value("${rabbitmq.queue.vaccination.reminder}") String vaccinationReminderQueue) {
        return BindingBuilder.bind(
            new Queue(vaccinationReminderQueue)
        ).to(
            new TopicExchange(vaccinationExchange)
        ).with("vaccination.reminder");
    }

    @Bean
    public Binding ownerCreatedBinding(@Value("${rabbitmq.queue.owner.created}") String ownerCreatedQueue) {
        return BindingBuilder.bind(
            new Queue(ownerCreatedQueue)
        ).to(
            new TopicExchange(ownerExchange)
        ).with("owner.created");
    }

    @Bean
    public Binding resourceDepletionBinding(@Value("${rabbitmq.queue.resource.depleted}") String resourceDepletionQueue) {
        return BindingBuilder.bind(
            new Queue(resourceDepletionQueue)
        ).to(
            new TopicExchange(notificationExchange)
        ).with(resourceDepletionRoutingKey);
    }
}