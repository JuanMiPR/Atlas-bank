package com.atlas.bank.atlas_bank.application.account.command;

import com.atlas.bank.atlas_bank.domain.account.enums.AccountTypeEnum;
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
