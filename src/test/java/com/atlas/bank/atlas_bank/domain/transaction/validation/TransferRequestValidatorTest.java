package com.atlas.bank.atlas_bank.domain.transaction.validation;

import com.atlas.bank.atlas_bank.domain.account.enums.AccountStatusEnum;
import com.atlas.bank.atlas_bank.domain.account.enums.AccountTypeEnum;
import com.atlas.bank.atlas_bank.domain.account.model.Account;
import com.atlas.bank.atlas_bank.domain.shared.enums.CurrencyEnum;
import com.atlas.bank.atlas_bank.domain.shared.model.Email;
import com.atlas.bank.atlas_bank.domain.shared.model.Money;
import com.atlas.bank.atlas_bank.domain.transaction.exception.InvalidTransferException;
import com.atlas.bank.atlas_bank.domain.transaction.model.TransfersContext;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

class TransferRequestValidatorTest {

    private final TransferRequestValidator validator = new TransferRequestValidator();

    @Test
    @DisplayName("Should reject a transfer to the same account")
    void shouldRejectSameAccount() {
        Account account = account(1L, CurrencyEnum.EUR);
        assertThrows(InvalidTransferException.class,
                () -> validator.validate(new TransfersContext(account, account(1L, CurrencyEnum.EUR), new BigDecimal("10"), "t"), BigDecimal.ZERO));
    }

    @Test
    @DisplayName("Should reject zero and negative amounts")
    void shouldRejectNonPositiveAmount() {
        assertThrows(InvalidTransferException.class, () -> validator.validate(context("0"), BigDecimal.ZERO));
        assertThrows(InvalidTransferException.class, () -> validator.validate(context("-5"), BigDecimal.ZERO));
        assertThrows(InvalidTransferException.class, () -> validator.validate(context(null), BigDecimal.ZERO));
    }

    @Test
    @DisplayName("Should reject amounts with more than two decimals")
    void shouldRejectTooManyDecimals() {
        assertThrows(InvalidTransferException.class, () -> validator.validate(context("10.001"), BigDecimal.ZERO));
        assertDoesNotThrow(() -> validator.validate(context("10.100"), BigDecimal.ZERO));
    }

    @Test
    @DisplayName("Should reject transfers between different currencies")
    void shouldRejectDifferentCurrencies() {
        TransfersContext context = new TransfersContext(account(1L, CurrencyEnum.EUR), account(2L, CurrencyEnum.USD),
                new BigDecimal("10"), "t");
        assertThrows(InvalidTransferException.class, () -> validator.validate(context, BigDecimal.ZERO));
    }

    @Test
    @DisplayName("Should accept a valid transfer")
    void shouldAcceptValidTransfer() {
        assertDoesNotThrow(() -> validator.validate(context("10.50"), BigDecimal.ZERO));
    }

    private TransfersContext context(String amount) {
        return new TransfersContext(account(1L, CurrencyEnum.EUR), account(2L, CurrencyEnum.EUR),
                amount == null ? null : new BigDecimal(amount), "t");
    }

    private Account account(Long id, CurrencyEnum currency) {
        return Account.builder()
                .id(id)
                .accountNumber("ACC-" + id)
                .ownerName("Jane Doe")
                .email(Email.of("jane@example.com"))
                .type(AccountTypeEnum.SAVINGS)
                .balance(Money.of(new BigDecimal("100"), currency))
                .status(AccountStatusEnum.ACTIVE)
                .build();
    }
}
