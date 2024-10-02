package com.wolfhack.vetoptim.billing.service;

import com.wolfhack.vetoptim.billing.client.OwnerClient;
import com.wolfhack.vetoptim.billing.client.TaskClient;
import com.wolfhack.vetoptim.billing.event.BillingEventPublisher;
import com.wolfhack.vetoptim.billing.model.ChargeableResource;
import com.wolfhack.vetoptim.billing.model.ChargeableTask;
import com.wolfhack.vetoptim.billing.model.Invoice;
import com.wolfhack.vetoptim.billing.model.Payment;
import com.wolfhack.vetoptim.billing.repository.ChargeableResourceRepository;
import com.wolfhack.vetoptim.billing.repository.ChargeableTaskRepository;
import com.wolfhack.vetoptim.billing.repository.InvoiceRepository;
import com.wolfhack.vetoptim.billing.repository.PaymentRepository;
import com.wolfhack.vetoptim.common.InvoiceStatus;
import com.wolfhack.vetoptim.common.PaymentStatus;
import com.wolfhack.vetoptim.common.dto.billing.ResourceBillingRequest;
import com.wolfhack.vetoptim.common.dto.billing.TaskBillingRequest;
import com.wolfhack.vetoptim.common.event.billing.InvoiceCreatedEvent;
import com.wolfhack.vetoptim.common.event.billing.PaymentProcessedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class BillingServiceImpl implements BillingService {

    private final InvoiceRepository invoiceRepository;
    private final PaymentRepository paymentRepository;
    private final ChargeableTaskRepository chargeableTaskRepository;
    private final ChargeableResourceRepository chargeableResourceRepository;
    private final BillingEventPublisher billingEventPublisher;
    private final OwnerClient ownerClient;
    private final TaskClient taskClient;

    @Override
    @Transactional
    public Invoice createInvoice(Long ownerId) {
        try {
            List<ChargeableTask> chargeableTasks = chargeableTaskRepository.findByInvoiceOwnerId(ownerId);
            List<ChargeableResource> chargeableResources = chargeableResourceRepository.findByInvoiceOwnerId(ownerId);

            BigDecimal totalAmount = calculateTotalAmount(chargeableTasks, chargeableResources);

            Invoice invoice = new Invoice();
            invoice.setInvoiceNumber(UUID.randomUUID().toString());
            invoice.setOwnerId(ownerId);
            invoice.setIssueDate(LocalDate.now());
            invoice.setDueDate(LocalDate.now().plusDays(30));
            invoice.setTotalAmount(totalAmount);
            invoice.setStatus(InvoiceStatus.PENDING);

            invoiceRepository.save(invoice);
            log.info("Invoice {} created for owner {}", invoice.getInvoiceNumber(), ownerId);

            publishInvoiceCreatedEvent(invoice);

            return invoice;

        } catch (Exception ex) {
            log.error("Failed to create invoice for ownerId {}: {}", ownerId, ex.getMessage());
            throw new RuntimeException("Failed to create invoice", ex);
        }
    }

    @Override
    @Transactional
    public Payment processPayment(String invoiceNumber, BigDecimal amountPaid) {
        try {
            Invoice invoice = invoiceRepository.findByInvoiceNumber(invoiceNumber)
                .orElseThrow(() -> new IllegalArgumentException("Invoice not found"));

            Payment payment = new Payment();
            payment.setInvoice(invoice);
            payment.setAmountPaid(amountPaid);
            payment.setPaymentDate(LocalDate.now());

            if (amountPaid.compareTo(invoice.getTotalAmount()) >= 0) {
                invoice.setStatus(InvoiceStatus.PAID);
                payment.setPaymentStatus(PaymentStatus.SUCCESS);
                log.info("Invoice {} fully paid with amount {}", invoiceNumber, amountPaid);
            } else {
                invoice.setStatus(InvoiceStatus.PARTIALLY_PAID);
                payment.setPaymentStatus(PaymentStatus.PENDING);
                log.info("Invoice {} partially paid with amount {}", invoiceNumber, amountPaid);
            }

            paymentRepository.save(payment);
            invoiceRepository.save(invoice);

            publishPaymentProcessedEvent(payment);

            return payment;

        } catch (Exception ex) {
            log.error("Failed to process payment for invoice {}: {}", invoiceNumber, ex.getMessage());
            throw new RuntimeException("Payment processing failed", ex);
        }
    }

    @Override
    @Transactional
    public void billTask(TaskBillingRequest taskBillingRequest) {
        ChargeableTask chargeableTask = new ChargeableTask();
        chargeableTask.setTaskId(taskBillingRequest.getTaskId());
        chargeableTask.setDescription(taskBillingRequest.getTaskDescription());
        chargeableTask.setTaskCost(calculateTaskCost(taskBillingRequest));

        Invoice invoice = invoiceRepository.findByOwnerId(taskBillingRequest.getPetId())
            .orElseGet(() -> createInvoice(taskBillingRequest.getPetId()));

        chargeableTask.setInvoice(invoice);
        chargeableTaskRepository.save(chargeableTask);

        invoice.setTotalAmount(invoice.getTotalAmount().add(chargeableTask.getTaskCost()));
        invoiceRepository.save(invoice);

        log.info("Billed task {} for invoice {}", taskBillingRequest.getTaskId(), invoice.getInvoiceNumber());
    }

    @Override
    @Transactional
    public void billResource(ResourceBillingRequest resourceBillingRequest) {
        ChargeableResource chargeableResource = new ChargeableResource();
        chargeableResource.setResourceId(resourceBillingRequest.getResourceId());
        chargeableResource.setResourceType(resourceBillingRequest.getResourceName());
        chargeableResource.setResourceCost(calculateResourceCost(resourceBillingRequest));

        Invoice invoice = invoiceRepository.findByOwnerId(resourceBillingRequest.getTaskId())
            .orElseGet(() -> createInvoice(resourceBillingRequest.getTaskId()));

        chargeableResource.setInvoice(invoice);
        chargeableResourceRepository.save(chargeableResource);

        invoice.setTotalAmount(invoice.getTotalAmount().add(chargeableResource.getResourceCost()));
        invoiceRepository.save(invoice);

        log.info("Billed resource {} for invoice {}", resourceBillingRequest.getResourceId(), invoice.getInvoiceNumber());
    }

    @Override
    public List<Invoice> getInvoiceHistory(Long ownerId, InvoiceStatus status, LocalDate startDate, LocalDate endDate, Pageable pageable) {
        try {
            log.info("Fetching invoice history for ownerId {}, status {}, between {} and {}", ownerId, status, startDate, endDate);
            return invoiceRepository.findByOwnerIdAndStatusAndIssueDateBetween(ownerId, status, startDate, endDate, pageable);
        } catch (Exception ex) {
            log.error("Failed to fetch invoice history for ownerId {}: {}", ownerId, ex.getMessage());
            throw new RuntimeException("Failed to fetch invoice history", ex);
        }
    }

    @Override
    public List<Payment> getPaymentHistory(Long ownerId, Pageable pageable) {
        try {
            log.info("Fetching payment history for ownerId {}", ownerId);
            return paymentRepository.findByOwnerId(ownerId, pageable);
        } catch (Exception ex) {
            log.error("Failed to fetch payment history for ownerId {}: {}", ownerId, ex.getMessage());
            throw new RuntimeException("Failed to fetch payment history", ex);
        }
    }

    private BigDecimal calculateTotalAmount(List<ChargeableTask> tasks, List<ChargeableResource> resources) {
        BigDecimal taskAmount = tasks.stream().map(ChargeableTask::getTaskCost).reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal resourceAmount = resources.stream().map(ChargeableResource::getResourceCost).reduce(BigDecimal.ZERO, BigDecimal::add);
        return taskAmount.add(resourceAmount);
    }

    private BigDecimal calculateTaskCost(TaskBillingRequest taskBillingRequest) {
        return taskBillingRequest.getResourcesUsed().stream()
            .map(resource -> chargeableTaskRepository.findByTaskId(taskBillingRequest.getTaskId())
                .map(ChargeableTask::getTaskCost)
                .orElse(BigDecimal.ZERO))
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private BigDecimal calculateResourceCost(ResourceBillingRequest resourceBillingRequest) {
        return chargeableResourceRepository.findByResourceId(resourceBillingRequest.getResourceId())
            .map(ChargeableResource::getResourceCost)
            .orElse(BigDecimal.ZERO);
    }

    private void publishInvoiceCreatedEvent(Invoice invoice) {
        InvoiceCreatedEvent event = new InvoiceCreatedEvent(invoice.getInvoiceNumber(), invoice.getOwnerId(), invoice.getIssueDate(), invoice.getDueDate(), invoice.getTotalAmount(), invoice.getStatus());
        billingEventPublisher.publishInvoiceCreatedEvent(event);
        log.info("Published InvoiceCreatedEvent for invoice {}", invoice.getInvoiceNumber());
    }

    private void publishPaymentProcessedEvent(Payment payment) {
        PaymentProcessedEvent event = new PaymentProcessedEvent(payment.getInvoice().getInvoiceNumber(), payment.getAmountPaid(), payment.getPaymentDate(), payment.getPaymentStatus());
        billingEventPublisher.publishPaymentProcessedEvent(event);
        log.info("Published PaymentProcessedEvent for invoice {}", payment.getInvoice().getInvoiceNumber());
    }
}
