package com.atlas.bank.atlas_bank.domain.model.transaction;

import com.atlas.bank.atlas_bank.domain.model.shared.Money;
import com.atlas.bank.atlas_bank.domain.model.transaction.state.*;
import com.atlas.bank.atlas_bank.domain.event.TransactionExecutedEvent;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Builder
public class Transaction {

    @EqualsAndHashCode.Include
    private Long id;

    private TransactionTypeEnum type;

    private Long sourceAccountId;

    private Long targetAccountId;

    private Money amount;

    private BigDecimal fee;

    private TransactionStatusEnum status;

    private LocalDateTime createdAt;

    private String description;

    private TransactionState state;

    @Builder.Default
    private List<Object> domainEvents = new ArrayList<>();

    public void initDefaults() {
        if (this.createdAt == null) this.createdAt = LocalDateTime.now();
        if (this.status == null) this.status = TransactionStatusEnum.EXECUTED;
    }

    public void clearDomainEvents() {
        this.domainEvents.clear();
    }

    public List<Object> getDomainEvents() {
        return Collections.unmodifiableList(this.domainEvents);
    }

    public TransactionState getState() {
        if (state == null) {
            state = switch (status) {
                case PENDING -> new PendingState();
                case VALIDATED -> new ValidatedState();
                case EXECUTED -> new ExecutedState();
                case REJECTED -> new RejectedState();
                case REVERSED -> new ReversedState();
            };
        }
        return state;
    }

    public void advanceTo(TransactionState newState) {
        this.state = newState;
        this.status = newState.status();
    }

    public void markAsExecuted() {
        domainEvents.add(new TransactionExecutedEvent(id, type, sourceAccountId, targetAccountId, amount.getAmount(), fee));
    }

    public void executeTransfer() {
        advanceTo(getState().validate());
        advanceTo(getState().execute());
        markAsExecuted();
    }
}