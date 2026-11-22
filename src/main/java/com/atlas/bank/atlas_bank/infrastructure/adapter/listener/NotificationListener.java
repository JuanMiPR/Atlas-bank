package com.atlas.bank.atlas_bank.infrastructure.adapter.listener;

import com.atlas.bank.atlas_bank.domain.event.TransactionExecutedEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class NotificationListener {

    @EventListener(TransactionExecutedEvent.class)
    public void onTransactionExecuted(TransactionExecutedEvent event) {
        log.info(event.toString());
    }
}
