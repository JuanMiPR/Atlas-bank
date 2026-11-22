package com.atlas.bank.atlas_bank.infrastructure.account.adapter.in.rest;

import com.atlas.bank.atlas_bank.application.account.command.CloseAccountCommand;
import com.atlas.bank.atlas_bank.application.account.command.CreateAccountCommand;
import com.atlas.bank.atlas_bank.application.account.port.in.CloseAccountUseCase;
import com.atlas.bank.atlas_bank.application.account.port.in.CreateAccountUseCase;
import com.atlas.bank.atlas_bank.application.account.port.in.GetAccountUseCase;
import com.atlas.bank.atlas_bank.application.account.port.in.ListAccountUseCase;
import com.atlas.bank.atlas_bank.application.reporting.query.DashboardReadModel;
import com.atlas.bank.atlas_bank.application.reporting.service.AccountDashboardFacade;
import com.atlas.bank.atlas_bank.infrastructure.account.adapter.in.rest.dto.AccountMapper;
import com.atlas.bank.atlas_bank.infrastructure.account.adapter.in.rest.dto.AccountResponse;
import com.atlas.bank.atlas_bank.infrastructure.account.adapter.in.rest.dto.CreateAccountRequest;
import com.atlas.bank.atlas_bank.infrastructure.account.adapter.in.security.AccountAccessGuard;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/accounts")
@RequiredArgsConstructor
public class AccountController {
    private final CreateAccountUseCase createAccountUseCase;
    private final ListAccountUseCase listAccountUseCase;
    private final GetAccountUseCase getAccountUseCase;
    private final CloseAccountUseCase closeAccountUseCase;
    private final AccountMapper accountMapper;
    private final AccountDashboardFacade dashboardFacade;
    private final AccountAccessGuard accountAccessGuard;


    @PostMapping
    public ResponseEntity<AccountResponse> save(@Valid @RequestBody CreateAccountRequest request) {
        CreateAccountCommand command = CreateAccountCommand.builder()
                .ownerName(request.getOwnerName())
                .balance(request.getBalance())
                .email(request.getEmail())
                .type(request.getType())
                .accountNumber(request.getAccountNumber())
                .build();
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(accountMapper.toResponse(createAccountUseCase.create(command)));
    }

    @GetMapping
    public ResponseEntity<List<AccountResponse>> findAll() {
        return ResponseEntity.ok(listAccountUseCase.findAll()
                .stream()
                .map(accountMapper::toResponse)
                .toList());
    }

    @GetMapping("/{id}")
    public ResponseEntity<AccountResponse> findById(@PathVariable Long id) {
        accountAccessGuard.checkAccess(id);
        return ResponseEntity.ok(accountMapper.toResponse(getAccountUseCase.findById(id)));
    }

    @GetMapping("/{id}/dashboard")
    public ResponseEntity<DashboardReadModel> getDashboard(@PathVariable Long id) {
        accountAccessGuard.checkAccess(id);
        return ResponseEntity.ok(dashboardFacade.getDashboard(id));
    }

    @PatchMapping("/{id}/close")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<AccountResponse> close(@PathVariable Long id) {
        return ResponseEntity.ok(accountMapper.toResponse(
                closeAccountUseCase.close(new CloseAccountCommand(id))));
    }

}
