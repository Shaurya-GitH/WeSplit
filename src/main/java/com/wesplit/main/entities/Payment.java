package com.wesplit.main.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long paymentId;
    @Column(nullable = false)
    private LocalDate createdAt;
    @Column(nullable = false)
    private BigDecimal amountPaid;

    @ManyToOne
    @JoinColumn(name = "paid_by", referencedColumnName = "userId",nullable = false)
    private User paidBy;

    @ManyToOne
    @JoinColumn(name = "paid_to",referencedColumnName = "userId",nullable = false)
    private User paidTo;
}
