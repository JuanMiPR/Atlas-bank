package com.atlas.bank.atlas_bank.domain.account.model;

import com.atlas.bank.atlas_bank.domain.account.enums.AccountStatusEnum;
import com.atlas.bank.atlas_bank.domain.account.enums.AccountTypeEnum;
import com.atlas.bank.atlas_bank.domain.account.exception.AccountBalanceNotZeroException;
import com.atlas.bank.atlas_bank.domain.account.exception.AccountNotActiveException;
import com.atlas.bank.atlas_bank.domain.account.exception.InsufficientFoundException;
import com.atlas.bank.atlas_bank.domain.shared.enums.CurrencyEnum;
import com.atlas.bank.atlas_bank.domain.shared.model.Email;
import com.atlas.bank.atlas_bank.domain.shared.model.Money;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString
@Builder
public class Account {
    @EqualsAndHashCode.Include
    private Long id;

    private String accountNumber;

    private String ownerName;

    private Email email;

    private AccountTypeEnum type;

    private Money balance;

    private AccountStatusEnum status;

    private LocalDateTime createdAt;

    private Long customerId;

    public void deposit(Money amount) {
        if (amount.isNegative()) throw new IllegalArgumentException("Deposit amount cannot be negative");
        this.balance = this.balance.add(amount);
    }

    public void withdraw(Money amount) {
        if (amount.isNegative()) throw new IllegalArgumentException("Withdraw amount cannot be negative");

        if (balance.isLessThan(amount))
            throw new InsufficientFoundException(id, balance.getAmount(), amount.getAmount());

        this.balance = this.balance.subtract(amount);
    }

    public void close() {
        if (status != AccountStatusEnum.ACTIVE) {
            throw new AccountNotActiveException(id, status);
        }

        if (balance == null || !balance.equals(Money.zero(balance.getCurrency()))) {
            throw new AccountBalanceNotZeroException();
        }

        this.status = AccountStatusEnum.CLOSED;
    }

    public void initDefaults() {
        if (status == null) status = AccountStatusEnum.ACTIVE;
        if (balance == null) balance = Money.zero(CurrencyEnum.EUR);
        if (createdAt == null) createdAt = LocalDateTime.now();
    }
}

