package com.wolfhack.vetoptim.billing.event;

import com.wolfhack.vetoptim.common.event.billing.InvoiceCreatedEvent;
import com.wolfhack.vetoptim.common.event.billing.PaymentProcessedEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class BillingEventPublisherTest {

    @Mock
    private RabbitTemplate rabbitTemplate;

    @InjectMocks
    private BillingEventPublisher billingEventPublisher;

    private InvoiceCreatedEvent invoiceCreatedEvent;
    private PaymentProcessedEvent paymentProcessedEvent;

    @BeforeEach
    void setUp() {
        invoiceCreatedEvent = new InvoiceCreatedEvent("INV123", 1L, null, null, null, null);
        paymentProcessedEvent = new PaymentProcessedEvent("INV123", null, null, null);
    }

    @Test
    void publishInvoiceCreatedEvent_Success() {
        billingEventPublisher.publishInvoiceCreatedEvent(invoiceCreatedEvent);

        verify(rabbitTemplate).convertAndSend("billing.exchange", "invoice.created", invoiceCreatedEvent);
    }

    @Test
    void publishPaymentProcessedEvent_Success() {
        billingEventPublisher.publishPaymentProcessedEvent(paymentProcessedEvent);

        verify(rabbitTemplate).convertAndSend("billing.exchange", "payment.processed", paymentProcessedEvent);
    }
}
