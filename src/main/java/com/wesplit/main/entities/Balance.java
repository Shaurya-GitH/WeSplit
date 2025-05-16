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
public class Balance {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long balanceId;
    //balance is stored in INR
    private BigDecimal oneOweTwo;
    private BigDecimal twoOweOne;

    @ManyToOne
    @JoinColumn(referencedColumnName = "userId")
    private User user1;
    @ManyToOne
    @JoinColumn(referencedColumnName = "userId")
    private User user2;

    @Column(nullable = true)
    private Long groupId;

}
