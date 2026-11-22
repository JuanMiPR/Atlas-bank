package com.atlas.bank.atlas_bank.application.service;

import com.atlas.bank.atlas_bank.application.command.CloseAccountCommand;
import com.atlas.bank.atlas_bank.application.command.CreateAccountCommand;
import com.atlas.bank.atlas_bank.application.port.in.CloseAccountUseCase;
import com.atlas.bank.atlas_bank.application.port.in.CreateAccountUseCase;
import com.atlas.bank.atlas_bank.application.port.in.GetAccountUseCase;
import com.atlas.bank.atlas_bank.application.port.in.ListAccountUseCase;
import com.atlas.bank.atlas_bank.domain.event.AccountClosedEvent;
import com.atlas.bank.atlas_bank.domain.exception.AccountNotFoundException;
import com.atlas.bank.atlas_bank.domain.model.account.Account;
import com.atlas.bank.atlas_bank.application.port.out.AccountRepositoryPort;
import com.atlas.bank.atlas_bank.domain.model.shared.CurrencyEnum;
import com.atlas.bank.atlas_bank.domain.model.shared.Email;
import com.atlas.bank.atlas_bank.domain.model.shared.Money;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AccountServiceImpl implements CreateAccountUseCase, GetAccountUseCase, ListAccountUseCase, CloseAccountUseCase {

    private final AccountRepositoryPort accountRepositoryPort;

    private final ApplicationEventPublisher eventPublisher;

    @Override
    public Account create(CreateAccountCommand command) {
        return accountRepositoryPort.save(Account.builder()
                .accountNumber(command.accountNumber())
                .balance(Money.of(command.balance(), CurrencyEnum.EUR))
                .ownerName(command.ownerName())
                .email(Email.of(command.email()))
                .type(command.type())
                .build());
    }

    @Override
    public Account findById(Long id) {
        return accountRepositoryPort.findById(id)
                .orElseThrow(() -> new AccountNotFoundException(id));
    }

    @Override
    public List<Account> findAll() {
        return accountRepositoryPort.findAll();
    }

    @Transactional
    @CacheEvict(value = "accounts", key = "#command.accountId()")
    @Override
    public Account close(CloseAccountCommand command) {
        Account account = accountRepositoryPort.findById(command.accountId())
                .orElseThrow(() -> new AccountNotFoundException(command.accountId()));

        account.close();

        Account savedAccount = accountRepositoryPort.save(account);
        eventPublisher.publishEvent(new AccountClosedEvent(savedAccount.getId(), savedAccount.getAccountNumber()));

        return savedAccount;
    }
}
