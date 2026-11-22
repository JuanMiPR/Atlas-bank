package com.atlas.bank.atlas_bank.application.transaction.validation;

import com.atlas.bank.atlas_bank.application.transaction.port.out.FraudCheckPort;
import com.atlas.bank.atlas_bank.domain.transaction.exception.FraudCheckException;
import com.atlas.bank.atlas_bank.domain.transaction.model.TransfersContext;
import com.atlas.bank.atlas_bank.application.transaction.dto.FraudCheckResult;
import com.atlas.bank.atlas_bank.domain.transaction.validation.TransferValidator;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;

@RequiredArgsConstructor
public class FraudValidator implements TransferValidator {
    private final FraudCheckPort fraudChecker;

    @Override
    public void validate(TransfersContext context, BigDecimal fee) {
        FraudCheckResult fraudCheckResult = fraudChecker.check(context.from()
                .getId(), context.amount());

        if (fraudCheckResult.blocked())
            throw new FraudCheckException(fraudCheckResult.reason());
    }
}
