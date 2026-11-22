package com.atlas.bank.atlas_bank.application.transaction.service;

import com.atlas.bank.atlas_bank.application.transaction.port.in.GetTransactionsByAccountUseCase;
import com.atlas.bank.atlas_bank.application.transaction.port.out.TransactionRepositoryPort;
import com.atlas.bank.atlas_bank.application.transaction.query.GetAccountStatementQuery;
import com.atlas.bank.atlas_bank.application.transaction.query.TransactionReadModel;
import com.atlas.bank.atlas_bank.domain.transaction.model.Transaction;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TransactionQueryService implements GetTransactionsByAccountUseCase {
    private final TransactionRepositoryPort transactionRepository;


    @Override
    public List<TransactionReadModel> getByAccountId(GetAccountStatementQuery query) {
        return transactionRepository.findBySourceAccountIdOrByTargetAccountId(query.accountId(), query.accountId())
                .stream()
                .map(this::toTransactionReadModel)
                .collect(Collectors.toList());
    }

    private TransactionReadModel toTransactionReadModel(Transaction transaction) {
        return new TransactionReadModel(transaction.getId(),
                transaction.getType()
                        .name(),
                transaction.getSourceAccountId(),
                transaction.getTargetAccountId(),
                transaction.getAmount()
                        .getAmount(),
                transaction.getFee(),
                transaction.getStatus()
                        .name(),
                transaction.getCreatedAt());
    }
}
