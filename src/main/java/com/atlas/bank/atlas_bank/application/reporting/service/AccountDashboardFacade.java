package com.atlas.bank.atlas_bank.application.reporting.service;

import com.atlas.bank.atlas_bank.application.account.port.in.GetAccountUseCase;
import com.atlas.bank.atlas_bank.application.transaction.port.in.GetTransactionsByAccountUseCase;
import com.atlas.bank.atlas_bank.application.reporting.query.DashboardReadModel;
import com.atlas.bank.atlas_bank.application.transaction.query.GetAccountStatementQuery;
import com.atlas.bank.atlas_bank.application.transaction.query.TransactionReadModel;
import com.atlas.bank.atlas_bank.domain.account.model.Account;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AccountDashboardFacade {
    private final GetAccountUseCase getAccountUseCase;
    private final GetTransactionsByAccountUseCase getTransactionsByAccountUseCase;

    public DashboardReadModel getDashboard(Long accountId) {
        Account account = getAccountUseCase.findById(accountId);
        List<TransactionReadModel> transactionResponse = getTransactionsByAccountUseCase.getByAccountId(new GetAccountStatementQuery(accountId));


        return DashboardReadModel.builder()
                .accountId(accountId)
                .balance(account.getBalance()
                        .getAmount())
                .accountNumber(account.getAccountNumber())
                .ownerName(account.getOwnerName())
                .type(account.getType()
                        .name())
                .status(account.getStatus()
                        .name())
                .transactions(transactionResponse)
                .build();
    }
}
