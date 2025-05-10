package com.wolfhack.vetoptim.taskresource.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

@TestConfiguration
public class DockerComposeTestConfiguration {

    /**
     * This configuration enables Spring Boot to use the docker-compose.ci.yml file for integration tests.
     * It automatically connects to the services defined in the docker-compose file.
     */
    public void dockerComposeServices() {
        // This method is intentionally empty.
        // Spring Boot Docker Compose will automatically configure the connection properties
        // based on the docker-compose.ci.yml file.
    }

    /**
     * This method is used to set dynamic properties for the test context.
     * It sets the spring.docker.compose.file property to point to the docker-compose.ci.yml file.
     */
    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.docker.compose.file", () -> "/home/wolfhack/IdeaProjects/VetOptim/docker-compose.ci.yml");
        registry.add("spring.docker.compose.skip.in-tests", () -> "false");
        registry.add("spring.docker.compose.enabled", () -> "true");
    }
}
