package com.atlas.bank.atlas_bank.domain.model.transaction.state;

import com.atlas.bank.atlas_bank.domain.model.transaction.TransactionStatusEnum;

public record PendingState() implements TransactionState {
    @Override
    public TransactionStatusEnum status() {
        return TransactionStatusEnum.PENDING;
    }

    @Override
    public TransactionState validate() {
        return new ValidatedState();
    }

    @Override
    public TransactionState reject(String reason) {
        return new RejectedState();
    }
}
