package com.atlas.bank.atlas_bank.application.command;

import com.atlas.bank.atlas_bank.domain.model.account.AccountTypeEnum;
import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record CreateAccountCommand(
        String accountNumber,
        String ownerName,
        String email,
        AccountTypeEnum type,
        BigDecimal balance
) {
}
