package com.wolfhack.vetoptim.billing.event;

import com.wolfhack.vetoptim.common.event.billing.InvoiceCreatedEvent;
import com.wolfhack.vetoptim.common.event.billing.PaymentProcessedEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.concurrent.CompletableFuture;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BillingEventPublisherTest {

    @Mock
    private KafkaTemplate<String, Object> kafkaTemplate;

    @InjectMocks
    private BillingEventPublisher billingEventPublisher;

    private InvoiceCreatedEvent invoiceCreatedEvent;
    private PaymentProcessedEvent paymentProcessedEvent;

    @BeforeEach
    void setUp() {
        invoiceCreatedEvent = new InvoiceCreatedEvent("INV123", 1L, null, null, null, null);
        paymentProcessedEvent = new PaymentProcessedEvent("INV123", null, null, null);

        // Set Kafka topic properties
        ReflectionTestUtils.setField(billingEventPublisher, "invoiceCreatedTopic", "invoice-created");
        ReflectionTestUtils.setField(billingEventPublisher, "paymentProcessedTopic", "payment-processed");

        // Mock the CompletableFuture returned by kafkaTemplate.send()
        CompletableFuture<SendResult<String, Object>> future = CompletableFuture.completedFuture(null);
        when(kafkaTemplate.send(anyString(), anyString(), any())).thenReturn(future);
    }

    @Test
    void publishInvoiceCreatedEvent_Success() {
        String key = "invoice-" + invoiceCreatedEvent.getInvoiceNumber();
        billingEventPublisher.publishInvoiceCreatedEvent(invoiceCreatedEvent);

        verify(kafkaTemplate).send("invoice-created", key, invoiceCreatedEvent);
    }

    @Test
    void publishPaymentProcessedEvent_Success() {
        String key = "payment-" + paymentProcessedEvent.getInvoiceNumber();
        billingEventPublisher.publishPaymentProcessedEvent(paymentProcessedEvent);

        verify(kafkaTemplate).send("payment-processed", key, paymentProcessedEvent);
    }
}
