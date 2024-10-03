package com.wolfhack.vetoptim.billing.service;

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
		when(invoiceRepository.findByOwnerId(anyLong())).thenReturn(Optional.empty());
		when(chargeableTaskRepository.save(any(ChargeableTask.class))).thenReturn(new ChargeableTask());
		when(invoiceRepository.save(any(Invoice.class))).thenReturn(new Invoice());

		billingService.billTask(taskBillingRequest);

		verify(invoiceRepository, times(2)).save(any(Invoice.class));
		verify(chargeableTaskRepository).save(any(ChargeableTask.class));
	}

	@Test
	void billTask_ExistingInvoice() {
		when(invoiceRepository.findByOwnerId(anyLong())).thenReturn(Optional.of(invoice));
		when(chargeableTaskRepository.save(any(ChargeableTask.class))).thenReturn(new ChargeableTask());

		billingService.billTask(taskBillingRequest);

		verify(invoiceRepository, times(1)).save(any(Invoice.class));
		verify(chargeableTaskRepository).save(any(ChargeableTask.class));
	}

	@Test
	void billResource_NewInvoiceCreated() {
		when(invoiceRepository.findByOwnerId(anyLong())).thenReturn(Optional.empty());
		when(chargeableResourceRepository.save(any(ChargeableResource.class))).thenReturn(new ChargeableResource());
		when(invoiceRepository.save(any(Invoice.class))).thenReturn(new Invoice());

		billingService.billResource(resourceBillingRequest);

		verify(invoiceRepository, times(2)).save(any(Invoice.class));
		verify(chargeableResourceRepository).save(any(ChargeableResource.class));
	}

	@Test
	void billResource_ExistingInvoice() {
		when(invoiceRepository.findByOwnerId(anyLong())).thenReturn(Optional.of(invoice));
		when(chargeableResourceRepository.save(any(ChargeableResource.class))).thenReturn(new ChargeableResource());

		billingService.billResource(resourceBillingRequest);

		verify(invoiceRepository, times(1)).save(any(Invoice.class));
		verify(chargeableResourceRepository).save(any(ChargeableResource.class));
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
}
