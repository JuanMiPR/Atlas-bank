package com.atlas.bank.atlas_bank.infrastructure.fraud.adapter.out.external;

import com.atlas.bank.atlas_bank.application.transaction.port.out.FraudCheckPort;
import com.atlas.bank.atlas_bank.application.transaction.dto.FraudCheckResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
@Slf4j
public class ExternalFraudCheckAdapter implements  FraudCheckPort {
    @Override
    public FraudCheckResult check(Long accountId, BigDecimal amount) {
        ExternalFraudResponse response = callExternalApi(amount);
        log.info("Response: {}", response);
        return "BLOCK".equalsIgnoreCase(response.getRecommendation())
                ? FraudCheckResult.blocked("Operation blocked for risk " + response.getRiskLevel() + " (score: " + response.getScore() + ")")
                : FraudCheckResult.allowed();
    }

    private ExternalFraudResponse callExternalApi(BigDecimal amount) {
        if (amount.compareTo(new BigDecimal(1000000)) > 0) return new ExternalFraudResponse("HIGH", 0.95, "BLOCK");

        return new ExternalFraudResponse("LOW", 0.1, "ALLOW");
    }
}
