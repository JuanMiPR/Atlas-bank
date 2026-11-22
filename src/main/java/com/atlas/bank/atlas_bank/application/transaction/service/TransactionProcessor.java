package com.atlas.bank.atlas_bank.application.transaction.service;

import com.atlas.bank.atlas_bank.application.transaction.port.out.TransactionRepositoryPort;
import com.atlas.bank.atlas_bank.domain.transaction.model.Transaction;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;

@RequiredArgsConstructor
public abstract class TransactionProcessor<C> {

    protected final TransactionRepositoryPort transactionRepository;

    @Transactional
    public Transaction process(C context) {
        BigDecimal fee = calculateFee(context);
        validate(context, fee);
        execute(context, fee);
        return save(context, fee);
    }

    protected abstract void validate(C context, BigDecimal fee);

    protected abstract BigDecimal calculateFee(C context);

    protected abstract void execute(C context, BigDecimal fee);

    protected abstract Transaction save(C context, BigDecimal fee);

}
