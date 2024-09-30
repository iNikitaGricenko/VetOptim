package com.wolfhack.vetoptim.appointment.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    @Value("${rabbitmq.exchange.task}")
    private String taskExchange;

    @Value("${rabbitmq.queue.task.appointment}")
    private String appointmentTaskQueue;

    @Value("${rabbitmq.routingKey.task.appointment}")
    private String appointmentTaskRoutingKey;

    @Value("${rabbitmq.exchange.notification}")
    private String notificationExchange;

    @Value("${rabbitmq.queue.notification.appointment}")
    private String appointmentNotificationQueue;

    @Value("${rabbitmq.routingKey.notification.appointment}")
    private String appointmentNotificationRoutingKey;

    @Bean
    public TopicExchange taskExchange() {
        return new TopicExchange(taskExchange);
    }

    @Bean
    public Queue appointmentTaskQueue() {
        return new Queue(appointmentTaskQueue);
    }

    @Bean
    public Binding appointmentTaskBinding(Queue appointmentTaskQueue, TopicExchange taskExchange) {
        return BindingBuilder.bind(appointmentTaskQueue).to(taskExchange).with(appointmentTaskRoutingKey);
    }

    @Bean
    public TopicExchange notificationExchange() {
        return new TopicExchange(notificationExchange);
    }

    @Bean
    public Queue appointmentNotificationQueue() {
        return new Queue(appointmentNotificationQueue);
    }

    @Bean
    public Binding appointmentNotificationBinding(Queue appointmentNotificationQueue, TopicExchange notificationExchange) {
        return BindingBuilder.bind(appointmentNotificationQueue).to(notificationExchange).with(appointmentNotificationRoutingKey);
    }
}