package com.atlas.bank.atlas_bank.infrastructure.transaction.adapter.out.persistence;

import com.atlas.bank.atlas_bank.application.transaction.port.out.TransactionRepositoryPort;
import com.atlas.bank.atlas_bank.domain.transaction.model.Transaction;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class JpaTransactionRepositoryAdapter implements TransactionRepositoryPort {
    private final SpringDataTransactionRepository transactionRepository;
    private final TransactionPersistenceMapper transactionMapper;
    private final ApplicationEventPublisher eventPublisher;


    @Override
    public Transaction save(Transaction transaction) {
        transaction.initDefaults();
        TransactionJpaEntity transactionJpaEntity = transactionMapper.toJpaEntity(transaction);
        TransactionJpaEntity saved = transactionRepository.save(transactionJpaEntity);

        Transaction res = transactionMapper.toDomain(saved);

        transaction.getDomainEvents()
                .forEach(eventPublisher::publishEvent);
        transaction.clearDomainEvents();

        return res;
    }

    @Override
    public List<Transaction> findBySourceAccountIdOrByTargetAccountId(Long sourceAccountId, Long targetAccountId) {
        return transactionRepository.findBySourceAccountIdOrTargetAccountId(sourceAccountId, targetAccountId)
                .stream()
                .map(transactionMapper::toDomain)
                .collect(Collectors.toList());
    }
}
