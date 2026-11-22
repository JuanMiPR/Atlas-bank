package com.atlas.bank.atlas_bank.application.customer.port.out;

import com.atlas.bank.atlas_bank.domain.customer.model.Customer;

import java.util.Optional;

public interface CustomerRepositoryPort {
    Optional<Customer> findById(Long id);

    Customer save(Customer customer);
}
