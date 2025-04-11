package com.wesplit.main.payloads;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ExpenseDTO {
    @NotBlank(message = "field must not be blank")
    private String description;
    @NotBlank(message = "field must not be blank")
    private BigDecimal amount;
    @NotBlank(message = "field must not be blank")
    private LocalDate createdAt;
    @NotBlank(message = "field must not be blank")
    private ExpenseType expenseType;
    private BigDecimal user1paid;
    private BigDecimal user2paid;
    private BigDecimal user1owe;
    private BigDecimal user2owe;
}
