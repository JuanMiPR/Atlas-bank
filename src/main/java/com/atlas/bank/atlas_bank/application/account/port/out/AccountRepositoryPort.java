package com.atlas.bank.atlas_bank.application.account.port.out;

import com.atlas.bank.atlas_bank.domain.account.model.Account;

import java.util.List;
import java.util.Optional;

public interface AccountRepositoryPort {
    Account save(Account account);

    List<Account> findAll();

    Optional<Account> findById(Long id);

    /**
     * Loads the account holding an exclusive lock on it until the surrounding transaction ends.
     * Must be called inside a transaction.
     */
    Optional<Account> findByIdForUpdate(Long id);
}
