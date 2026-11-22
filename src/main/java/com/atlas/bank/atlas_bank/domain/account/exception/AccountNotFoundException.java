package com.atlas.bank.atlas_bank.domain.account.exception;

public class AccountNotFoundException extends RuntimeException {
    public AccountNotFoundException(Long accountId) {
        super("The account " + accountId + " does not exist");
    }
}
