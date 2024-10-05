package com.wolfhack.vetoptim.billing.service.integration;

import com.wolfhack.vetoptim.billing.client.OwnerClient;
import com.wolfhack.vetoptim.billing.client.TaskClient;
import com.wolfhack.vetoptim.billing.event.BillingEventPublisher;
import com.wolfhack.vetoptim.billing.model.*;
import com.wolfhack.vetoptim.billing.repository.*;
import com.wolfhack.vetoptim.billing.service.BillingServiceImpl;
import com.wolfhack.vetoptim.common.InvoiceStatus;
import com.wolfhack.vetoptim.common.PaymentStatus;
import com.wolfhack.vetoptim.common.TaskType;
import com.wolfhack.vetoptim.common.dto.billing.ResourceBillingRequest;
import com.wolfhack.vetoptim.common.dto.billing.TaskBillingRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
@Transactional
class BillingServiceImplIT {

    @Autowired
    private BillingServiceImpl billingService;

    @MockBean
    private OwnerClient ownerClient;

    @MockBean
    private TaskClient taskClient;

    @MockBean
    private BillingEventPublisher billingEventPublisher;

    @Autowired
    private InvoiceRepository invoiceRepository;

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private ChargeableTaskRepository chargeableTaskRepository;

    @Autowired
    private ChargeableResourceRepository chargeableResourceRepository;

    @Autowired
    private ResourceCostRepository resourceCostRepository;

    @Autowired
    private TaskCostRepository taskCostRepository;

    @BeforeEach
    void setup() {
        invoiceRepository.deleteAll();
        paymentRepository.deleteAll();
        chargeableTaskRepository.deleteAll();
        chargeableResourceRepository.deleteAll();
        taskCostRepository.deleteAll();
        resourceCostRepository.deleteAll();
    }

    @Test
    void testBillResource_Success() {
        resourceCostRepository.save(new ResourceCost(null, 1L, "Vaccine", new BigDecimal("50.00")));

        ResourceBillingRequest request = new ResourceBillingRequest(1L, 1L, "Vaccine", 5);

        billingService.billResource(request);

        List<Invoice> invoices = invoiceRepository.findAll();
        assertThat(invoices).hasSize(1);
        Invoice invoice = invoices.getFirst();
        assertThat(invoice.getTotalAmount()).isEqualTo(new BigDecimal("250.00"));

        List<ChargeableResource> resources = chargeableResourceRepository.findAll();
        assertThat(resources).hasSize(1);
        ChargeableResource resource = resources.getFirst();
        assertThat(resource.getResourceCost()).isEqualTo(new BigDecimal("250.00"));
        assertThat(resource.getInvoice()).isEqualTo(invoice);

        verify(billingEventPublisher, times(1)).publishInvoiceCreatedEvent(any());
    }
    @Test
    void testBillTask_Success() {
        TaskCost taskCost = new TaskCost(null, TaskType.SURGERY, BigDecimal.valueOf(500));
        taskCostRepository.save(taskCost);

        TaskBillingRequest taskBillingRequest = new TaskBillingRequest();
        taskBillingRequest.setTaskId(1L);
        taskBillingRequest.setPetId(1L);
        taskBillingRequest.setTaskDescription("Pet Surgery");
        taskBillingRequest.setTaskType(TaskType.SURGERY);
        taskBillingRequest.setResourcesUsed(Collections.emptyList());

        billingService.billTask(taskBillingRequest);

        List<Invoice> invoices = invoiceRepository.findAll();
        assertThat(invoices).hasSize(1);
        Invoice invoice = invoices.getFirst();
        assertThat(invoice.getTotalAmount()).isEqualTo(new BigDecimal("500"));

        List<ChargeableTask> tasks = chargeableTaskRepository.findAll();
        assertThat(tasks).hasSize(1);
        ChargeableTask savedTask = tasks.getFirst();
        assertThat(savedTask.getTaskCost()).isEqualTo(new BigDecimal("500"));
        assertThat(savedTask.getInvoice()).isEqualTo(invoice);

        List<ChargeableResource> resources = chargeableResourceRepository.findAll();
        assertThat(resources).hasSize(0);

        verify(billingEventPublisher, times(1)).publishInvoiceCreatedEvent(any());
    }

    @Test
    void testProcessPayment_Success() {
        String invoiceNumber = UUID.randomUUID().toString();
        Invoice invoice = new Invoice();
        invoice.setInvoiceNumber(invoiceNumber);
        invoice.setTotalAmount(new BigDecimal("100.00"));
        invoice.setStatus(InvoiceStatus.PENDING);
        invoiceRepository.save(invoice);

        Payment payment = billingService.processPayment(invoiceNumber, new BigDecimal("50.00"));

        assertThat(payment).isNotNull();
        assertThat(payment.getAmountPaid()).isEqualTo(new BigDecimal("50.00"));
        assertThat(payment.getPaymentStatus()).isEqualTo(PaymentStatus.PENDING);
        assertThat(payment.getInvoice()).isEqualTo(invoice);

        assertThat(invoice.getStatus()).isEqualTo(InvoiceStatus.PARTIALLY_PAID);

        assertThat(paymentRepository.findAll()).hasSize(1);
        assertThat(invoiceRepository.findAll()).hasSize(1);

        verify(billingEventPublisher, times(1)).publishPaymentProcessedEvent(any());
    }

    @Test
    void testGetInvoiceHistory_Success() {
        Invoice invoice = new Invoice();
        invoice.setInvoiceNumber("INV123");
        invoice.setOwnerId(1L);
        invoice.setIssueDate(LocalDate.now());
        invoice.setDueDate(LocalDate.now().plusDays(30));
        invoice.setTotalAmount(new BigDecimal("100.00"));
        invoice.setStatus(InvoiceStatus.PENDING);
        invoiceRepository.save(invoice);

        List<Invoice> result = billingService.getInvoiceHistory(1L, InvoiceStatus.PENDING, LocalDate.now().minusDays(1), LocalDate.now().plusDays(1), Pageable.unpaged());

        assertThat(result).hasSize(1);
        assertThat(result.getFirst().getInvoiceNumber()).isEqualTo("INV123");
    }

    @Test
    void testGetPaymentHistory_Success() {
        Invoice invoice = new Invoice();
        invoice.setOwnerId(1L);
        invoiceRepository.save(invoice);

        Payment payment = new Payment();
        payment.setInvoice(invoice);
        payment.setAmountPaid(new BigDecimal("100.00"));
        paymentRepository.save(payment);

        List<Payment> result = billingService.getPaymentHistory(1L, Pageable.unpaged());

        assertThat(result).hasSize(1);
        assertThat(result.getFirst().getAmountPaid()).isEqualTo(new BigDecimal("100.00"));
    }
}
