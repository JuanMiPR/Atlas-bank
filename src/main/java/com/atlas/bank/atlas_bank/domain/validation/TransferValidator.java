package com.atlas.bank.atlas_bank.domain.validation;

import com.atlas.bank.atlas_bank.domain.model.transaction.TransfersContext;

public interface TransferValidator {

    void validate(TransfersContext transfersContext);
}
