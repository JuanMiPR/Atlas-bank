package com.atlas.bank.atlas_bank.infrastructure.customer.adapter.out.persistence;

import com.atlas.bank.atlas_bank.domain.customer.model.Customer;
import com.atlas.bank.atlas_bank.domain.shared.model.Email;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public class CustomerPersistenceMapper {

    public Customer toDomain(CustomerJpaEntity entity) {
        if (entity == null) return null;

        return Customer.builder()
                .id(entity.getId())
                .name(entity.getName())
                .email(Objects.nonNull(entity.getEmail())
                        ? Email.of(entity.getEmail()
                        .getValue())
                        : null)
                .status(entity.getStatus())
                .createdAt(entity.getCreatedAt())
                .build();
    }

    public CustomerJpaEntity toEntity(Customer domain) {
        if (domain == null) return null;
        CustomerJpaEntity entity = new CustomerJpaEntity();
        entity.setId(domain.getId());
        entity.setName(domain.getName());
        entity.setEmail(Objects.nonNull(domain.getEmail())
                ? com.atlas.bank.atlas_bank.infrastructure.shared.persistence.Email.of(domain.getEmail()
                .getValue())
                : null);
        entity.setStatus(domain.getStatus());
        entity.setCreatedAt(domain.getCreatedAt());
        return entity;
    }
}
