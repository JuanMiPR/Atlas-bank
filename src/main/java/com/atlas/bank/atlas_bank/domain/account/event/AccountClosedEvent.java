package com.atlas.bank.atlas_bank.domain.account.event;

public record AccountClosedEvent(
        Long accountId,
        String accountNumber
) {
}
