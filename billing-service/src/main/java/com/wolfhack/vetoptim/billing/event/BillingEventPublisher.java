package com.wolfhack.vetoptim.billing.event;

import com.wolfhack.vetoptim.common.event.billing.InvoiceCreatedEvent;
import com.wolfhack.vetoptim.common.event.billing.PaymentProcessedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.retry.annotation.Recover;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class BillingEventPublisher {

    private final RabbitTemplate rabbitTemplate;

    @Retryable(
        retryFor = { Exception.class },
        maxAttempts = 5,
        backoff = @Backoff(delay = 2000, multiplier = 2)
    )
    public void publishInvoiceCreatedEvent(InvoiceCreatedEvent event) {
        rabbitTemplate.convertAndSend("billing.exchange", "invoice.created", event);
        log.info("InvoiceCreatedEvent published for invoice {}", event.getInvoiceNumber());
    }

    @Retryable(
        retryFor = { Exception.class },
        maxAttempts = 5,
        backoff = @Backoff(delay = 2000, multiplier = 2)
    )
    public void publishPaymentProcessedEvent(PaymentProcessedEvent event) {
        rabbitTemplate.convertAndSend("billing.exchange", "payment.processed", event);
        log.info("PaymentProcessedEvent published for invoice {}", event.getInvoiceNumber());
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
