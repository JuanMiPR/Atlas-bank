package com.atlas.bank.atlas_bank.infrastructure.adapter.in.rest;

import com.atlas.bank.atlas_bank.application.command.TransferMoneyCommand;
import com.atlas.bank.atlas_bank.application.port.in.GetTransactionsByAccountUseCase;
import com.atlas.bank.atlas_bank.application.port.in.TransferMoneyUseCase;
import com.atlas.bank.atlas_bank.application.query.GetAccountStatementQuery;
import com.atlas.bank.atlas_bank.application.query.TransactionReadModel;
import com.atlas.bank.atlas_bank.infrastructure.adapter.in.rest.dto.TransactionMapper;
import com.atlas.bank.atlas_bank.infrastructure.adapter.in.rest.dto.TransactionResponse;
import com.atlas.bank.atlas_bank.infrastructure.adapter.in.rest.dto.TransferRequest;
import com.atlas.bank.atlas_bank.domain.model.transaction.Transaction;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/transactions")
public class TransactionController {
    private final TransferMoneyUseCase transferMoneyUseCase;

    private final GetTransactionsByAccountUseCase getTransactionsByAccountUseCase;

    private final TransactionMapper transactionMapper;

    @PostMapping("/transfer")
    public ResponseEntity<TransactionResponse> transferMoney(@Valid @RequestBody TransferRequest transferRequest) {
        TransferMoneyCommand command = new TransferMoneyCommand(transferRequest.getFromAccountId(), transferRequest.getToAccountId(), transferRequest.getAmount());
        Transaction transaction = transferMoneyUseCase.transfer(command);
        return ResponseEntity.ok(transactionMapper.toTransactionResponse(transaction));
    }

    @GetMapping("/{id}/transactions")
    public ResponseEntity<List<TransactionReadModel>> getTransactions(@PathVariable Long id) {
        return ResponseEntity.ok(getTransactionsByAccountUseCase.getByAccountId(new GetAccountStatementQuery(id)));
    }
}
