package com.atlas.bank.atlas_bank.domain.transaction.model;

import com.atlas.bank.atlas_bank.domain.account.model.Account;

import java.math.BigDecimal;

public record TransfersContext(Account from, Account to, BigDecimal amount, String description)  {
}
