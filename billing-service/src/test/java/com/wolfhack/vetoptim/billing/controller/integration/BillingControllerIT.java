package com.wolfhack.vetoptim.billing.controller.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wolfhack.vetoptim.billing.client.OwnerClient;
import com.wolfhack.vetoptim.billing.client.TaskClient;
import com.wolfhack.vetoptim.billing.model.*;
import com.wolfhack.vetoptim.billing.repository.*;
import com.wolfhack.vetoptim.common.InvoiceStatus;
import com.wolfhack.vetoptim.common.TaskStatus;
import com.wolfhack.vetoptim.common.TaskType;
import com.wolfhack.vetoptim.common.dto.OwnerDTO;
import com.wolfhack.vetoptim.common.dto.ResourceUsageDTO;
import com.wolfhack.vetoptim.common.dto.TaskDTO;
import com.wolfhack.vetoptim.common.dto.billing.ResourceBillingRequest;
import com.wolfhack.vetoptim.common.dto.billing.TaskBillingRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
@Transactional
class BillingControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private InvoiceRepository invoiceRepository;

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private ChargeableResourceRepository chargeableResourceRepository;

    @Autowired
    private ChargeableTaskRepository chargeableTaskRepository;

    @Autowired
    private TaskCostRepository taskCostRepository;

    @Autowired
    private ResourceCostRepository resourceCostRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private OwnerClient ownerClient;

    @MockBean
    private TaskClient taskClient;

    @BeforeEach
    void setup() {
        invoiceRepository.deleteAll();
        paymentRepository.deleteAll();
        chargeableResourceRepository.deleteAll();
        chargeableTaskRepository.deleteAll();
        taskCostRepository.deleteAll();
        resourceCostRepository.deleteAll();

        when(ownerClient.getOwnerById(1L)).thenReturn(new OwnerDTO(1L, "John Doe", "john@example.com", true, true, List.of(), List.of()));
        when(taskClient.getTaskById(1L)).thenReturn(new TaskDTO(1L, TaskType.SURGERY, "Task description", LocalDateTime.now(), TaskStatus.COMPLETED));
    }

    @Test
    void testCreateInvoice_Success() throws Exception {
        mockMvc.perform(post("/api/billing/invoice/1")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.invoiceNumber").isNotEmpty())
            .andExpect(jsonPath("$.ownerId").value(1));

        List<Invoice> invoices = invoiceRepository.findAll();
        assertThat(invoices).hasSize(1);
        assertThat(invoices.getFirst().getOwnerId()).isEqualTo(1L);
    }

    @Test
    void testProcessPayment_Success() throws Exception {
        Invoice invoice = new Invoice();
        invoice.setInvoiceNumber("INV123");
        invoice.setTotalAmount(new BigDecimal("100.00"));
        invoiceRepository.save(invoice);

        mockMvc.perform(post("/api/billing/invoice/INV123/payment")
                .contentType(MediaType.APPLICATION_JSON)
                .content("50.00"))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.amountPaid").value(50.00));

        List<Payment> payments = paymentRepository.findAll();
        assertThat(payments).hasSize(1);
        assertThat(payments.getFirst().getAmountPaid()).isEqualTo(new BigDecimal("50.00"));
    }

    @Test
    void testBillTask_Success() throws Exception {
        taskCostRepository.save(new TaskCost(null, TaskType.SURGERY, BigDecimal.valueOf(500)));

        resourceCostRepository.save(new ResourceCost(null, 1L, "Surgery Kit", BigDecimal.valueOf(100)));

        TaskBillingRequest taskBillingRequest = new TaskBillingRequest();
        taskBillingRequest.setTaskId(1L);
        taskBillingRequest.setPetId(1L);
        taskBillingRequest.setTaskDescription("Surgery for pet");
        taskBillingRequest.setTaskType(TaskType.SURGERY);
        taskBillingRequest.setResourcesUsed(List.of(new ResourceUsageDTO(1L, "Surgery Kit", 1)));

        mockMvc.perform(post("/api/billing/task/bill")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(taskBillingRequest)))
            .andExpect(status().isCreated());

        List<Invoice> invoices = invoiceRepository.findAll();
        assertThat(invoices).hasSize(1);
        assertThat(invoices.getFirst().getTotalAmount()).isEqualTo(BigDecimal.valueOf(600));
    }

    @Test
    void testBillResource_Success() throws Exception {
        resourceCostRepository.save(new ResourceCost(null, 1L, "Vaccine", BigDecimal.valueOf(50)));

        ResourceBillingRequest resourceBillingRequest = new ResourceBillingRequest(1L, 1L, "Vaccine", 5);

        mockMvc.perform(post("/api/billing/resource/bill")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(resourceBillingRequest)))
            .andExpect(status().isCreated());

        List<Invoice> invoices = invoiceRepository.findAll();
        assertThat(invoices).hasSize(1);
        assertThat(invoices.getFirst().getTotalAmount()).isEqualTo(BigDecimal.valueOf(250));
    }

    @Test
    void testGetInvoiceHistory_Success() throws Exception {
        Invoice invoice = new Invoice();
        invoice.setInvoiceNumber("INV123");
        invoice.setOwnerId(1L);
        invoice.setIssueDate(LocalDate.now());
        invoice.setDueDate(LocalDate.now().plusDays(30));
        invoice.setTotalAmount(new BigDecimal("100.00"));
        invoice.setStatus(InvoiceStatus.PENDING);
        invoiceRepository.save(invoice);

        mockMvc.perform(get("/api/billing/invoice/history/1")
                .param("status", String.valueOf(InvoiceStatus.PENDING))
                .param("startDate", String.valueOf(LocalDate.now()))
                .param("endDate", String.valueOf(LocalDate.now().plusDays(30)))
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].ownerId").value(1L));
    }

    @Test
    void testGetPaymentHistory_Success() throws Exception {
        Invoice invoice = new Invoice();
        invoice.setOwnerId(1L);
        invoiceRepository.save(invoice);

        Payment payment = new Payment();
        payment.setInvoice(invoice);
        payment.setAmountPaid(new BigDecimal("100.00"));
        paymentRepository.save(payment);

        mockMvc.perform(get("/api/billing/payment/history/1")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].amountPaid").value(100.00));
    }
}
