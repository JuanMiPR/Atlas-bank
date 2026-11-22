package com.atlas.bank.atlas_bank.domain.model.account;

import com.atlas.bank.atlas_bank.domain.exception.AccountNotActiveException;
import com.atlas.bank.atlas_bank.domain.exception.InsufficientFoundException;
import com.atlas.bank.atlas_bank.domain.model.shared.CurrencyEnum;
import com.atlas.bank.atlas_bank.domain.model.shared.Email;
import com.atlas.bank.atlas_bank.domain.model.shared.Money;
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
            throw new IllegalStateException("Cannot close an account with non-zero balance");
        }

        this.status = AccountStatusEnum.CLOSED;
    }

    public void initDefaults() {
        if (status == null) status = AccountStatusEnum.ACTIVE;
        if (balance == null) balance = Money.zero(CurrencyEnum.EUR);
        if (createdAt == null) createdAt = LocalDateTime.now();
    }
}

