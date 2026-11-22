package com.atlas.bank.atlas_bank.infrastructure.account.adapter.in.rest.dto;

import com.atlas.bank.atlas_bank.domain.account.enums.AccountStatusEnum;
import com.atlas.bank.atlas_bank.domain.account.enums.AccountTypeEnum;
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
