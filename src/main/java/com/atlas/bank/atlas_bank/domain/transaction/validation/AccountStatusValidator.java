package com.atlas.bank.atlas_bank.domain.transaction.validation;

import com.atlas.bank.atlas_bank.domain.account.enums.AccountStatusEnum;
import com.atlas.bank.atlas_bank.domain.account.exception.AccountNotActiveException;
import com.atlas.bank.atlas_bank.domain.transaction.model.TransfersContext;

import java.math.BigDecimal;

public class AccountStatusValidator implements TransferValidator {
    @Override
    public void validate(TransfersContext context, BigDecimal fee) {
        if (!AccountStatusEnum.ACTIVE.equals(context.from()
                .getStatus()))
            throw new AccountNotActiveException(context.from()
                .getId(), context.from()
                .getStatus());

        if (!AccountStatusEnum.ACTIVE.equals(context.to()
                .getStatus()))
            throw new AccountNotActiveException(context.to()
                .getId(), context.to()
                .getStatus());
    }
}
