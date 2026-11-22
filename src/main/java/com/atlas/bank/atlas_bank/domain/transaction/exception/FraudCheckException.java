package com.atlas.bank.atlas_bank.domain.transaction.exception;

public class FraudCheckException extends RuntimeException {
    public FraudCheckException(String reason) {
        super(reason);
    }
}
