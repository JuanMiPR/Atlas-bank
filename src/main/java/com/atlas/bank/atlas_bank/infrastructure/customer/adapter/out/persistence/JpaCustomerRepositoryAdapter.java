package com.atlas.bank.atlas_bank.infrastructure.customer.adapter.out.persistence;

import com.atlas.bank.atlas_bank.application.customer.port.out.CustomerRepositoryPort;
import com.atlas.bank.atlas_bank.domain.customer.model.Customer;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class JpaCustomerRepositoryAdapter implements CustomerRepositoryPort {
    private final SpringDataCustomerRepository customerRepository;
    private final CustomerPersistenceMapper customerMapper;


    @Override
    public Optional<Customer> findById(Long id) {
        return customerRepository.findById(id)
                .map(customerMapper::toDomain);
    }

    @Override
    public Customer save(Customer customer) {
        customer.initDefaults();
        return customerMapper.toDomain(customerRepository.save(customerMapper.toEntity(customer)));
    }
}
