package com.atlas.bank.atlas_bank.domain.transaction.fee;

import com.atlas.bank.atlas_bank.domain.account.enums.AccountTypeEnum;

import java.math.BigDecimal;

public interface FeeCalculator {

    boolean supports(AccountTypeEnum accountType);
    BigDecimal calculate(BigDecimal amount);
}
