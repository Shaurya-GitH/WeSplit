package com.wesplit.main.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Table
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ExpenseSplit {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long expenseSplitId;
    private BigDecimal owed;
    private BigDecimal paid;

    @ManyToOne
    @JoinColumn(name="payer_id",referencedColumnName = "userId")
    private User user;
    @ManyToOne
    @JoinColumn(name = "expense_ref",referencedColumnName = "expenseId")
    private Expense expense;
}
