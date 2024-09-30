package com.wolfhack.vetoptim.taskresource;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class TaskResourceApplication {

	public static void main(String[] args) {
		SpringApplication.run(TaskResourceApplication.class, args);
	}

}
