package com.atlas.bank.atlas_bank.domain.model.transaction.state;

import com.atlas.bank.atlas_bank.domain.model.transaction.TransactionStatusEnum;

public record RejectedState() implements TransactionState {

    @Override
    public TransactionStatusEnum status() {
        return TransactionStatusEnum.REJECTED;
    }
}
