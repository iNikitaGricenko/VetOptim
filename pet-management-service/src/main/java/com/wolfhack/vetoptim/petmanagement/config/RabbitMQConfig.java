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

	@Value("${rabbitmq.exchange.vaccination}")
	private String vaccinationExchange;

	@Value("${rabbitmq.exchange.task}")
	private String taskExchange;

	@Value("${rabbitmq.exchange.notification}")
	private String notificationExchange;

	@Value("${rabbitmq.routingKey.pet.created}")
	private String petCreatedRoutingKey;

	@Value("${rabbitmq.routingKey.pet.updated}")
	private String petUpdatedRoutingKey;

	@Value("${rabbitmq.routingKey.pet.deleted}")
	private String petDeletedRoutingKey;

	@Value("${rabbitmq.exchange.appointment}")
	private String appointmentExchange;

	@Value("${rabbitmq.routingKey.appointment.created}")
	private String appointmentCreatedRoutingKey;

	@Value("${rabbitmq.routingKey.appointment.updated}")
	private String appointmentUpdatedRoutingKey;

	@Value("${rabbitmq.routingKey.appointment.deleted}")
	private String appointmentDeletedRoutingKey;

	@Value("${rabbitmq.routingKey.task.medical}")
	private String medicalTaskRoutingKey;

	@Value("${rabbitmq.exchange.appointment-task}")
	private String appointmentTaskExchange;

	@Value("${rabbitmq.routingKey.task.appointment}")
	private String appointmentTaskRoutingKey;

	@Value("${rabbitmq.routingKey.notification.appointment}")
	private String appointmentNotificationRoutingKey;

	@Value("${rabbitmq.routingKey.task.emergency}")
	private String emergencyTaskRoutingKey;

	@Value("${rabbitmq.routingKey.task.followup}")
	private String followUpTaskRoutingKey;

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
	public TopicExchange petExchange() {
		return new TopicExchange(petExchange);
	}

	@Bean
	public TopicExchange appointmentExchange() {
		return new TopicExchange(appointmentExchange);
	}

	@Bean
	public TopicExchange taskExchange() {
		return new TopicExchange(taskExchange);
	}

	@Bean
	public TopicExchange appointmentTaskExchange() {
		return new TopicExchange(appointmentTaskExchange);
	}

	@Bean
	public TopicExchange notificationExchange() {
		return new TopicExchange(notificationExchange);
	}

	@Bean
	public TopicExchange vaccinationExchange() {
		return new TopicExchange(vaccinationExchange);
	}


	@Bean
	public Queue petCreatedQueue(
		@Value("${rabbitmq.queue.pet.created}") String petCreatedQueue
	) {
		return new Queue(petCreatedQueue);
	}

	@Bean
	public Queue petUpdatedQueue(
		@Value("${rabbitmq.queue.pet.updated}") String petUpdatedQueue
	) {
		return new Queue(petUpdatedQueue);
	}

	@Bean
	public Queue petDeletedQueue(
		@Value("${rabbitmq.queue.pet.deleted}") String petDeletedQueue
	) {
		return new Queue(petDeletedQueue);
	}

	@Bean
	public Queue appointmentTaskQueue(
		@Value("${rabbitmq.queue.task.appointment}") String appointmentTaskQueue
	) {
		return new Queue(appointmentTaskQueue);
	}

	@Bean
	public Queue appointmentNotificationQueue(
		@Value("${rabbitmq.queue.notification.appointment}") String appointmentNotificationQueue
	) {
		return new Queue(appointmentNotificationQueue);
	}

	@Bean
	public Queue appointmentCreatedQueue(
		@Value("${rabbitmq.queue.appointment.created}") String appointmentCreatedQueue
	) {
		return new Queue(appointmentCreatedQueue);
	}

	@Bean
	public Queue appointmentUpdatedQueue(
		@Value("${rabbitmq.queue.appointment.updated}") String appointmentUpdatedQueue
	) {
		return new Queue(appointmentUpdatedQueue);
	}

	@Bean
	public Queue appointmentDeletedQueue(
		@Value("${rabbitmq.queue.appointment.deleted}") String appointmentDeletedQueue
	) {
		return new Queue(appointmentDeletedQueue);
	}

	@Bean
	public Queue vaccinationReminderQueue(
		@Value("${rabbitmq.queue.vaccination.reminder}") String vaccinationReminderQueue
	) {
		return new Queue(vaccinationReminderQueue);
	}

	@Bean
	public Queue medicalTaskQueue(
		@Value("${rabbitmq.queue.task.medical}") String medicalTaskQueue
	) {
		return new Queue(medicalTaskQueue);
	}

	@Bean
	public Queue emergencyTaskQueue(
		@Value("${rabbitmq.queue.task.emergency}") String emergencyTaskQueue
	) {
		return new Queue(emergencyTaskQueue);
	}

	@Bean
	public Queue followUpTaskQueue(
		@Value("${rabbitmq.queue.task.followup}") String followUpTaskQueue
	) {
		return new Queue(followUpTaskQueue);
	}

    @Bean
    public Queue resourceDepletedQueue(
		@Value("${rabbitmq.queue.resource.depleted}") String depletedResourceQueue
    ) {
        return new Queue(depletedResourceQueue);
    }

	@Bean
	public Queue taskCompletedQueue(@Value("${rabbitmq.queue.task.completed}") String completedTaskQueue) {
		return new Queue(completedTaskQueue);
	}


    @Bean
    public Binding taskCompletedBinding(Queue taskCompletedQueue, TopicExchange taskExchange) {
        return BindingBuilder.bind(taskCompletedQueue).to(taskExchange).with("task.completed");
    }

    @Bean
    public Binding resourceDepletedBinding(Queue resourceDepletedQueue, TopicExchange taskExchange) {
        return BindingBuilder.bind(resourceDepletedQueue).to(taskExchange).with("resource.depleted");
    }
	@Bean
	public Binding emergencyTaskBinding(Queue emergencyTaskQueue, TopicExchange taskExchange) {
		return BindingBuilder.bind(emergencyTaskQueue).to(taskExchange).with(emergencyTaskRoutingKey);
	}

	@Bean
	public Binding followUpTaskBinding(Queue followUpTaskQueue, TopicExchange taskExchange) {
		return BindingBuilder.bind(followUpTaskQueue).to(taskExchange).with(followUpTaskRoutingKey);
	}

	@Bean
	public Binding appointmentCreatedBinding(Queue appointmentCreatedQueue, TopicExchange appointmentExchange) {
		return BindingBuilder.bind(appointmentCreatedQueue).to(appointmentExchange).with(appointmentCreatedRoutingKey);
	}

	@Bean
	public Binding appointmentUpdatedBinding(Queue appointmentUpdatedQueue, TopicExchange appointmentExchange) {
		return BindingBuilder.bind(appointmentUpdatedQueue).to(appointmentExchange).with(appointmentUpdatedRoutingKey);
	}

	@Bean
	public Binding appointmentDeletedBinding(Queue appointmentDeletedQueue, TopicExchange appointmentExchange) {
		return BindingBuilder.bind(appointmentDeletedQueue).to(appointmentExchange).with(appointmentDeletedRoutingKey);
	}

	@Bean
	public Binding vaccinationReminderBinding(Queue vaccinationReminderQueue, TopicExchange vaccinationExchange) {
		return BindingBuilder.bind(vaccinationReminderQueue).to(vaccinationExchange).with("vaccination.reminder");
	}

	@Bean
	public Binding medicalTaskBinding(Queue medicalTaskQueue, TopicExchange taskExchange) {
		return BindingBuilder.bind(medicalTaskQueue).to(taskExchange).with(medicalTaskRoutingKey);
	}

	@Bean
	public Binding appointmentTaskBinding(Queue appointmentTaskQueue, TopicExchange appointmentTaskExchange) {
		return BindingBuilder.bind(appointmentTaskQueue).to(appointmentTaskExchange).with(appointmentTaskRoutingKey);
	}

	@Bean
	public Binding appointmentNotificationBinding(Queue appointmentNotificationQueue, TopicExchange notificationExchange) {
		return BindingBuilder.bind(appointmentNotificationQueue).to(notificationExchange).with(appointmentNotificationRoutingKey);
	}

	@Bean
	public Binding petCreatedBinding(Queue petCreatedQueue, TopicExchange petExchange) {
		return BindingBuilder.bind(petCreatedQueue).to(petExchange).with(petCreatedRoutingKey);
	}

	@Bean
	public Binding petUpdatedBinding(Queue petUpdatedQueue, TopicExchange petExchange) {
		return BindingBuilder.bind(petUpdatedQueue).to(petExchange).with(petUpdatedRoutingKey);
	}

	@Bean
	public Binding petDeletedBinding(Queue petDeletedQueue, TopicExchange petExchange) {
		return BindingBuilder.bind(petDeletedQueue).to(petExchange).with(petDeletedRoutingKey);
	}

}