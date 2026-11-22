package com.atlas.bank.atlas_bank.infrastructure.ai.adapter.in;

import com.atlas.bank.atlas_bank.application.account.command.CloseAccountCommand;
import com.atlas.bank.atlas_bank.application.account.port.in.CloseAccountUseCase;
import com.atlas.bank.atlas_bank.application.account.port.in.GetAccountUseCase;
import com.atlas.bank.atlas_bank.application.transaction.command.TransferMoneyCommand;
import com.atlas.bank.atlas_bank.application.transaction.port.in.TransferMoneyUseCase;
import com.atlas.bank.atlas_bank.domain.account.exception.AccountNotActiveException;
import com.atlas.bank.atlas_bank.domain.account.exception.AccountNotFoundException;
import com.atlas.bank.atlas_bank.domain.account.exception.InsufficientFoundException;
import com.atlas.bank.atlas_bank.domain.account.model.Account;
import com.atlas.bank.atlas_bank.infrastructure.account.adapter.in.security.AccountAccessGuard;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

/**
 * Tools exposed to the LLM. They bypass the REST layer, so every tool applies the same
 * authorization as the equivalent endpoint through {@link AccountAccessGuard}; business
 * invariants (different accounts, positive amount...) are enforced by the domain validators.
 */
@Component
@RequiredArgsConstructor
public class AtlasBankTools {

    private final TransferMoneyUseCase transferMoneyUseCase;
    private final GetAccountUseCase getAccountUseCase;
    private final CloseAccountUseCase closeAccountUseCase;
    private final AccountAccessGuard accountAccessGuard;

    @Tool(description = "Transfer money between two accounts in atlas-bank")
    public String transferMoney(@ToolParam(description = "Origin account ID") Long from, @ToolParam(description = "Destination account ID") Long to, @ToolParam(description = "Amount to be transferred") BigDecimal amount) {
        try {
            accountAccessGuard.checkAccess(from);
            transferMoneyUseCase.transfer(new TransferMoneyCommand(from, to, amount));
            return "Transfer successful";
        } catch (AccessDeniedException e) {
            return "Not authorized to operate on account " + from;
        } catch (InsufficientFoundException e) {
            return "Insufficient funds: " + e.getMessage();
        } catch (Exception e) {
            return "Transfer failed: " + e.getMessage();
        }
    }

    @Tool(description = "Get account balance in atlas-bank")
    public String getAccountBalance(@ToolParam(description = "Account id") Long id) {
        try {
            accountAccessGuard.checkAccess(id);
            Account account = getAccountUseCase.findById(id);
            return "The account balance is " + account.getBalance();
        } catch (AccessDeniedException e) {
            return "Not authorized to access account " + id;
        } catch (AccountNotFoundException e) {
            return "Account not found: " + e.getMessage();
        } catch (Exception e) {
            return "Get account failed: " + e.getMessage();
        }
    }

    @Tool(description = "Close an account in atlas-bank")
    public String closeAccount(@ToolParam(description = "Account id") Long id) {
        try {
            accountAccessGuard.requireAdmin();
            closeAccountUseCase.close(new CloseAccountCommand(id));
            return "Account closed successfully";
        } catch (AccessDeniedException e) {
            return "Only administrators can close accounts";
        } catch (AccountNotFoundException e) {
            return "Account not found: " + e.getMessage();
        } catch (AccountNotActiveException e) {
            return "Account not active: " + e.getMessage();
        } catch (IllegalStateException e) {
            return "Account cannot be closed: " + e.getMessage();
        } catch (Exception e) {
            return "Close account failed: " + e.getMessage();
        }
    }

}
