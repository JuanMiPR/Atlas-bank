package com.atlas.bank.atlas_bank.domain.transaction.fee;

import com.atlas.bank.atlas_bank.domain.account.enums.AccountTypeEnum;

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
