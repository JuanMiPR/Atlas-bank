package com.atlas.bank.atlas_bank.application.service;

import com.atlas.bank.atlas_bank.application.port.in.GetAccountUseCase;
import com.atlas.bank.atlas_bank.application.port.in.GetTransactionsByAccountUseCase;
import com.atlas.bank.atlas_bank.application.query.DashboardReadModel;
import com.atlas.bank.atlas_bank.application.query.GetAccountStatementQuery;
import com.atlas.bank.atlas_bank.application.query.TransactionReadModel;
import com.atlas.bank.atlas_bank.domain.model.account.Account;
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
