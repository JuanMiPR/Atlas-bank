package com.atlas.bank.atlas_bank.domain.service;

import com.atlas.bank.atlas_bank.domain.model.account.Account;
import com.atlas.bank.atlas_bank.domain.model.shared.Money;

import java.math.BigDecimal;

public class TransferDomainService {

    public void transfer(Account fromAccount, Account toAccount, BigDecimal amount, BigDecimal fee) {
        Money totalDebit = Money.of(amount.add(fee), fromAccount.getBalance()
                .getCurrency());
        Money deposit = Money.of(amount, toAccount.getBalance()
                .getCurrency());

        fromAccount.withdraw(totalDebit);
        toAccount.deposit(deposit);
    }
}
