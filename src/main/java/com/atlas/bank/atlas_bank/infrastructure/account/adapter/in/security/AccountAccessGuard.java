package com.atlas.bank.atlas_bank.infrastructure.account.adapter.in.security;

import com.atlas.bank.atlas_bank.application.account.port.in.GetAccountUseCase;
import com.atlas.bank.atlas_bank.domain.account.exception.AccountNotFoundException;
import com.atlas.bank.atlas_bank.domain.account.model.Account;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

/**
 * Authorization checks shared by every inbound adapter (REST controllers and AI tools).
 * <p>
 * ADMIN can access any account. Any other authenticated user can only access accounts whose
 * email matches the {@code email} claim of their JWT. Fails closed: no authentication, no
 * email claim, or an unknown account all result in {@link AccessDeniedException} for non-admins,
 * so account ids cannot be enumerated.
 */
@Component
@RequiredArgsConstructor
public class AccountAccessGuard {

    private static final String ROLE_ADMIN = "ROLE_ADMIN";
    private static final String EMAIL_CLAIM = "email";

    private final GetAccountUseCase getAccountUseCase;

    public void checkAccess(Long accountId) {
        Authentication authentication = currentAuthentication();
        if (isAdmin(authentication)) return;

        String email = emailOf(authentication);
        Account account;
        try {
            account = getAccountUseCase.findById(accountId);
        } catch (AccountNotFoundException e) {
            throw new AccessDeniedException("Access to account " + accountId + " denied");
        }

        if (account.getEmail() == null || !account.getEmail().getValue().equalsIgnoreCase(email))
            throw new AccessDeniedException("Access to account " + accountId + " denied");
    }

    public void requireAdmin() {
        if (!isAdmin(currentAuthentication()))
            throw new AccessDeniedException("Admin role required");
    }

    private Authentication currentAuthentication() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated())
            throw new AccessDeniedException("Authentication required");
        return authentication;
    }

    private boolean isAdmin(Authentication authentication) {
        return authentication.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(ROLE_ADMIN::equals);
    }

    private String emailOf(Authentication authentication) {
        if (authentication.getPrincipal() instanceof Jwt jwt) {
            String email = jwt.getClaimAsString(EMAIL_CLAIM);
            if (email != null && !email.isBlank()) return email.trim();
        }
        throw new AccessDeniedException("The token does not contain an email claim");
    }
}
