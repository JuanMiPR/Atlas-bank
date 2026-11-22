package com.atlas.bank.atlas_bank.domain.transaction.validation;

import com.atlas.bank.atlas_bank.domain.transaction.exception.InvalidTransferException;
import com.atlas.bank.atlas_bank.domain.transaction.model.TransfersContext;

import java.math.BigDecimal;
import java.util.Objects;

/**
 * Basic invariants of a transfer, enforced in the domain so that every entry point
 * (REST, AI tools, future adapters) is covered, not only the REST DTO validation.
 */
public class TransferRequestValidator implements TransferValidator {

    private static final int MAX_DECIMALS = 2;

    @Override
    public void validate(TransfersContext context, BigDecimal fee) {
        if (Objects.equals(context.from().getId(), context.to().getId()))
            throw new InvalidTransferException("The origin account and destination account must be different");

        BigDecimal amount = context.amount();
        if (amount == null || amount.signum() <= 0)
            throw new InvalidTransferException("The amount must be positive");

        if (amount.stripTrailingZeros().scale() > MAX_DECIMALS)
            throw new InvalidTransferException("The amount cannot have more than " + MAX_DECIMALS + " decimals");

        if (context.from().getBalance().getCurrency() != context.to().getBalance().getCurrency())
            throw new InvalidTransferException("Transfers between accounts with different currencies are not supported");
    }
}
