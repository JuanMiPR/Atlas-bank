package com.atlas.bank.atlas_bank.infrastructure.adapter.in.rest.dto;

import com.atlas.bank.atlas_bank.domain.model.shared.Money;
import com.atlas.bank.atlas_bank.domain.model.transaction.Transaction;
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
