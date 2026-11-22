package com.atlas.bank.atlas_bank.infrastructure.adapter.in.rest.dto;

import com.atlas.bank.atlas_bank.domain.model.account.AccountTypeEnum;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class CreateAccountRequest {
    @NotBlank(message = "The account number is required")
    private String accountNumber;

    @NotBlank(message = "The owner name is required")
    private String ownerName;

    @NotBlank(message = "The email is required")
    @Email(message = "The email has invalid format")
    private String email;

    @NotNull(message = "The account type is required")
    private AccountTypeEnum type;

    @PositiveOrZero(message = "The balance must be positive")
    private BigDecimal balance;
}
