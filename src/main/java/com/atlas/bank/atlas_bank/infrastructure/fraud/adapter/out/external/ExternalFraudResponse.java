package com.atlas.bank.atlas_bank.infrastructure.fraud.adapter.out.external;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;


@Getter
@AllArgsConstructor
@ToString
public class ExternalFraudResponse {
    private String riskLevel;
    private double score;
    private String recommendation;
}
