package com.atlas.bank.atlas_bank.infrastructure.account.adapter.in.security;

import com.atlas.bank.atlas_bank.application.account.port.in.GetAccountUseCase;
import com.atlas.bank.atlas_bank.domain.account.exception.AccountNotFoundException;
import com.atlas.bank.atlas_bank.domain.account.model.Account;
import com.atlas.bank.atlas_bank.domain.shared.model.Email;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class AccountAccessGuardTest {

    private final GetAccountUseCase getAccountUseCase = mock(GetAccountUseCase.class);
    private final AccountAccessGuard guard = new AccountAccessGuard(getAccountUseCase);

    @BeforeEach
    void setUp() {
        when(getAccountUseCase.findById(1L)).thenReturn(Account.builder().id(1L).email(Email.of("owner@example.com")).build());
        when(getAccountUseCase.findById(99L)).thenThrow(new AccountNotFoundException(99L));
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    @DisplayName("Admin can access any account")
    void adminCanAccessAnyAccount() {
        authenticate("admin@example.com", "ROLE_ADMIN");
        assertDoesNotThrow(() -> guard.checkAccess(1L));
        assertDoesNotThrow(guard::requireAdmin);
    }

    @Test
    @DisplayName("User can access own account only")
    void userCanAccessOwnAccountOnly() {
        authenticate("OWNER@example.com", "ROLE_USER");
        assertDoesNotThrow(() -> guard.checkAccess(1L));

        authenticate("intruder@example.com", "ROLE_USER");
        assertThrows(AccessDeniedException.class, () -> guard.checkAccess(1L));
        assertThrows(AccessDeniedException.class, guard::requireAdmin);
    }

    @Test
    @DisplayName("User gets 403 (not 404) for unknown accounts")
    void unknownAccountIsDeniedForUsers() {
        authenticate("owner@example.com", "ROLE_USER");
        assertThrows(AccessDeniedException.class, () -> guard.checkAccess(99L));
    }

    @Test
    @DisplayName("Anonymous access is denied")
    void anonymousIsDenied() {
        assertThrows(AccessDeniedException.class, () -> guard.checkAccess(1L));
        assertThrows(AccessDeniedException.class, guard::requireAdmin);
    }

    private void authenticate(String email, String role) {
        Jwt jwt = Jwt.withTokenValue("token")
                .header("alg", "none")
                .claim("email", email)
                .issuedAt(Instant.now())
                .expiresAt(Instant.now().plusSeconds(60))
                .build();
        SecurityContextHolder.getContext()
                .setAuthentication(new JwtAuthenticationToken(jwt, List.of(new SimpleGrantedAuthority(role))));
    }
}
