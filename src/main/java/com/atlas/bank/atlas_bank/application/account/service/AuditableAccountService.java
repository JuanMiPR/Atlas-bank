package com.atlas.bank.atlas_bank.application.account.service;

import com.atlas.bank.atlas_bank.application.account.command.CloseAccountCommand;
import com.atlas.bank.atlas_bank.application.account.command.CreateAccountCommand;
import com.atlas.bank.atlas_bank.application.account.port.in.CloseAccountUseCase;
import com.atlas.bank.atlas_bank.application.account.port.in.CreateAccountUseCase;
import com.atlas.bank.atlas_bank.application.account.port.in.GetAccountUseCase;
import com.atlas.bank.atlas_bank.application.account.port.in.ListAccountUseCase;
import com.atlas.bank.atlas_bank.domain.account.model.Account;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@Primary
public class AuditableAccountService implements CreateAccountUseCase, ListAccountUseCase, GetAccountUseCase, CloseAccountUseCase {

    private final CreateAccountUseCase createAccountUseCase;
    private final ListAccountUseCase listAccountUseCase;
    private final GetAccountUseCase getAccountUseCase;
    private final CloseAccountUseCase closeAccountUseCase;

    public AuditableAccountService(@Qualifier("accountServiceImpl") CreateAccountUseCase createAccountUseCase,
                                   @Qualifier("accountServiceImpl") ListAccountUseCase listAccountUseCase,
                                   @Qualifier("accountServiceImpl") GetAccountUseCase getAccountUseCase,
                                   @Qualifier("accountServiceImpl") CloseAccountUseCase closeAccountUseCase) {
        this.createAccountUseCase = createAccountUseCase;
        this.listAccountUseCase = listAccountUseCase;
        this.getAccountUseCase = getAccountUseCase;
        this.closeAccountUseCase = closeAccountUseCase;
    }


    @Override
    public Account create(CreateAccountCommand command) {
        log.info("Creating account number={} type={}", command.accountNumber(), command.type());
        Account createdAccount = createAccountUseCase.create(command);
        log.info("Created account id={}", createdAccount.getId());
        return createdAccount;
    }

    @Override
    public Account findById(Long id) {
        log.info("Finding account by id {}", id);
        Account account = getAccountUseCase.findById(id);
        log.info("Found account id={}", account.getId());
        return account;
    }

    @Override
    public List<Account> findAll() {
        log.info("Finding all accounts");
        List<Account> accounts = listAccountUseCase.findAll();
        log.info("Found {} accounts", accounts.size());
        return accounts;
    }

    @Override
    public Account close(CloseAccountCommand command) {
        log.info("Closing account {}", command.accountId());
        Account closedAccount = closeAccountUseCase.close(command);
        log.info("Closed account id={}", closedAccount.getId());
        return closedAccount;
    }
}
