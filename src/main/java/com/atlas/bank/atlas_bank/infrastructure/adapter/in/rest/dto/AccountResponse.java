package com.atlas.bank.atlas_bank.infrastructure.adapter.in.rest.dto;

import com.atlas.bank.atlas_bank.domain.model.account.AccountStatusEnum;
import com.atlas.bank.atlas_bank.domain.model.account.AccountTypeEnum;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class AccountResponse {
    private Long id;

    private String accountNumber;

    private String ownerName;

    private String email;

    private AccountTypeEnum type;

    private BigDecimal balance;

    private AccountStatusEnum status;

    private LocalDateTime createdAt;
}
