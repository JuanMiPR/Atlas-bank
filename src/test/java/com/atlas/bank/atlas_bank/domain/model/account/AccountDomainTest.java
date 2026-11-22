package com.atlas.bank.atlas_bank.domain.model.account;

import com.atlas.bank.atlas_bank.domain.exception.AccountNotActiveException;
import com.atlas.bank.atlas_bank.domain.exception.InsufficientFoundException;
import com.atlas.bank.atlas_bank.domain.model.shared.CurrencyEnum;
import com.atlas.bank.atlas_bank.domain.model.shared.Email;
import com.atlas.bank.atlas_bank.domain.model.shared.Money;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class AccountDomainTest {

    private Account createAccountWithBalance(BigDecimal balance) {
        return Account.builder()
                .id(1L)
                .accountNumber("123")
                .ownerName("Test Owner")
                .email(Email.of("test@test.com"))
                .type(AccountTypeEnum.SAVINGS)
                .balance(Money.of(balance, CurrencyEnum.EUR))
                .status(AccountStatusEnum.ACTIVE)
                .build();
    }


    @Test
    @DisplayName("Should deposit money correctly")
    void shouldDepositMoney() {

        Account account = createAccountWithBalance(new BigDecimal(1000));

        Money deposit = Money.of(new BigDecimal(500), CurrencyEnum.EUR);

        account.deposit(deposit);

        assertEquals(Money.of(new BigDecimal(1500), CurrencyEnum.EUR), account.getBalance());
    }

    @Test
    @DisplayName("Should withdraw money correctly")
    void shouldWithdrawMoney() {

        Account account = createAccountWithBalance(new BigDecimal(1000));

        Money withdraw = Money.of(new BigDecimal(500), CurrencyEnum.EUR);

        account.withdraw(withdraw);

        assertEquals(Money.of(new BigDecimal(500), CurrencyEnum.EUR), account.getBalance());
    }

    @Test
    @DisplayName("Withdraw without founds")
    void shouldRejectWithdrawWithoutFounds() {

        Account account = createAccountWithBalance(new BigDecimal(1000));

        Money withdraw = Money.of(new BigDecimal(1001), CurrencyEnum.EUR);

        assertThrows(InsufficientFoundException.class, () -> account.withdraw(withdraw));

    }

    @Test
    @DisplayName("Should reject deposit with negative amount")
    void shouldRejectDepositWithNegativeAmount() {

        Account account = createAccountWithBalance(new BigDecimal(1000));

        Money deposit = Money.of(new BigDecimal(-500), CurrencyEnum.EUR);

        assertThrows(IllegalArgumentException.class, () -> account.deposit(deposit));

    }

    @Test
    @DisplayName("Should reject withdraw with negative amount")
    void shouldRejectWithdrawWithNegativeAmount() {

        Account account = createAccountWithBalance(new BigDecimal(1000));

        Money withdraw = Money.of(new BigDecimal(-500), CurrencyEnum.EUR);

        assertThrows(IllegalArgumentException.class, () -> account.withdraw(withdraw));

    }

    @Test
    @DisplayName("Should close an active account with zero balance")
    void shouldCloseActiveAccountWithZeroBalance() {

        Account account = createAccountWithBalance(BigDecimal.ZERO);

        account.close();

        assertEquals(AccountStatusEnum.CLOSED, account.getStatus());
    }

    @Test
    @DisplayName("Should reject closing a non-active account")
    void shouldRejectClosingNonActiveAccount() {

        Account account = createAccountWithBalance(BigDecimal.ZERO);
        account.setStatus(AccountStatusEnum.FROZEN);

        assertThrows(AccountNotActiveException.class, account::close);
    }

    @Test
    @DisplayName("Should reject closing an account with balance")
    void shouldRejectClosingAccountWithBalance() {

        Account account = createAccountWithBalance(new BigDecimal(100));

        IllegalStateException exception = assertThrows(IllegalStateException.class, account::close);

        assertEquals("Cannot close an account with non-zero balance", exception.getMessage());
    }

}