package com.atlas.bank.atlas_bank.infrastructure.transaction.config;

import com.atlas.bank.atlas_bank.application.transaction.port.out.FraudCheckPort;
import com.atlas.bank.atlas_bank.domain.transaction.service.TransferDomainService;
import com.atlas.bank.atlas_bank.domain.transaction.fee.CheckingFeeCalculator;
import com.atlas.bank.atlas_bank.domain.transaction.fee.DefaultFeeCalculator;
import com.atlas.bank.atlas_bank.domain.transaction.fee.PremiumFeeCalculator;
import com.atlas.bank.atlas_bank.domain.transaction.fee.SavingsFeeCalculator;
import com.atlas.bank.atlas_bank.domain.transaction.validation.AccountStatusValidator;
import com.atlas.bank.atlas_bank.application.transaction.validation.FraudValidator;
import com.atlas.bank.atlas_bank.domain.transaction.validation.SufficientFoundValidator;
import com.atlas.bank.atlas_bank.domain.transaction.validation.TransferRequestValidator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;

@Configuration
public class TransactionBeanConfig {

    @Bean
    public TransferDomainService transferDomainService() {
        return new TransferDomainService();
    }

    @Bean
    @Order(1)
    public SavingsFeeCalculator savingsFeeCalculator() {
        return new SavingsFeeCalculator();
    }

    @Bean
    @Order(2)
    public CheckingFeeCalculator checkingFeeCalculator() {
        return new CheckingFeeCalculator();
    }

    @Bean
    @Order(3)
    public PremiumFeeCalculator premiumFeeCalculator() {
        return new PremiumFeeCalculator();
    }

    @Bean
    public DefaultFeeCalculator defaultFeeCalculator() {
        return new DefaultFeeCalculator();
    }

    @Bean
    @Order(0)
    public TransferRequestValidator transferRequestValidator() {
        return new TransferRequestValidator();
    }

    @Bean
    @Order(1)
    public AccountStatusValidator accountStatusValidator() {
        return new AccountStatusValidator();
    }

    @Bean
    @Order(2)
    public SufficientFoundValidator sufficientFundsValidator() {
        return new SufficientFoundValidator();
    }

    @Bean
    @Order(3)
    public FraudValidator fraudValidator(FraudCheckPort fraudCheckPort) {
        return new FraudValidator(fraudCheckPort);
    }
}