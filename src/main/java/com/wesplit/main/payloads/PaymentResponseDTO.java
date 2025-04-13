package com.wesplit.main.payloads;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PaymentResponseDTO {
    private LocalDate createdAt;
    private BigDecimal amountPaid;
    private FriendDTO paidBy;
    private FriendDTO paidTo;
}
