package com.wesplit.main.entities;

import com.wesplit.main.payloads.ExpenseType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Entity
@Table
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Expense {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE,generator = "money_sequence")
    @SequenceGenerator(name = "money_sequence",sequenceName ="money_sequence",allocationSize = 1)
    private Long expenseId;
    private String description;
    private BigDecimal amount;
    private LocalDate createdAt;
    private ExpenseType expenseType;
    private Boolean settled;
    private Long groupId;
    @Column(nullable = false)
    private String currency;

    @OneToMany(mappedBy = "expense", cascade = CascadeType.ALL)
    private List<ExpenseSplit> splitList;
}
