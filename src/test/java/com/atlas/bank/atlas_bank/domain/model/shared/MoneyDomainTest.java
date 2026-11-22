package com.atlas.bank.atlas_bank.domain.model.shared;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class MoneyDomainTest {

    @Test
    @DisplayName("Should add two moneys with same currency")
    void shouldAddTwoMoneysWithSameCurrency() {
        Money a = Money.zero(CurrencyEnum.EUR);
        Money b = Money.of(new BigDecimal(200), CurrencyEnum.EUR);

        Money result = a.add(b);

        assertEquals(Money.of(new BigDecimal(200), CurrencyEnum.EUR), result);
    }

    @Test
    @DisplayName("Should reject two moneys with different currency")
    void shouldRejectTwoMoneysWithDifferentCurrency() {
        Money a = Money.zero(CurrencyEnum.EUR);
        Money b = Money.of(new BigDecimal(200), CurrencyEnum.USD);

        assertThrows(IllegalArgumentException.class, () -> a.add(b));

    }

    @Test
    @DisplayName("Should detect negative amount")
    void shouldDetectNegativeAmount() {
        Money a = Money.of(new BigDecimal(-500), CurrencyEnum.EUR);
        assertTrue(a.isNegative());
    }

    @Test
    @DisplayName("Should compare different moneys")
    void shouldCompareDifferentMoneys() {
        Money a = Money.zero(CurrencyEnum.EUR);
        Money b = Money.of(new BigDecimal(200), CurrencyEnum.EUR);

        assertTrue(a.isLessThan(b));
        assertFalse(a.isGreaterThan(b));
    }

    @Test
    @DisplayName("Money should be zero")
    void moneyShouldBeZero() {
        Money a = Money.zero(CurrencyEnum.EUR);
        assertEquals(new BigDecimal("0.00"), a.getAmount());
    }
}