package com.wesplit.main.payloads;

import com.wesplit.main.entities.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserDebt {
    private User user;
    private BigDecimal debt;
}
