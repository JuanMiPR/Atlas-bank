package com.atlas.bank.atlas_bank.domain.transaction.service;

import com.atlas.bank.atlas_bank.domain.account.model.Account;
import com.atlas.bank.atlas_bank.domain.shared.enums.CurrencyEnum;
import com.atlas.bank.atlas_bank.domain.shared.model.Money;
import com.atlas.bank.atlas_bank.domain.transaction.exception.InvalidTransferException;

import java.math.BigDecimal;
import java.util.Objects;

public class TransferDomainService {

    public void transfer(Account fromAccount, Account toAccount, BigDecimal amount, BigDecimal fee) {
        if (fromAccount == toAccount || Objects.equals(fromAccount.getId(), toAccount.getId()))
            throw new InvalidTransferException("The origin account and destination account must be different");

        CurrencyEnum currency = fromAccount.getBalance().getCurrency();
        if (currency != toAccount.getBalance().getCurrency())
            throw new InvalidTransferException("Transfers between accounts with different currencies are not supported");

        fromAccount.withdraw(Money.of(amount.add(fee), currency));
        toAccount.deposit(Money.of(amount, currency));
    }
}
