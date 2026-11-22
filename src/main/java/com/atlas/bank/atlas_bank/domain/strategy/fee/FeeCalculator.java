package com.atlas.bank.atlas_bank.domain.strategy.fee;

import com.atlas.bank.atlas_bank.domain.model.account.AccountTypeEnum;

import java.math.BigDecimal;

public interface FeeCalculator {

    boolean supports(AccountTypeEnum accountType);
    BigDecimal calculate(BigDecimal amount);
}
