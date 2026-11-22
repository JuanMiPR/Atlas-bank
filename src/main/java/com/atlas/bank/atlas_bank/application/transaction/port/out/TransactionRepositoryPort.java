package com.atlas.bank.atlas_bank.application.transaction.port.out;

import com.atlas.bank.atlas_bank.domain.transaction.model.Transaction;

import java.util.List;

public interface TransactionRepositoryPort {
    Transaction save(Transaction transaction);
    List<Transaction> findBySourceAccountIdOrByTargetAccountId(Long sourceAccountId, Long targetAccountId);
}
