package com.atlas.bank.atlas_bank.application.transaction.service;

import com.atlas.bank.atlas_bank.application.account.port.out.AccountRepositoryPort;
import com.atlas.bank.atlas_bank.application.transaction.command.TransferMoneyCommand;
import com.atlas.bank.atlas_bank.application.transaction.port.out.TransactionRepositoryPort;
import com.atlas.bank.atlas_bank.domain.account.enums.AccountStatusEnum;
import com.atlas.bank.atlas_bank.domain.account.enums.AccountTypeEnum;
import com.atlas.bank.atlas_bank.domain.account.model.Account;
import com.atlas.bank.atlas_bank.domain.shared.enums.CurrencyEnum;
import com.atlas.bank.atlas_bank.domain.shared.model.Email;
import com.atlas.bank.atlas_bank.domain.shared.model.Money;
import com.atlas.bank.atlas_bank.domain.transaction.exception.InvalidTransferException;
import com.atlas.bank.atlas_bank.domain.transaction.fee.CheckingFeeCalculator;
import com.atlas.bank.atlas_bank.domain.transaction.fee.DefaultFeeCalculator;
import com.atlas.bank.atlas_bank.domain.transaction.model.Transaction;
import com.atlas.bank.atlas_bank.domain.transaction.service.TransferDomainService;
import com.atlas.bank.atlas_bank.domain.transaction.validation.SufficientFoundValidator;
import com.atlas.bank.atlas_bank.domain.transaction.validation.TransferRequestValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InOrder;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class TransferServiceImplTest {

    private final AccountRepositoryPort accountRepository = mock(AccountRepositoryPort.class);
    private final TransactionRepositoryPort transactionRepository = mock(TransactionRepositoryPort.class);

    private final TransferServiceImpl service = new TransferServiceImpl(accountRepository,
            List.of(new CheckingFeeCalculator(), new DefaultFeeCalculator()),
            new TransferDomainService(),
            transactionRepository,
            List.of(new TransferRequestValidator(), new SufficientFoundValidator()));

    @BeforeEach
    void setUp() {
        when(accountRepository.save(any(Account.class))).thenAnswer(i -> i.getArgument(0));
        when(transactionRepository.save(any(Transaction.class))).thenAnswer(i -> i.getArgument(0));
    }

    @Test
    @DisplayName("Should reject a transfer to the same account without touching its balance")
    void shouldRejectSelfTransfer() {
        Account account = account(1L, "100.00");
        when(accountRepository.findByIdForUpdate(1L)).thenReturn(Optional.of(account));

        assertThrows(InvalidTransferException.class,
                () -> service.transfer(new TransferMoneyCommand(1L, 1L, new BigDecimal("50"))));

        assertEquals(Money.of(new BigDecimal("100.00"), CurrencyEnum.EUR), account.getBalance());
        verify(accountRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should round the fee to two decimals and charge exactly amount plus fee")
    void shouldRoundFee() {
        Account from = account(1L, "100.00");
        Account to = account(2L, "0.00");
        when(accountRepository.findByIdForUpdate(1L)).thenReturn(Optional.of(from));
        when(accountRepository.findByIdForUpdate(2L)).thenReturn(Optional.of(to));

        // 10.01 * 0.015 = 0.15015 -> 0.15
        Transaction transaction = service.transfer(new TransferMoneyCommand(1L, 2L, new BigDecimal("10.01")));

        assertEquals(new BigDecimal("0.15"), transaction.getFee());
        assertEquals(Money.of(new BigDecimal("89.84"), CurrencyEnum.EUR), from.getBalance());
        assertEquals(Money.of(new BigDecimal("10.01"), CurrencyEnum.EUR), to.getBalance());
    }

    @Test
    @DisplayName("Should lock accounts in ascending id order to avoid deadlocks")
    void shouldLockInIdOrder() {
        when(accountRepository.findByIdForUpdate(1L)).thenReturn(Optional.of(account(1L, "0.00")));
        when(accountRepository.findByIdForUpdate(2L)).thenReturn(Optional.of(account(2L, "100.00")));

        service.transfer(new TransferMoneyCommand(2L, 1L, new BigDecimal("10")));

        InOrder inOrder = inOrder(accountRepository);
        inOrder.verify(accountRepository).findByIdForUpdate(1L);
        inOrder.verify(accountRepository).findByIdForUpdate(2L);
    }

    @Test
    @DisplayName("Should reject a null amount coming from non-REST adapters")
    void shouldRejectNullAmount() {
        when(accountRepository.findByIdForUpdate(1L)).thenReturn(Optional.of(account(1L, "100.00")));
        when(accountRepository.findByIdForUpdate(2L)).thenReturn(Optional.of(account(2L, "0.00")));

        assertThrows(InvalidTransferException.class,
                () -> service.transfer(new TransferMoneyCommand(1L, 2L, null)));
    }

    private Account account(Long id, String balance) {
        return Account.builder()
                .id(id)
                .accountNumber("ACC-" + id)
                .ownerName("Jane Doe")
                .email(Email.of("jane@example.com"))
                .type(AccountTypeEnum.CHECKING)
                .balance(Money.of(new BigDecimal(balance), CurrencyEnum.EUR))
                .status(AccountStatusEnum.ACTIVE)
                .build();
    }
}
