package com.atlas.bank.atlas_bank.infrastructure.account.adapter.out.persistence;

import com.atlas.bank.atlas_bank.application.account.port.out.AccountRepositoryPort;
import com.atlas.bank.atlas_bank.domain.account.model.Account;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class JpaAccountRepositoryAdapter implements AccountRepositoryPort {
    private final SpringDataAccountRepository accountRepository;
    private final AccountPersistenceMapper accountMapper;

    @Override
    public Account save(Account account) {
        account.initDefaults();
        return accountMapper.toDomain(accountRepository.save(accountMapper.toJpaEntity(account)));
    }

    @Override
    public List<Account> findAll() {
        return accountRepository.findAll().stream().map(accountMapper::toDomain).collect(Collectors.toList());
    }

    @Override
    public Optional<Account> findById(Long id) {
        return accountRepository.findById(id).map(accountMapper::toDomain);
    }

    @Override
    public Optional<Account> findByIdForUpdate(Long id) {
        return accountRepository.findByIdForUpdate(id).map(accountMapper::toDomain);
    }
}
