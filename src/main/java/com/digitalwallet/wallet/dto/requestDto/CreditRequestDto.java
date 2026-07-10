package com.digitalwallet.wallet.dto.requestDto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreditRequestDto {
    private UUID walletId;
    private BigDecimal amount;
}
