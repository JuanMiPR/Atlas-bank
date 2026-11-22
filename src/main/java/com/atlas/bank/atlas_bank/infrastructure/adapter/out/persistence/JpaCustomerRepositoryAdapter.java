package com.atlas.bank.atlas_bank.infrastructure.adapter.out.persistence;

import com.atlas.bank.atlas_bank.application.port.out.CustomerRepositoryPort;
import com.atlas.bank.atlas_bank.application.port.out.TransactionRepositoryPort;
import com.atlas.bank.atlas_bank.domain.model.customer.Customer;
import com.atlas.bank.atlas_bank.domain.model.transaction.Transaction;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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
