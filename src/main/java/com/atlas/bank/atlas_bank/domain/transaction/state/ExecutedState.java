package com.atlas.bank.atlas_bank.domain.transaction.state;


import com.atlas.bank.atlas_bank.domain.transaction.enums.TransactionStatusEnum;

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
