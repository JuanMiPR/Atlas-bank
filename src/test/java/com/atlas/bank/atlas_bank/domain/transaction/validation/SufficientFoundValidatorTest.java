package com.atlas.bank.atlas_bank.domain.transaction.validation;

import com.atlas.bank.atlas_bank.domain.account.enums.AccountStatusEnum;
import com.atlas.bank.atlas_bank.domain.account.enums.AccountTypeEnum;
import com.atlas.bank.atlas_bank.domain.account.exception.InsufficientFoundException;
import com.atlas.bank.atlas_bank.domain.account.model.Account;
import com.atlas.bank.atlas_bank.domain.shared.enums.CurrencyEnum;
import com.atlas.bank.atlas_bank.domain.shared.model.Email;
import com.atlas.bank.atlas_bank.domain.shared.model.Money;
import com.atlas.bank.atlas_bank.domain.transaction.model.TransfersContext;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

class SufficientFoundValidatorTest {

    private final SufficientFoundValidator validator = new SufficientFoundValidator();

    @Test
    @DisplayName("Should reject a transfer when the balance does not cover amount plus fee")
    void shouldRejectWhenBalanceDoesNotCoverAmountPlusFee() {
        TransfersContext context = contextWithBalance("100.00", "100.00");

        assertThrows(InsufficientFoundException.class,
                () -> validator.validate(context, new BigDecimal("2.00")));
    }

    @Test
    @DisplayName("Should allow a transfer when the balance covers amount plus fee")
    void shouldAllowWhenBalanceCoversAmountPlusFee() {
        TransfersContext context = contextWithBalance("102.00", "100.00");

        assertDoesNotThrow(() -> validator.validate(context, new BigDecimal("2.00")));
    }

    private TransfersContext contextWithBalance(String balance, String amount) {
        return new TransfersContext(accountWithBalance(balance), accountWithBalance("0.00"),
                new BigDecimal(amount), "test");
    }

    private Account accountWithBalance(String amount) {
        return Account.builder()
                .id(1L)
                .accountNumber("ACC-1")
                .ownerName("Jane Doe")
                .email(Email.of("jane@example.com"))
                .type(AccountTypeEnum.SAVINGS)
                .balance(Money.of(new BigDecimal(amount), CurrencyEnum.EUR))
                .status(AccountStatusEnum.ACTIVE)
                .build();
    }
}
