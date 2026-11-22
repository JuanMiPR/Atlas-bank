package com.atlas.bank.atlas_bank.domain.event;

import com.atlas.bank.atlas_bank.domain.model.transaction.TransactionTypeEnum;

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
