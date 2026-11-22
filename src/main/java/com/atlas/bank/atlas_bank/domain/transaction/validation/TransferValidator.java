package com.atlas.bank.atlas_bank.domain.transaction.validation;

import com.atlas.bank.atlas_bank.domain.transaction.model.TransfersContext;

import java.math.BigDecimal;

public interface TransferValidator {

    void validate(TransfersContext transfersContext, BigDecimal fee);
}
