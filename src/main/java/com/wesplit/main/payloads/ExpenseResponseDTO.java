package com.wesplit.main.payloads;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ExpenseResponseDTO {
    private Long expenseId;
    private String description;
    private BigDecimal amount;
    private LocalDate createdAt;
    private ExpenseType expenseType;
    private Boolean settled;
    private String currency;
}
