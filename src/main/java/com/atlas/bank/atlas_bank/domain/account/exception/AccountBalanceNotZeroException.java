package com.atlas.bank.atlas_bank.domain.account.exception;

public class AccountBalanceNotZeroException extends IllegalStateException {
    public AccountBalanceNotZeroException() {
        super("Cannot close an account with non-zero balance");
    }
}
