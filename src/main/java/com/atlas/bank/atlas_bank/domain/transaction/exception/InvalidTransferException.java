package com.atlas.bank.atlas_bank.domain.transaction.exception;

public class InvalidTransferException extends RuntimeException {
    public InvalidTransferException(String reason) {
        super(reason);
    }
}
