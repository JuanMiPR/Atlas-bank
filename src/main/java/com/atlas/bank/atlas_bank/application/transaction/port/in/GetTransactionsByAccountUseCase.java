package com.atlas.bank.atlas_bank.application.transaction.port.in;

import com.atlas.bank.atlas_bank.application.transaction.query.GetAccountStatementQuery;
import com.atlas.bank.atlas_bank.application.transaction.query.TransactionReadModel;

import java.util.List;

public interface GetTransactionsByAccountUseCase {
    List<TransactionReadModel> getByAccountId(GetAccountStatementQuery query);
}
