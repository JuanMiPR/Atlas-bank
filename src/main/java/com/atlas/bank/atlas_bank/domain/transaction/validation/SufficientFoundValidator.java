package com.atlas.bank.atlas_bank.domain.transaction.validation;

import com.atlas.bank.atlas_bank.domain.account.exception.InsufficientFoundException;
import com.atlas.bank.atlas_bank.domain.transaction.model.TransfersContext;

import java.math.BigDecimal;

public class SufficientFoundValidator implements TransferValidator {
    @Override
    public void validate(TransfersContext context, BigDecimal fee) {
        BigDecimal totalDebit = context.amount().add(fee);

        if (context.from()
                .getBalance()
                .getAmount()
                .compareTo(totalDebit) < 0)
            throw new InsufficientFoundException(context.from()
                    .getId(), context.from()
                    .getBalance().getAmount(), totalDebit);
    }
}
