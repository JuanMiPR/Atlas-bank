package com.atlas.bank.atlas_bank.application.transaction.service;

import com.atlas.bank.atlas_bank.application.account.port.out.AccountRepositoryPort;
import com.atlas.bank.atlas_bank.application.transaction.command.TransferMoneyCommand;
import com.atlas.bank.atlas_bank.application.transaction.port.in.TransferMoneyUseCase;
import com.atlas.bank.atlas_bank.application.transaction.port.out.TransactionRepositoryPort;
import com.atlas.bank.atlas_bank.domain.account.exception.AccountNotFoundException;
import com.atlas.bank.atlas_bank.domain.account.model.Account;
import com.atlas.bank.atlas_bank.domain.transaction.exception.InvalidTransferException;
import com.atlas.bank.atlas_bank.domain.transaction.fee.FeeCalculator;
import com.atlas.bank.atlas_bank.domain.transaction.model.Transaction;
import com.atlas.bank.atlas_bank.domain.transaction.model.TransfersContext;
import com.atlas.bank.atlas_bank.domain.transaction.service.TransferDomainService;
import com.atlas.bank.atlas_bank.domain.transaction.validation.TransferValidator;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

@Service
public class TransferServiceImpl extends TransactionProcessor<TransfersContext> implements TransferMoneyUseCase {

    private static final int MONEY_SCALE = 2;

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
        if (command.fromId() == null || command.toId() == null)
            throw new InvalidTransferException("The origin and destination accounts are required");

        Map<Long, Account> accounts = lockAccounts(command.fromId(), command.toId());
        Account from = accounts.get(command.fromId());
        Account to = accounts.get(command.toId());

        Transaction transaction = this.process(new TransfersContext(from, to, command.amount(),
                "Transfer from account " + from.getId() + " to account " + to.getId()));
        transaction.executeTransfer();

        transactionRepository.save(transaction);

        return transaction;
    }

    /**
     * Locks the involved accounts in ascending id order so two opposite transfers
     * (A→B and B→A) cannot deadlock. If both ids are equal the same instance is returned
     * for both, and the validators reject the transfer.
     */
    private Map<Long, Account> lockAccounts(Long fromId, Long toId) {
        Map<Long, Account> accounts = new HashMap<>();
        Stream.of(fromId, toId)
                .distinct()
                .sorted()
                .forEach(id -> accounts.put(id, accountRepositoryPort.findByIdForUpdate(id)
                        .orElseThrow(() -> new AccountNotFoundException(id))));
        return accounts;
    }

    @Override
    protected void validate(TransfersContext context, BigDecimal fee) {
        validators.forEach(validator -> validator.validate(context, fee));
    }

    @Override
    protected BigDecimal calculateFee(TransfersContext context) {
        if (context.amount() == null)
            throw new InvalidTransferException("The amount must be positive");

        return feeCalculators.stream()
                .filter(feeCalculator -> feeCalculator.supports(context.from()
                        .getType()))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("No fee calculator found"))
                .calculate(context.amount())
                .setScale(MONEY_SCALE, RoundingMode.HALF_UP);
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
