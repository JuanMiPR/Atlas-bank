package com.atlas.bank.atlas_bank.domain.validation;

import com.atlas.bank.atlas_bank.domain.exception.InsufficientFoundException;
import com.atlas.bank.atlas_bank.domain.model.transaction.TransfersContext;

public class SufficientFoundValidator implements TransferValidator {
    @Override
    public void validate(TransfersContext context) {
        if (context.from()
                .getBalance()
                .getAmount()
                .compareTo(context.amount()) < 0)
            throw new InsufficientFoundException(context.from()
                    .getId(), context.from()
                    .getBalance().getAmount(), context.amount());
    }
}
