package com.wolfhack.vetoptim.taskresource.client;

import com.wolfhack.vetoptim.common.dto.billing.ResourceBillingRequest;
import com.wolfhack.vetoptim.common.dto.billing.TaskBillingRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "billing-service", url = "${billing.service.url}")
public interface BillingClient {

    @PostMapping("/billing/resource")
    void sendResourceBillingRequest(@RequestBody ResourceBillingRequest request);

    @PostMapping("/billing/task")
    void sendTaskBillingRequest(@RequestBody TaskBillingRequest request);
}