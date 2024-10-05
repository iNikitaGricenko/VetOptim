package com.wolfhack.vetoptim.billing.client;

import com.wolfhack.vetoptim.common.dto.TaskDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "task-resource-service")
public interface TaskClient {

    @GetMapping("/api/tasks/{id}")
    TaskDTO getTaskById(@PathVariable("id") Long id);
}
