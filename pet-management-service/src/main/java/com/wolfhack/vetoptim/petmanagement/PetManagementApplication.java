package com.wolfhack.vetoptim.petmanagement;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class PetManagementApplication {

	public static void main(String[] args) {
		SpringApplication.run(PetManagementApplication.class, args);
	}

}
