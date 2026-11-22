package com.atlas.bank.atlas_bank.domain.transaction.state;


import com.atlas.bank.atlas_bank.domain.transaction.enums.TransactionStatusEnum;

public sealed interface TransactionState permits ExecutedState, PendingState, ValidatedState, RejectedState, ReversedState {

    TransactionStatusEnum status();

    default TransactionState execute() {
        throw new IllegalStateException("Can't execute transaction in state" + status());
    }

    default TransactionState validate() {
        throw new IllegalStateException("Can't validate transaction in state" + status());
    }

    default TransactionState reject(String reason) {
        throw new IllegalStateException("Can't reject transaction in state" + status());
    }

    default TransactionState reverse() {
        throw new IllegalStateException("Can't reverse transaction in state" + status());
    }
}
