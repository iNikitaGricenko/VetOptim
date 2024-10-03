package com.wolfhack.vetoptim.billing.controller;

import com.wolfhack.vetoptim.billing.model.Invoice;
import com.wolfhack.vetoptim.billing.model.Payment;
import com.wolfhack.vetoptim.billing.service.BillingService;
import com.wolfhack.vetoptim.common.InvoiceStatus;
import com.wolfhack.vetoptim.common.dto.billing.ResourceBillingRequest;
import com.wolfhack.vetoptim.common.dto.billing.TaskBillingRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.net.URI;
import java.time.LocalDate;
import java.util.List;

@Validated
@RestController
@RequestMapping("/api/billing")
@RequiredArgsConstructor
public class BillingController {

    private final BillingService billingService;

    @PostMapping("/invoice/{ownerId}")
    public ResponseEntity<Invoice> createInvoice(@PathVariable Long ownerId) {
        Invoice invoice = billingService.createInvoice(ownerId);
        return ResponseEntity.created(
            URI.create("/api/billing/invoice/" + invoice.getInvoiceNumber())
        ).body(invoice);
    }

    @PostMapping("/invoice/{invoiceNumber}/payment")
    public ResponseEntity<Payment> processPayment(@PathVariable String invoiceNumber, @RequestBody @Valid BigDecimal amountPaid) {
        Payment payment = billingService.processPayment(invoiceNumber, amountPaid);
        return ResponseEntity.created(
            URI.create("/api/billing/payment/" + payment.getPaymentId())
        ).body(payment);
    }

    @PostMapping("/task/bill")
    public ResponseEntity<Void> billTask(@RequestBody @Valid TaskBillingRequest taskBillingRequest) {
        billingService.billTask(taskBillingRequest);
        return ResponseEntity.created(URI.create("/api/billing/task/" + taskBillingRequest.getTaskId())).build();
    }

    @PostMapping("/resource/bill")
    public ResponseEntity<Void> billResource(@RequestBody @Valid ResourceBillingRequest resourceBillingRequest) {
        billingService.billResource(resourceBillingRequest);
        return ResponseEntity.created(URI.create("/api/billing/resource/" + resourceBillingRequest.getResourceId())).build();
    }

    @GetMapping("/invoice/history/{ownerId}")
    public ResponseEntity<List<Invoice>> getInvoiceHistory(
        @PathVariable Long ownerId,
        @RequestParam(required = false) InvoiceStatus status,
        @RequestParam(required = false) LocalDate startDate,
        @RequestParam(required = false) LocalDate endDate,
        Pageable pageable) {
        List<Invoice> invoiceHistory = billingService.getInvoiceHistory(ownerId, status, startDate, endDate, pageable);
        return ResponseEntity.ok(invoiceHistory);
    }

    @GetMapping("/payment/history/{ownerId}")
    public ResponseEntity<List<Payment>> getPaymentHistory(@PathVariable Long ownerId, Pageable pageable) {
        List<Payment> paymentHistory = billingService.getPaymentHistory(ownerId, pageable);
        return ResponseEntity.ok(paymentHistory);
    }
}