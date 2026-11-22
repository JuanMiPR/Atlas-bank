package com.atlas.bank.atlas_bank.domain.shared.model;

import com.atlas.bank.atlas_bank.domain.shared.enums.CurrencyEnum;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Getter
@NoArgsConstructor
@EqualsAndHashCode
public class Money {

    private BigDecimal amount;
    private CurrencyEnum currency;

    private Money(BigDecimal amount, CurrencyEnum currency) {
        if (amount == null || currency == null) {
            throw new IllegalArgumentException("Amount and currency are mandatory");
        }
        this.amount = amount.setScale(2, RoundingMode.HALF_UP);
        this.currency = currency;
    }

    public static Money of(BigDecimal amount, CurrencyEnum currency) {
        return new Money(amount, currency);
    }

    public static Money zero(CurrencyEnum currency) {
        return new Money(BigDecimal.ZERO, currency);
    }

    public Money add(Money other) {
        validateSameCurrency(other);
        return Money.of(this.amount.add(other.amount), this.currency);
    }

    public Money subtract(Money other) {
        validateSameCurrency(other);
        return Money.of(this.amount.subtract(other.amount), this.currency);
    }

    public boolean isGreaterThan(Money other) {
        validateSameCurrency(other);
        return this.amount.compareTo(other.amount) > 0;
    }

    public boolean isLessThan(Money other) {
        validateSameCurrency(other);
        return this.amount.compareTo(other.amount) < 0;
    }

    public boolean isNegative() {
        return this.amount.compareTo(BigDecimal.ZERO) < 0;
    }

    private void validateSameCurrency(Money other) {
        if (this.currency != other.currency) throw new IllegalArgumentException("Currencies do not match");

    }

    @Override
    public String toString() {
        return amount.toPlainString() + " " + currency;
    }
}