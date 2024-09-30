package com.wolfhack.vetoptim.taskresource.config;

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

    @Value("${rabbitmq.exchange.notification}")
    private String notificationExchange;

    @Value("${rabbitmq.queue.notification.urgent}")
    private String urgentTaskQueue;

    @Value("${rabbitmq.queue.notification.resource.depletion}")
    private String resourceDepletionQueue;

    @Value("${rabbitmq.routingKey.notification.urgent}")
    private String urgentTaskRoutingKey;

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
    public TopicExchange notificationExchange() {
        return new TopicExchange(notificationExchange);
    }

    @Bean
    public Queue urgentTaskQueue() {
        return new Queue(urgentTaskQueue);
    }

    @Bean
    public Queue resourceDepletionQueue() {
        return new Queue(resourceDepletionQueue);
    }

    @Bean
    public Binding urgentTaskBinding(Queue urgentTaskQueue, TopicExchange notificationExchange) {
        return BindingBuilder.bind(urgentTaskQueue).to(notificationExchange).with(urgentTaskRoutingKey);
    }

    @Bean
    public Binding resourceDepletionBinding(Queue resourceDepletionQueue, TopicExchange notificationExchange) {
        return BindingBuilder.bind(resourceDepletionQueue).to(notificationExchange).with(resourceDepletionRoutingKey);
    }
}