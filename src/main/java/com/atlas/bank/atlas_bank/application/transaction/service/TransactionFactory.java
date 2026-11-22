package com.atlas.bank.atlas_bank.application.transaction.service;

import com.atlas.bank.atlas_bank.domain.shared.model.Money;
import com.atlas.bank.atlas_bank.domain.transaction.model.Transaction;
import com.atlas.bank.atlas_bank.domain.transaction.enums.TransactionStatusEnum;
import com.atlas.bank.atlas_bank.domain.transaction.enums.TransactionTypeEnum;
import com.atlas.bank.atlas_bank.domain.transaction.state.PendingState;
import com.atlas.bank.atlas_bank.domain.transaction.model.TransfersContext;

import java.math.BigDecimal;

public class TransactionFactory {
    public static Transaction createTransfer(TransfersContext context, BigDecimal fee) {
        Transaction transaction = new Transaction();
        transaction.setType(TransactionTypeEnum.TRANSFER);
        transaction.setSourceAccountId(context.from()
                .getId());
        transaction.setTargetAccountId(context.to()
                .getId());

        transaction.setAmount(Money.of(context.amount(), context.from()
                .getBalance()
                .getCurrency()));

        transaction.setFee(fee);
        transaction.setDescription(context.description());


        transaction.setStatus(TransactionStatusEnum.PENDING);
        transaction.advanceTo(new PendingState());

        return transaction;
    }
}
