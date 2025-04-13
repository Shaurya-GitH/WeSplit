package com.wesplit.main.payloads;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PaymentDTO {
    @NotNull
    private LocalDate createdAt;
    @NotNull
    private BigDecimal amountPaid;
}
