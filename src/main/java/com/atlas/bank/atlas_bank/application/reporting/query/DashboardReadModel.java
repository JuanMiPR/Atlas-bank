package com.atlas.bank.atlas_bank.application.reporting.query;

import com.atlas.bank.atlas_bank.application.transaction.query.TransactionReadModel;
import lombok.Builder;

import java.math.BigDecimal;
import java.util.List;

@Builder
public record DashboardReadModel(
        Long accountId,
        String accountNumber,
        String ownerName,
        String type,
        BigDecimal balance,
        String status,
        List<TransactionReadModel> transactions) {
}
