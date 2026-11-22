package com.atlas.bank.atlas_bank.domain.account.exception;

import com.atlas.bank.atlas_bank.domain.account.enums.AccountStatusEnum;

public class AccountNotActiveException extends RuntimeException {
    public AccountNotActiveException(Long accountId, AccountStatusEnum status) {
        super("Account " + accountId + " is not active (status: " + status + ")");
    }
}
