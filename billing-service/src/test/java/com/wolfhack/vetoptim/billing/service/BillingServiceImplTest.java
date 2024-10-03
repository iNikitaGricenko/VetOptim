package com.wolfhack.vetoptim.billing.service;

import com.wolfhack.vetoptim.billing.event.BillingEventPublisher;
import com.wolfhack.vetoptim.billing.model.*;
import com.wolfhack.vetoptim.billing.repository.*;
import com.wolfhack.vetoptim.common.InvoiceStatus;
import com.wolfhack.vetoptim.common.PaymentStatus;
import com.wolfhack.vetoptim.common.TaskType;
import com.wolfhack.vetoptim.common.dto.billing.ResourceBillingRequest;
import com.wolfhack.vetoptim.common.dto.billing.TaskBillingRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BillingServiceImplTest {

	@Mock
	private InvoiceRepository invoiceRepository;

	@Mock
	private PaymentRepository paymentRepository;

	@Mock
	private ChargeableTaskRepository chargeableTaskRepository;

	@Mock
	private TaskCostRepository taskCostRepository;

	@Mock
	private ResourceCostRepository resourceCostRepository;

	@Mock
	private ChargeableResourceRepository chargeableResourceRepository;

	@Mock
	private BillingEventPublisher billingEventPublisher;

	@InjectMocks
	private BillingServiceImpl billingService;

	private Invoice invoice;
	private Payment payment;
	private TaskBillingRequest taskBillingRequest;
	private ResourceBillingRequest resourceBillingRequest;

	@BeforeEach
	void setUp() {
		invoice = new Invoice();
		invoice.setInvoiceNumber(UUID.randomUUID().toString());
		invoice.setOwnerId(1L);
		invoice.setIssueDate(LocalDate.now());
		invoice.setDueDate(LocalDate.now().plusDays(30));
		invoice.setTotalAmount(BigDecimal.valueOf(100));
		invoice.setStatus(InvoiceStatus.PENDING);

		payment = new Payment();
		payment.setInvoice(invoice);
		payment.setAmountPaid(BigDecimal.valueOf(50));
		payment.setPaymentDate(LocalDate.now());

		taskBillingRequest = new TaskBillingRequest();
		taskBillingRequest.setTaskId(1L);
		taskBillingRequest.setTaskDescription("Surgery");
		taskBillingRequest.setPetId(1L);
		taskBillingRequest.setTaskType(TaskType.SURGERY);
		taskBillingRequest.setResourcesUsed(Collections.emptyList());

		resourceBillingRequest = new ResourceBillingRequest();
		resourceBillingRequest.setResourceId(1L);
		resourceBillingRequest.setTaskId(1L);
		resourceBillingRequest.setResourceName("Vaccine");
		resourceBillingRequest.setQuantityUsed(2);
	}

	@Test
	void createInvoice_Success() {
		when(invoiceRepository.save(any(Invoice.class))).thenReturn(invoice);

		Invoice createdInvoice = billingService.createInvoice(1L);

		verify(invoiceRepository).save(any(Invoice.class));
		assertNotNull(createdInvoice);
		assertEquals(1L, createdInvoice.getOwnerId());
		assertEquals(InvoiceStatus.PENDING, createdInvoice.getStatus());
	}

	@Test
	void processPayment_FullyPaid() {
		when(invoiceRepository.findByInvoiceNumber(anyString())).thenReturn(Optional.of(invoice));

		Payment processedPayment = billingService.processPayment(invoice.getInvoiceNumber(), BigDecimal.valueOf(100));

		assertEquals(PaymentStatus.SUCCESS, processedPayment.getPaymentStatus());
		verify(paymentRepository).save(any(Payment.class));
		verify(invoiceRepository).save(any(Invoice.class));
	}

	@Test
	void processPayment_PartiallyPaid() {
		when(invoiceRepository.findByInvoiceNumber(anyString())).thenReturn(Optional.of(invoice));

		Payment processedPayment = billingService.processPayment(invoice.getInvoiceNumber(), BigDecimal.valueOf(50));

		assertEquals(PaymentStatus.PENDING, processedPayment.getPaymentStatus());
		verify(paymentRepository).save(any(Payment.class));
		verify(invoiceRepository).save(any(Invoice.class));
	}

	@Test
	void billTask_NewInvoiceCreated() {
		when(taskCostRepository.findByTaskType(TaskType.SURGERY))
			.thenReturn(Optional.of(new TaskCost(1L, TaskType.SURGERY, BigDecimal.valueOf(500))));

		when(invoiceRepository.findByOwnerId(anyLong())).thenReturn(Optional.empty());

		Invoice newInvoice = new Invoice();
		newInvoice.setInvoiceNumber(UUID.randomUUID().toString());
		newInvoice.setOwnerId(1L);
		newInvoice.setTotalAmount(BigDecimal.ZERO); // Initially zero

		when(invoiceRepository.save(any(Invoice.class))).thenAnswer(invocation -> {
			Invoice savedInvoice = invocation.getArgument(0);
			newInvoice.setTotalAmount(savedInvoice.getTotalAmount());
			return savedInvoice; // Return the updated invoice
		});

		billingService.billTask(taskBillingRequest);

		verify(invoiceRepository, times(2)).save(any(Invoice.class)); // Invoice saved twice
		verify(chargeableTaskRepository).save(any(ChargeableTask.class));

		assertEquals(BigDecimal.valueOf(500), newInvoice.getTotalAmount());
	}


	@Test
	void billTask_ExistingInvoice() {
		when(taskCostRepository.findByTaskType(TaskType.SURGERY))
			.thenReturn(Optional.of(new TaskCost(1L, TaskType.SURGERY, BigDecimal.valueOf(500))));

		when(invoiceRepository.findByOwnerId(anyLong())).thenReturn(Optional.of(invoice));
		when(chargeableTaskRepository.save(any(ChargeableTask.class))).thenReturn(new ChargeableTask());

		billingService.billTask(taskBillingRequest);

		verify(invoiceRepository, times(1)).save(any(Invoice.class));
		verify(chargeableTaskRepository).save(any(ChargeableTask.class));

		assertEquals(BigDecimal.valueOf(600), invoice.getTotalAmount());
	}

	@Test
	void billResource_NewInvoiceCreated() {
		when(resourceCostRepository.findByResourceId(anyLong()))
			.thenReturn(Optional.of(new ResourceCost(1L, 1L, "Vaccine", BigDecimal.valueOf(50))));

		when(invoiceRepository.findByOwnerId(anyLong())).thenReturn(Optional.empty());
		when(chargeableResourceRepository.save(any(ChargeableResource.class))).thenReturn(new ChargeableResource());
		when(invoiceRepository.save(any(Invoice.class))).thenReturn(invoice);

		billingService.billResource(resourceBillingRequest);

		verify(invoiceRepository, times(2)).save(any(Invoice.class));
		verify(chargeableResourceRepository).save(any(ChargeableResource.class));
		assertEquals(invoice.getTotalAmount(), BigDecimal.valueOf(100));
	}

	@Test
	void billResource_ExistingInvoice() {
		when(resourceCostRepository.findByResourceId(anyLong()))
			.thenReturn(Optional.of(new ResourceCost(1L, 1L, "Vaccine", BigDecimal.valueOf(50))));

		when(invoiceRepository.findByOwnerId(anyLong())).thenReturn(Optional.of(invoice));
		when(chargeableResourceRepository.save(any(ChargeableResource.class))).thenReturn(new ChargeableResource());

		billingService.billResource(resourceBillingRequest);

		verify(invoiceRepository, times(1)).save(any(Invoice.class));
		verify(chargeableResourceRepository).save(any(ChargeableResource.class));
		assertEquals(invoice.getTotalAmount(), BigDecimal.valueOf(200));
	}

	@Test
	void getInvoiceHistory_Success() {
		when(invoiceRepository.findByOwnerIdAndStatusAndIssueDateBetween(anyLong(), any(InvoiceStatus.class), any(LocalDate.class), any(LocalDate.class), any(Pageable.class)))
			.thenReturn(Collections.singletonList(invoice));

		List<Invoice> invoiceHistory = billingService.getInvoiceHistory(1L, InvoiceStatus.PENDING, LocalDate.now().minusDays(1), LocalDate.now(), Pageable.unpaged());

		assertFalse(invoiceHistory.isEmpty());
		verify(invoiceRepository).findByOwnerIdAndStatusAndIssueDateBetween(anyLong(), any(InvoiceStatus.class), any(LocalDate.class), any(LocalDate.class), any(Pageable.class));
	}

	@Test
	void getPaymentHistory_Success() {
		when(paymentRepository.findByOwnerId(anyLong(), any(Pageable.class)))
			.thenReturn(Collections.singletonList(payment));

		List<Payment> paymentHistory = billingService.getPaymentHistory(1L, Pageable.unpaged());

		assertFalse(paymentHistory.isEmpty());
		verify(paymentRepository).findByOwnerId(anyLong(), any(Pageable.class));
	}

	@Test
	void billTask_NoCostFound() {
		when(taskCostRepository.findByTaskType(TaskType.SURGERY))
			.thenReturn(Optional.empty());

		Exception exception = assertThrows(IllegalArgumentException.class, () -> {
			billingService.billTask(taskBillingRequest);
		});

		assertEquals("Cost not found for task type: SURGERY", exception.getMessage());
	}

	@Test
	void billResource_NoCostFound() {
		when(resourceCostRepository.findByResourceId(anyLong()))
			.thenReturn(Optional.empty());

		Exception exception = assertThrows(IllegalArgumentException.class, () -> {
			billingService.billResource(resourceBillingRequest);
		});

		assertEquals("Cost not found for resource ID: 1", exception.getMessage());
	}
}
