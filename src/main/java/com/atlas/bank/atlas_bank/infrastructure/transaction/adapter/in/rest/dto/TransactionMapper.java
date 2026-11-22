package com.atlas.bank.atlas_bank.infrastructure.transaction.adapter.in.rest.dto;

import com.atlas.bank.atlas_bank.domain.shared.model.Money;
import com.atlas.bank.atlas_bank.domain.transaction.model.Transaction;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.math.BigDecimal;

@Mapper(componentModel = "spring")
public interface TransactionMapper {
    @Mapping(target = "amount", source = "amount", qualifiedByName = "toBigDecimal")
    TransactionResponse toTransactionResponse(Transaction from);


    @Named("toBigDecimal")
    default BigDecimal toBigDecimal(Money money) {
        if (money == null)
            return null;

        return money.getAmount();
    }
}
