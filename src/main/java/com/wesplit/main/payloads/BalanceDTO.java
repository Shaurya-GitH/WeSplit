package com.wesplit.main.payloads;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BalanceDTO {
    private BigDecimal oneOweTwo;
    private BigDecimal twoOweOne;
    private FriendDTO user1;
    private FriendDTO user2;
}
