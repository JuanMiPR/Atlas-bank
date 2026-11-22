package com.atlas.bank.atlas_bank.infrastructure.adapter.out.fraud;

import com.atlas.bank.atlas_bank.application.port.out.FraudCheckPort;
import com.atlas.bank.atlas_bank.domain.model.shared.FraudCheckResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
@Slf4j
public class ExternalFraudCheckAdapter implements  FraudCheckPort {
    @Override
    public FraudCheckResult check(Long accountId, BigDecimal amount) {
        ExternalFraudResponse response = callExternalApi(accountId, amount);
        log.info("Response: {}", response);
        return "BLOCK".equalsIgnoreCase(response.getRecommendation())
                ? FraudCheckResult.blocked("Operation blocked for risk" + response.getRiskLevel() + "(score: " + response.getScore() + ")")
                : FraudCheckResult.allowed();
    }

    private ExternalFraudResponse callExternalApi(Long accountId, BigDecimal amount) {
        if (amount.compareTo(new BigDecimal(1000000)) > 0) return new ExternalFraudResponse("HIGHT", 0.95, "BLOCK");

        return new ExternalFraudResponse("LOW", 0.1, "ALLOW");
    }
}
