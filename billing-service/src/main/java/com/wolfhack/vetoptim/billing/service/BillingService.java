package com.wolfhack.vetoptim.billing.service;

import com.wolfhack.vetoptim.billing.model.*;
import com.wolfhack.vetoptim.common.InvoiceStatus;
import com.wolfhack.vetoptim.common.dto.billing.ResourceBillingRequest;
import com.wolfhack.vetoptim.common.dto.billing.TaskBillingRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public interface BillingService {
    Invoice createInvoice(Long ownerId);
    Payment processPayment(String invoiceNumber, BigDecimal amountPaid);
    void billTask(TaskBillingRequest taskBillingRequest);
    void billResource(ResourceBillingRequest resourceBillingRequest);


    List<Invoice> getInvoiceHistory(Long ownerId, InvoiceStatus status, LocalDate startDate, LocalDate endDate, Pageable pageable);

    List<Payment> getPaymentHistory(Long ownerId, Pageable pageable);
}