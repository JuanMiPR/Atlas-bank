package com.atlas.bank.atlas_bank.domain.strategy.fee;

import com.atlas.bank.atlas_bank.domain.model.account.AccountTypeEnum;

import java.math.BigDecimal;

public class DefaultFeeCalculator implements FeeCalculator {
    @Override
    public boolean supports(AccountTypeEnum accountType) {
        return true;
    }

    @Override
    public BigDecimal calculate(BigDecimal amount) {
        return BigDecimal.ZERO;
    }
}
