package com.atlas.bank.atlas_bank.application.service;

import com.atlas.bank.atlas_bank.application.command.CloseAccountCommand;
import com.atlas.bank.atlas_bank.application.command.CreateAccountCommand;
import com.atlas.bank.atlas_bank.application.port.in.CloseAccountUseCase;
import com.atlas.bank.atlas_bank.application.port.in.CreateAccountUseCase;
import com.atlas.bank.atlas_bank.application.port.in.GetAccountUseCase;
import com.atlas.bank.atlas_bank.application.port.in.ListAccountUseCase;
import com.atlas.bank.atlas_bank.domain.model.account.Account;
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
        log.info("Creating account {}", command);
        Account createdAccount = createAccountUseCase.create(command);
        log.info("Created account {}", createdAccount);
        return createdAccount;
    }

    @Override
    public Account findById(Long id) {
        log.info("Finding account by id {}", id);
        Account account = getAccountUseCase.findById(id);
        log.info("Found account {}", account);
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
        log.info("Closed account {}", closedAccount);
        return closedAccount;
    }
}
