package com.wolfhack.vetoptim.billing.controller;

import com.wolfhack.vetoptim.billing.model.Invoice;
import com.wolfhack.vetoptim.billing.model.Payment;
import com.wolfhack.vetoptim.billing.service.BillingService;
import com.wolfhack.vetoptim.common.dto.billing.ResourceBillingRequest;
import com.wolfhack.vetoptim.common.dto.billing.TaskBillingRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.net.URI;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BillingControllerTest {

    @Mock
    private BillingService billingService;

    @InjectMocks
    private BillingController billingController;

    private Invoice invoice;
    private Payment payment;
    private TaskBillingRequest taskBillingRequest;
    private ResourceBillingRequest resourceBillingRequest;

    @BeforeEach
    void setUp() {
        invoice = new Invoice();
        invoice.setInvoiceNumber("INV123");
        invoice.setOwnerId(1L);
        invoice.setIssueDate(LocalDate.now());
        invoice.setTotalAmount(new BigDecimal("100.00"));

        payment = new Payment();
        payment.setPaymentId("PAY123");

        taskBillingRequest = new TaskBillingRequest();
        taskBillingRequest.setTaskId(1L);
        taskBillingRequest.setTaskDescription("Test Task");

        resourceBillingRequest = new ResourceBillingRequest();
        resourceBillingRequest.setResourceId(1L);
        resourceBillingRequest.setResourceName("Vaccine");
    }

    @Test
    void createInvoice_Success() {
        when(billingService.createInvoice(1L)).thenReturn(invoice);

        ResponseEntity<Invoice> response = billingController.createInvoice(1L);

        verify(billingService).createInvoice(1L);
        assertEquals(ResponseEntity.created(URI.create("/api/billing/invoice/INV123")).body(invoice), response);
    }

    @Test
    void processPayment_Success() {
        when(billingService.processPayment("INV123", new BigDecimal("100.00"))).thenReturn(payment);

        ResponseEntity<Payment> response = billingController.processPayment("INV123", new BigDecimal("100.00"));

        verify(billingService).processPayment("INV123", new BigDecimal("100.00"));
        assertEquals(ResponseEntity.created(URI.create("/api/billing/payment/PAY123")).body(payment), response);
    }

    @Test
    void billTask_Success() {
        doNothing().when(billingService).billTask(taskBillingRequest);

        ResponseEntity<Void> response = billingController.billTask(taskBillingRequest);

        verify(billingService).billTask(taskBillingRequest);
        assertEquals(ResponseEntity.created(URI.create("/api/billing/task/1")).build(), response);
    }

    @Test
    void billResource_Success() {
        doNothing().when(billingService).billResource(resourceBillingRequest);

        ResponseEntity<Void> response = billingController.billResource(resourceBillingRequest);

        verify(billingService).billResource(resourceBillingRequest);
        assertEquals(ResponseEntity.created(URI.create("/api/billing/resource/1")).build(), response);
    }
}
