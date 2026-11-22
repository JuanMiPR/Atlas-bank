package com.atlas.bank.atlas_bank.domain.transaction.fee;

import com.atlas.bank.atlas_bank.domain.account.enums.AccountTypeEnum;

import java.math.BigDecimal;

public class CheckingFeeCalculator implements FeeCalculator {
    @Override
    public boolean supports(AccountTypeEnum accountType) {
        return AccountTypeEnum.CHECKING.equals(accountType);
    }

    @Override
    public BigDecimal calculate(BigDecimal amount) {
        return amount.multiply(new BigDecimal("0.015"));
    }
}
