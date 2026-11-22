package com.atlas.bank.atlas_bank.application.account.port.in;

import com.atlas.bank.atlas_bank.domain.account.model.Account;

import java.util.List;

public interface ListAccountUseCase {
    List<Account> findAll();
}
