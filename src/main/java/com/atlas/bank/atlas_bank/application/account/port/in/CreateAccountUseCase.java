package com.atlas.bank.atlas_bank.application.account.port.in;

import com.atlas.bank.atlas_bank.application.account.command.CreateAccountCommand;
import com.atlas.bank.atlas_bank.domain.account.model.Account;

public interface CreateAccountUseCase {
    Account create(CreateAccountCommand command);
}
