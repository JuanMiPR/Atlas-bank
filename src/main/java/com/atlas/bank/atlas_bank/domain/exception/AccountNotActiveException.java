package com.atlas.bank.atlas_bank.domain.exception;

import com.atlas.bank.atlas_bank.domain.model.account.AccountStatusEnum;

public class AccountNotActiveException extends RuntimeException {
    public AccountNotActiveException(Long accountId, AccountStatusEnum status) {
        super("Account " + accountId + " is not active " + status);
    }
}
