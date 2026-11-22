package com.atlas.bank.atlas_bank.application.service;

import com.atlas.bank.atlas_bank.application.command.TransferMoneyCommand;
import com.atlas.bank.atlas_bank.application.port.out.TransactionRepositoryPort;
import com.atlas.bank.atlas_bank.domain.exception.AccountNotFoundException;
import com.atlas.bank.atlas_bank.domain.model.account.Account;
import com.atlas.bank.atlas_bank.application.port.in.TransferMoneyUseCase;
import com.atlas.bank.atlas_bank.application.port.out.AccountRepositoryPort;
import com.atlas.bank.atlas_bank.domain.model.transaction.TransfersContext;
import com.atlas.bank.atlas_bank.domain.model.transaction.Transaction;
import com.atlas.bank.atlas_bank.domain.service.TransferDomainService;
import com.atlas.bank.atlas_bank.domain.strategy.fee.FeeCalculator;
import com.atlas.bank.atlas_bank.domain.validation.TransferValidator;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
public class TransferServiceImpl extends TransactionProcessor<TransfersContext> implements TransferMoneyUseCase {

    private final AccountRepositoryPort accountRepositoryPort;

    private final List<FeeCalculator> feeCalculators;

    private final List<TransferValidator> validators;

    private final TransferDomainService transferDomainService;

    public TransferServiceImpl(AccountRepositoryPort accountRepositoryPort, List<FeeCalculator> feeCalculators, TransferDomainService transferDomainService, TransactionRepositoryPort transactionRepository, List<TransferValidator> validators) {
        super(transactionRepository);
        this.accountRepositoryPort = accountRepositoryPort;
        this.feeCalculators = feeCalculators;
        this.validators = validators;
        this.transferDomainService = transferDomainService;
    }

    @Transactional
    @Override
    public Transaction transfer(TransferMoneyCommand command) {
        Account from = accountRepositoryPort.findById(command.fromId())
                .orElseThrow(() -> new AccountNotFoundException(command.fromId()));
        Account to = accountRepositoryPort.findById(command.toId())
                .orElseThrow(() -> new AccountNotFoundException(command.toId()));

        Transaction transaction = this.process(new TransfersContext(from, to, command.amount(), "foo"));
        transaction.executeTransfer();

        transactionRepository.save(transaction);

        return transaction;
    }

    @Override
    protected void validate(TransfersContext context) {
        validators.forEach(validator -> validator.validate(context));
    }

    @Override
    protected BigDecimal calculateFee(TransfersContext context) {
        return feeCalculators.stream()
                .filter(feeCalculator -> feeCalculator.supports(context.from()
                        .getType()))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("No fee calculator found"))
                .calculate(context.amount());
    }

    @Override
    protected void execute(TransfersContext context, BigDecimal fee) {
        transferDomainService.transfer(context.from(), context.to(), context.amount(), fee);
        accountRepositoryPort.save(context.from());
        accountRepositoryPort.save(context.to());
    }

    @Override
    protected Transaction save(TransfersContext context, BigDecimal fee) {
        return transactionRepository.save(TransactionFactory.createTransfer(context, fee));
    }
}
