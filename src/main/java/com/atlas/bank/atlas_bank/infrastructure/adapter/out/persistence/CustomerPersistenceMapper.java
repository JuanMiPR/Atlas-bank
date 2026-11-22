package com.atlas.bank.atlas_bank.infrastructure.adapter.out.persistence;

import com.atlas.bank.atlas_bank.domain.model.customer.Customer;
import com.atlas.bank.atlas_bank.domain.model.shared.Email;
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
        entity.setEmail(Objects.nonNull(entity.getEmail())
                ? com.atlas.bank.atlas_bank.infrastructure.adapter.out.persistence.Email.of(entity.getEmail()
                .getValue())
                : null);
        entity.setStatus(domain.getStatus());
        entity.setCreatedAt(domain.getCreatedAt());
        return entity;
    }
}
