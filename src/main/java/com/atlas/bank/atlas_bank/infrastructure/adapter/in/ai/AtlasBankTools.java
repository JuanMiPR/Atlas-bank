package com.atlas.bank.atlas_bank.infrastructure.adapter.in.ai;

import com.atlas.bank.atlas_bank.application.command.CloseAccountCommand;
import com.atlas.bank.atlas_bank.application.command.TransferMoneyCommand;
import com.atlas.bank.atlas_bank.application.port.in.CloseAccountUseCase;
import com.atlas.bank.atlas_bank.application.port.in.GetAccountUseCase;
import com.atlas.bank.atlas_bank.application.port.in.TransferMoneyUseCase;
import com.atlas.bank.atlas_bank.domain.exception.AccountNotActiveException;
import com.atlas.bank.atlas_bank.domain.exception.AccountNotFoundException;
import com.atlas.bank.atlas_bank.domain.exception.InsufficientFoundException;
import com.atlas.bank.atlas_bank.domain.model.account.Account;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
@RequiredArgsConstructor
public class AtlasBankTools {

    private final TransferMoneyUseCase transferMoneyUseCase;
    private final GetAccountUseCase getAccountUseCase;
    private final CloseAccountUseCase closeAccountUseCase;

    @Tool(description = "Transfer money between two accounts in atlas-bank")
    public String transferMoney(@ToolParam(description = "Origin account ID") Long from, @ToolParam(description = "Destination account ID") Long to, @ToolParam(description = "Amount to be transferred") BigDecimal amount) {
        try {
            TransferMoneyCommand transferMoneyCommand = new TransferMoneyCommand(from, to, amount);
            transferMoneyUseCase.transfer(transferMoneyCommand);
            return "Transfer successful";
        } catch (InsufficientFoundException e) {
            return "Insufficient Funds" + e.getMessage();
        } catch (Exception e) {
            return "Transfer failed " + e.getMessage();
        }
    }

    @Tool(description = "Get account balance in atlas-bank")
    public String getAccountBalance(@ToolParam(description = "Account id") Long id) {
        try {
            Account account = getAccountUseCase.findById(id);
            return "The account balance is " + account.getBalance();
        } catch (AccountNotFoundException e) {
            return "Account not found" + e.getMessage();
        } catch (Exception e) {
            return "Get account failed " + e.getMessage();
        }
    }

    @Tool(description = "Close an account in atlas-bank")
    public String closeAccount(@ToolParam(description = "Account id") Long id) {
        try {
            closeAccountUseCase.close(new CloseAccountCommand(id));
            return "Account closed successfully";
        } catch (AccountNotFoundException e) {
            return "Account not found " + e.getMessage();
        } catch (AccountNotActiveException e) {
            return "Account not active " + e.getMessage();
        } catch (IllegalStateException e) {
            return "Account cannot be closed " + e.getMessage();
        } catch (Exception e) {
            return "Close account failed " + e.getMessage();
        }
    }

}
