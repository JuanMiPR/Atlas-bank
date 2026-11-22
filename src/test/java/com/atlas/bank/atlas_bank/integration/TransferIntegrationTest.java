package com.atlas.bank.atlas_bank.integration;

import com.atlas.bank.atlas_bank.application.account.command.CloseAccountCommand;
import com.atlas.bank.atlas_bank.application.account.command.CreateAccountCommand;
import com.atlas.bank.atlas_bank.application.account.port.in.CloseAccountUseCase;
import com.atlas.bank.atlas_bank.application.account.port.in.CreateAccountUseCase;
import com.atlas.bank.atlas_bank.application.account.port.in.GetAccountUseCase;
import com.atlas.bank.atlas_bank.application.transaction.command.TransferMoneyCommand;
import com.atlas.bank.atlas_bank.application.transaction.port.in.GetTransactionsByAccountUseCase;
import com.atlas.bank.atlas_bank.application.transaction.port.in.TransferMoneyUseCase;
import com.atlas.bank.atlas_bank.application.transaction.query.GetAccountStatementQuery;
import com.atlas.bank.atlas_bank.application.transaction.query.TransactionReadModel;
import com.atlas.bank.atlas_bank.domain.account.enums.AccountStatusEnum;
import com.atlas.bank.atlas_bank.domain.account.enums.AccountTypeEnum;
import com.atlas.bank.atlas_bank.domain.account.model.Account;
import com.atlas.bank.atlas_bank.domain.transaction.exception.InvalidTransferException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class TransferIntegrationTest {

    @Autowired
    private CreateAccountUseCase createAccountUseCase;
    @Autowired
    private GetAccountUseCase getAccountUseCase;
    @Autowired
    private CloseAccountUseCase closeAccountUseCase;
    @Autowired
    private TransferMoneyUseCase transferMoneyUseCase;
    @Autowired
    private GetTransactionsByAccountUseCase getTransactionsByAccountUseCase;
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    @DisplayName("Transfer moves money, records an EXECUTED transaction and stores the currency as text")
    void transferEndToEnd() {
        Account from = create(AccountTypeEnum.PREMIUM, "100.00");
        Account to = create(AccountTypeEnum.PREMIUM, "0.00");

        transferMoneyUseCase.transfer(new TransferMoneyCommand(from.getId(), to.getId(), new BigDecimal("40.00")));

        assertEquals(new BigDecimal("60.00"), balanceOf(from));
        assertEquals(new BigDecimal("40.00"), balanceOf(to));

        List<TransactionReadModel> transactions = getTransactionsByAccountUseCase.getByAccountId(new GetAccountStatementQuery(from.getId()));
        assertEquals(1, transactions.size());
        assertEquals("EXECUTED", transactions.getFirst().status());

        String currency = jdbcTemplate.queryForObject("select currency from transactions where id = ?", String.class,
                transactions.getFirst().transactionId());
        assertEquals("EUR", currency);
    }

    @Test
    @DisplayName("Self transfer is rejected and the balance is unchanged")
    void selfTransferIsRejected() {
        Account account = create(AccountTypeEnum.PREMIUM, "100.00");

        assertThrows(InvalidTransferException.class,
                () -> transferMoneyUseCase.transfer(new TransferMoneyCommand(account.getId(), account.getId(), new BigDecimal("50"))));

        assertEquals(new BigDecimal("100.00"), balanceOf(account));
    }

    @Test
    @DisplayName("Concurrent transfers from the same account do not lose updates")
    void concurrentTransfersDoNotLoseUpdates() throws Exception {
        Account from = create(AccountTypeEnum.PREMIUM, "100.00");
        Account to = create(AccountTypeEnum.PREMIUM, "0.00");
        int transfers = 10;

        ExecutorService executor = Executors.newFixedThreadPool(transfers);
        CountDownLatch start = new CountDownLatch(1);
        try {
            List<Future<?>> futures = java.util.stream.IntStream.range(0, transfers)
                    .<Future<?>>mapToObj(i -> executor.submit(() -> {
                        start.await();
                        return transferMoneyUseCase.transfer(new TransferMoneyCommand(from.getId(), to.getId(), new BigDecimal("10.00")));
                    }))
                    .toList();
            start.countDown();
            for (Future<?> future : futures) future.get();
        } finally {
            executor.shutdown();
        }

        assertEquals(new BigDecimal("0.00"), balanceOf(from));
        assertEquals(new BigDecimal("100.00"), balanceOf(to));
    }

    @Test
    @DisplayName("Closing an account with zero balance marks it as CLOSED")
    void closeAccount() {
        Account account = create(AccountTypeEnum.SAVINGS, "0.00");

        closeAccountUseCase.close(new CloseAccountCommand(account.getId()));

        assertEquals(AccountStatusEnum.CLOSED, getAccountUseCase.findById(account.getId()).getStatus());
    }

    private Account create(AccountTypeEnum type, String balance) {
        return createAccountUseCase.create(new CreateAccountCommand("ACC-" + UUID.randomUUID(), "Jane Doe",
                "jane+test@example.com", type, new BigDecimal(balance)));
    }

    private BigDecimal balanceOf(Account account) {
        return getAccountUseCase.findById(account.getId()).getBalance().getAmount();
    }
}
