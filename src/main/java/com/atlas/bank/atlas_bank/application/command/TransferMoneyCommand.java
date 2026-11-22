package com.atlas.bank.atlas_bank.application.command;

import java.math.BigDecimal;

public record TransferMoneyCommand(
        Long fromId,
        Long toId,
        BigDecimal amount) {
}
