package com.atlas.bank.atlas_bank.domain.exception;

import java.math.BigDecimal;

public class InsufficientFoundException extends RuntimeException {
    public InsufficientFoundException(Long accountId, BigDecimal balance, BigDecimal amount) {
        super("The account" + accountId + " has insufficient funds to balance " + balance + " and " + amount);
    }
}
