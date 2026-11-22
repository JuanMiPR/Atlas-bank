package com.atlas.bank.atlas_bank.domain.transaction.state;

import com.atlas.bank.atlas_bank.domain.transaction.enums.TransactionStatusEnum;

public record RejectedState() implements TransactionState {

    @Override
    public TransactionStatusEnum status() {
        return TransactionStatusEnum.REJECTED;
    }
}
