package com.atlas.bank.atlas_bank.application.transaction.port.out;

import com.atlas.bank.atlas_bank.application.transaction.dto.FraudCheckResult;

import java.math.BigDecimal;

public interface FraudCheckPort {
    FraudCheckResult check(Long accountId, BigDecimal amount);
}
