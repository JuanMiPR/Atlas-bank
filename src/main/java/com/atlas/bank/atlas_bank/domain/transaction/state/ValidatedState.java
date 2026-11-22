package com.atlas.bank.atlas_bank.domain.transaction.state;

import com.atlas.bank.atlas_bank.domain.transaction.enums.TransactionStatusEnum;

public record ValidatedState() implements TransactionState {
    @Override
    public TransactionStatusEnum status() {
        return TransactionStatusEnum.VALIDATED;
    }

    @Override
    public TransactionState reject(String reason) {
        return new RejectedState();
    }

    @Override
    public TransactionState execute() {
        return new ExecutedState();
    }
}
