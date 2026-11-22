package com.atlas.bank.atlas_bank.infrastructure.adapter.in.rest.dto;

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
    private BigDecimal amount;
}
