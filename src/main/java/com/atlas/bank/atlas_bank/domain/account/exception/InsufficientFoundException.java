package com.atlas.bank.atlas_bank.domain.account.exception;

import java.math.BigDecimal;

public class InsufficientFoundException extends RuntimeException {
    public InsufficientFoundException(Long accountId, BigDecimal balance, BigDecimal amount) {
        super("The account " + accountId + " has insufficient funds: balance " + balance + ", required " + amount);
    }
}
