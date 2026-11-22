package com.atlas.bank.atlas_bank.infrastructure.adapter.in.rest.dto;

import com.atlas.bank.atlas_bank.domain.model.transaction.TransactionStatusEnum;
import com.atlas.bank.atlas_bank.domain.model.transaction.TransactionTypeEnum;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class TransactionResponse {

    private Long id;

    private TransactionTypeEnum type;

    private Long sourceAccountId;

    private Long targetAccountId;

    private BigDecimal amount;

    private BigDecimal fee;

    private TransactionStatusEnum status;

    private LocalDateTime createdAt;

}
