package com.wesplit.main.payloads;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Null;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GroupExpenseDTO {
    @NotBlank(message = "field must not be blank")
    private String description;
    @NotNull(message = "field must not be blank")
    private BigDecimal amount;
    @Null
    private LocalDate createdAt;
    @NotNull(message = "field must not be blank")
    private String currency;
    @NotNull
    private Long groupId;
    @NotNull
    private HashMap<String,BigDecimal> payments;
    @NotNull
    private HashMap<String,BigDecimal> owe;
}
