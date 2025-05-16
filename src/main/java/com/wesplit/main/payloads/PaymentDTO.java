package com.wesplit.main.payloads;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Null;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PaymentDTO {
    @Null
    private LocalDate createdAt;
    @NotNull
    private BigDecimal amountPaid;
    @NotNull
    private String currency;
    private Long groupId;
    @NotNull
    private String email1;
    @NotNull
    private String email2;
}
