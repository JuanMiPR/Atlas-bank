package com.atlas.bank.atlas_bank.domain.strategy.fee;

import com.atlas.bank.atlas_bank.domain.model.account.AccountTypeEnum;

import java.math.BigDecimal;

public class SavingsFeeCalculator implements FeeCalculator {
    @Override
    public boolean supports(AccountTypeEnum accountType) {
        return AccountTypeEnum.SAVINGS.equals(accountType);
    }

    @Override
    public BigDecimal calculate(BigDecimal amount) {
        return amount.multiply(new BigDecimal("0.01"));
    }
}
