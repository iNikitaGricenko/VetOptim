package com.wolfhack.vetoptim.billing.event;

import com.wolfhack.vetoptim.common.event.billing.InvoiceCreatedEvent;
import com.wolfhack.vetoptim.common.event.billing.PaymentProcessedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@Slf4j
@Component
@RequiredArgsConstructor
public class BillingEventPublisher {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Value("${kafka.topic.billing.invoice.created}")
    private String invoiceCreatedTopic;

    @Value("${kafka.topic.billing.payment.processed}")
    private String paymentProcessedTopic;

    @Retryable(
        retryFor = { ExecutionException.class, TimeoutException.class, InterruptedException.class },
        maxAttempts = 5,
        backoff = @Backoff(delay = 2000, multiplier = 2)
    )
    public void publishInvoiceCreatedEvent(InvoiceCreatedEvent event) {
        log.info("Publishing invoice created event for invoice {}", event.getInvoiceNumber());
        String key = "invoice-" + event.getInvoiceNumber();

        try {
            kafkaTemplate.send(invoiceCreatedTopic, key, event).get(10, TimeUnit.SECONDS);
            log.info("InvoiceCreatedEvent published for invoice {}", event.getInvoiceNumber());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("Interrupted while publishing invoice created event for invoice {}", event.getInvoiceNumber(), e);
            throw new RuntimeException("Message sending interrupted", e);
        } catch (ExecutionException | TimeoutException e) {
            log.error("Failed to publish invoice created event for invoice {}", event.getInvoiceNumber(), e);
            throw new RuntimeException("Failed to send message", e);
        }
    }

    @Retryable(
        retryFor = { ExecutionException.class, TimeoutException.class, InterruptedException.class },
        maxAttempts = 5,
        backoff = @Backoff(delay = 2000, multiplier = 2)
    )
    public void publishPaymentProcessedEvent(PaymentProcessedEvent event) {
        log.info("Publishing payment processed event for invoice {}", event.getInvoiceNumber());
        String key = "payment-" + event.getInvoiceNumber();

        try {
            kafkaTemplate.send(paymentProcessedTopic, key, event).get(10, TimeUnit.SECONDS);
            log.info("PaymentProcessedEvent published for invoice {}", event.getInvoiceNumber());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("Interrupted while publishing payment processed event for invoice {}", event.getInvoiceNumber(), e);
            throw new RuntimeException("Message sending interrupted", e);
        } catch (ExecutionException | TimeoutException e) {
            log.error("Failed to publish payment processed event for invoice {}", event.getInvoiceNumber(), e);
            throw new RuntimeException("Failed to send message", e);
        }
    }

    @Recover
    public void recover(Exception ex, InvoiceCreatedEvent event) {
        log.error("Failed to publish InvoiceCreatedEvent after retries for invoice {}", event.getInvoiceNumber(), ex);
    }

    @Recover
    public void recover(Exception ex, PaymentProcessedEvent event) {
        log.error("Failed to publish PaymentProcessedEvent after retries for invoice {}", event.getInvoiceNumber(), ex);
    }
}
