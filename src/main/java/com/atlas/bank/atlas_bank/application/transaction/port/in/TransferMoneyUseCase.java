package com.atlas.bank.atlas_bank.application.transaction.port.in;

import com.atlas.bank.atlas_bank.application.transaction.command.TransferMoneyCommand;
import com.atlas.bank.atlas_bank.domain.transaction.model.Transaction;

public interface TransferMoneyUseCase {
    Transaction transfer(TransferMoneyCommand command);
}
