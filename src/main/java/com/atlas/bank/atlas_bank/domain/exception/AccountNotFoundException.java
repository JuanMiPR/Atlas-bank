package com.atlas.bank.atlas_bank.domain.exception;

public class AccountNotFoundException extends RuntimeException {
    public AccountNotFoundException(Long accountId) {
        super("The account" + accountId + " does not exist");
    }
}
