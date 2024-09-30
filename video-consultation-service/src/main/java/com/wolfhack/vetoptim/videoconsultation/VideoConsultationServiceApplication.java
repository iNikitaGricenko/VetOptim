package com.wolfhack.vetoptim.videoconsultation;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.retry.annotation.EnableRetry;

@EnableRetry
@SpringBootApplication
public class VideoConsultationServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(VideoConsultationServiceApplication.class, args);
	}

}
