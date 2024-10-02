package com.wolfhack.vetoptim.common.event.billing;

import com.wolfhack.vetoptim.common.PaymentStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentProcessedEvent implements Serializable {

    private String invoiceNumber;
    private BigDecimal amountPaid;
    private LocalDate paymentDate;
    private PaymentStatus paymentStatus;
}