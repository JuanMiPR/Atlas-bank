package com.atlas.bank.atlas_bank.infrastructure.transaction.adapter.in.rest.dto;

import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.math.BigDecimal;

@Data
@DifferentAccounts
public class TransferRequest {
    @NotNull(message = "The fromId can´t be null")
    private Long fromAccountId;

    @NotNull(message = "The toId can´t be null")
    private Long toAccountId;

    @NotNull(message = "The amount can´t be null")
    @Positive(message = "The amount must be positive")
    @Digits(integer = 17, fraction = 2, message = "The amount cannot have more than 2 decimals")
    private BigDecimal amount;
}
