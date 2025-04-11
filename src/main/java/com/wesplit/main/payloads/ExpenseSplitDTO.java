package com.wesplit.main.payloads;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ExpenseSplitDTO {
    private Long expenseSplitId;
    private BigDecimal owed;
    private BigDecimal paid;
    private FriendDTO user;
}
