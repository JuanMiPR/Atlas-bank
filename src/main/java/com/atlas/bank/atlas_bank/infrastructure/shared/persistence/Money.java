package com.atlas.bank.atlas_bank.infrastructure.shared.persistence;

import com.atlas.bank.atlas_bank.domain.shared.enums.CurrencyEnum;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Getter
@Embeddable
@NoArgsConstructor
@EqualsAndHashCode
public class Money {

    @Column(nullable = false)
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 3)
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
        return Money.of(BigDecimal.ZERO, currency);
    }

    public Money add(Money other) {
        validateSameCurrency(other);
        return Money.of(amount.add(other.amount), currency);
    }

    public Money subtract(Money other) {
        validateSameCurrency(other);
        return Money.of(amount.subtract(other.amount), currency);
    }

    public boolean isLessThan(Money other) {
        validateSameCurrency(other);
        return amount.compareTo(other.amount) < 0;
    }

    public boolean isNegative() {
        return amount.compareTo(BigDecimal.ZERO) < 0;
    }

    private void validateSameCurrency(Money other) {
        if (this.currency != other.currency) throw new IllegalArgumentException("Currencies do not match");

    }

    @Override
    public String toString() {
        return amount.toPlainString() + " " + currency;
    }
}
