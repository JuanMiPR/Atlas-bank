package com.atlas.bank.atlas_bank.infrastructure.account.adapter.in.rest.dto;

import com.atlas.bank.atlas_bank.domain.account.model.Account;
import com.atlas.bank.atlas_bank.domain.shared.enums.CurrencyEnum;
import com.atlas.bank.atlas_bank.domain.shared.model.Email;
import com.atlas.bank.atlas_bank.domain.shared.model.Money;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.math.BigDecimal;

@Mapper(componentModel = "spring")
public interface AccountMapper {


    @Mapping(target = "balance", source = "balance", qualifiedByName = "toBigDecimal")
    @Mapping(target = "email", source = "email", qualifiedByName = "fromEmail")
    AccountResponse toResponse(Account account);

    @Named("toMoney")
    default Money toMoney(BigDecimal amount) {
        if (amount == null) return null;

        return Money.of(amount, CurrencyEnum.EUR);
    }

    @Named("toBigDecimal")
    default BigDecimal toBigDecimal(Money money) {
        if (money == null) return null;

        return money.getAmount();
    }

    @Named("toEmail")
    default Email toEmail(String email) {
        if (email == null) return null;

        return Email.of(email);
    }

    @Named("fromEmail")
    default String fromEmail(Email email) {
        if (email == null) return null;

        return email.getValue();
    }
}
