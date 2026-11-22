package com.atlas.bank.atlas_bank.application.account.service;

import com.atlas.bank.atlas_bank.application.account.command.CreateAccountCommand;
import com.atlas.bank.atlas_bank.application.account.port.out.AccountRepositoryPort;
import com.atlas.bank.atlas_bank.domain.account.enums.AccountTypeEnum;
import com.atlas.bank.atlas_bank.domain.account.model.Account;
import com.atlas.bank.atlas_bank.domain.shared.enums.CurrencyEnum;
import com.atlas.bank.atlas_bank.domain.shared.model.Money;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.context.ApplicationEventPublisher;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class AccountServiceImplTest {

    private final AccountRepositoryPort accountRepository = mock(AccountRepositoryPort.class);
    private final ApplicationEventPublisher eventPublisher = mock(ApplicationEventPublisher.class);
    private final AccountServiceImpl service = new AccountServiceImpl(accountRepository, eventPublisher);

    @BeforeEach
    void setUp() {
        when(accountRepository.save(any(Account.class))).thenAnswer(invocation -> invocation.getArgument(0));
    }

    @Test
    @DisplayName("Should default the balance to zero when the request does not provide one")
    void shouldDefaultBalanceToZeroWhenNotProvided() {
        Account account = service.create(new CreateAccountCommand(
                "ACC-1", "Jane Doe", "jane@example.com", AccountTypeEnum.SAVINGS, null));

        assertEquals(Money.zero(CurrencyEnum.EUR), account.getBalance());
    }
}
