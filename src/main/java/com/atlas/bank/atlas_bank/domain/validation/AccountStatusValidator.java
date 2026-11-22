package com.atlas.bank.atlas_bank.domain.validation;

import com.atlas.bank.atlas_bank.domain.model.account.AccountStatusEnum;
import com.atlas.bank.atlas_bank.domain.exception.AccountNotActiveException;
import com.atlas.bank.atlas_bank.domain.model.transaction.TransfersContext;

public class AccountStatusValidator implements TransferValidator {
    @Override
    public void validate(TransfersContext context) {
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
