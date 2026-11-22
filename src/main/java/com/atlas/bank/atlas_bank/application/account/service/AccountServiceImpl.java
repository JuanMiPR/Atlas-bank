package com.atlas.bank.atlas_bank.application.account.service;

import com.atlas.bank.atlas_bank.application.account.command.CloseAccountCommand;
import com.atlas.bank.atlas_bank.application.account.command.CreateAccountCommand;
import com.atlas.bank.atlas_bank.application.account.port.in.CloseAccountUseCase;
import com.atlas.bank.atlas_bank.application.account.port.in.CreateAccountUseCase;
import com.atlas.bank.atlas_bank.application.account.port.in.GetAccountUseCase;
import com.atlas.bank.atlas_bank.application.account.port.in.ListAccountUseCase;
import com.atlas.bank.atlas_bank.domain.account.event.AccountClosedEvent;
import com.atlas.bank.atlas_bank.domain.account.exception.AccountNotFoundException;
import com.atlas.bank.atlas_bank.domain.account.model.Account;
import com.atlas.bank.atlas_bank.application.account.port.out.AccountRepositoryPort;
import com.atlas.bank.atlas_bank.domain.shared.enums.CurrencyEnum;
import com.atlas.bank.atlas_bank.domain.shared.model.Email;
import com.atlas.bank.atlas_bank.domain.shared.model.Money;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AccountServiceImpl implements CreateAccountUseCase, GetAccountUseCase, ListAccountUseCase, CloseAccountUseCase {

    private final AccountRepositoryPort accountRepositoryPort;

    private final ApplicationEventPublisher eventPublisher;

    @Transactional
    @Override
    public Account create(CreateAccountCommand command) {
        return accountRepositoryPort.save(Account.builder()
                .accountNumber(command.accountNumber())
                .balance(command.balance() != null
                        ? Money.of(command.balance(), CurrencyEnum.EUR)
                        : Money.zero(CurrencyEnum.EUR))
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
    @Override
    public Account close(CloseAccountCommand command) {
        Account account = accountRepositoryPort.findByIdForUpdate(command.accountId())
                .orElseThrow(() -> new AccountNotFoundException(command.accountId()));

        account.close();

        Account savedAccount = accountRepositoryPort.save(account);
        eventPublisher.publishEvent(new AccountClosedEvent(savedAccount.getId(), savedAccount.getAccountNumber()));

        return savedAccount;
    }
}
