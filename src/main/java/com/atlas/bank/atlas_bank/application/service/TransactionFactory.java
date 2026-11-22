package com.atlas.bank.atlas_bank.application.service;

import com.atlas.bank.atlas_bank.domain.model.shared.Money;
import com.atlas.bank.atlas_bank.domain.model.transaction.Transaction;
import com.atlas.bank.atlas_bank.domain.model.transaction.TransactionStatusEnum;
import com.atlas.bank.atlas_bank.domain.model.transaction.TransactionTypeEnum;
import com.atlas.bank.atlas_bank.domain.model.transaction.state.PendingState;
import com.atlas.bank.atlas_bank.domain.model.transaction.TransfersContext;

import java.math.BigDecimal;

public class TransactionFactory {
    public static Transaction createTransfer(TransfersContext context, BigDecimal fee) {
        Transaction transaction = new Transaction();
        transaction.setType(TransactionTypeEnum.TRANSFER);
        transaction.setSourceAccountId(context.from()
                .getId());
        transaction.setTargetAccountId(context.to()
                .getId());

        transaction.setAmount(Money.of(context.amount(), context.to()
                .getBalance()
                .getCurrency()));

        transaction.setFee(fee);
        transaction.setDescription(context.description());


        transaction.setStatus(TransactionStatusEnum.PENDING);
        transaction.advanceTo(new PendingState());

        return transaction;
    }
}
