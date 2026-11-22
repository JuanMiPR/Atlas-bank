package com.atlas.bank.atlas_bank.infrastructure.transaction.adapter.in.event;

import com.atlas.bank.atlas_bank.domain.transaction.event.TransactionExecutedEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class NotificationListener {

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT, fallbackExecution = true)
    public void onTransactionExecuted(TransactionExecutedEvent event) {
        log.info(event.toString());
    }
}
