package com.atlas.bank.atlas_bank.infrastructure.adapter.out.fraud;

import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;


@Getter
@AllArgsConstructor
@Table
public class ExternalFraudResponse {
    private String riskLevel;
    private double score;
    private String recommendation;
}
