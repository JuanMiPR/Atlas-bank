package com.atlas.bank.atlas_bank.domain.transaction.event;

import com.atlas.bank.atlas_bank.domain.transaction.enums.TransactionTypeEnum;

import java.math.BigDecimal;

public record TransactionExecutedEvent(
        Long transactionId,
        TransactionTypeEnum type,
        Long sourceAccountId,
        Long targetAccountId,
        BigDecimal amount,
        BigDecimal fee
) {
}
