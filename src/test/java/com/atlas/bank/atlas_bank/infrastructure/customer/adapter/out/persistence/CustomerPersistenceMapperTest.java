package com.atlas.bank.atlas_bank.infrastructure.customer.adapter.out.persistence;

import com.atlas.bank.atlas_bank.domain.customer.enums.CustomerStatusEnum;
import com.atlas.bank.atlas_bank.domain.customer.model.Customer;
import com.atlas.bank.atlas_bank.domain.shared.model.Email;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class CustomerPersistenceMapperTest {

    private final CustomerPersistenceMapper mapper = new CustomerPersistenceMapper();

    @Test
    @DisplayName("Should keep the email when mapping the domain customer to the JPA entity")
    void shouldKeepEmailWhenMappingToEntity() {
        Customer customer = Customer.builder()
                .id(1L)
                .name("Jane Doe")
                .email(Email.of("jane@example.com"))
                .status(CustomerStatusEnum.ACTIVE)
                .createdAt(LocalDateTime.now())
                .build();

        CustomerJpaEntity entity = mapper.toEntity(customer);

        assertNotNull(entity.getEmail());
        assertEquals("jane@example.com", entity.getEmail().getValue());
    }

    @Test
    @DisplayName("Should keep the email when mapping the JPA entity to the domain customer")
    void shouldKeepEmailWhenMappingToDomain() {
        CustomerJpaEntity entity = new CustomerJpaEntity();
        entity.setId(1L);
        entity.setName("Jane Doe");
        entity.setEmail(com.atlas.bank.atlas_bank.infrastructure.shared.persistence.Email.of("jane@example.com"));
        entity.setStatus(CustomerStatusEnum.ACTIVE);
        entity.setCreatedAt(LocalDateTime.now());

        Customer customer = mapper.toDomain(entity);

        assertNotNull(customer.getEmail());
        assertEquals("jane@example.com", customer.getEmail().getValue());
    }
}
