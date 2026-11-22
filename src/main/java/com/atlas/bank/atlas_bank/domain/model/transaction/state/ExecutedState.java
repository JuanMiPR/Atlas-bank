package com.atlas.bank.atlas_bank.domain.model.transaction.state;


import com.atlas.bank.atlas_bank.domain.model.transaction.TransactionStatusEnum;

public record ExecutedState() implements TransactionState {
    @Override
    public TransactionStatusEnum status() {
        return TransactionStatusEnum.EXECUTED;
    }

    @Override
    public TransactionState reverse() {
        return new ReversedState();
    }
}
