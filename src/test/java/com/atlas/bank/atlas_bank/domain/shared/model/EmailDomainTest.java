package com.atlas.bank.atlas_bank.domain.shared.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class EmailDomainTest {

    @Test
    @DisplayName("Should create valid email")
    void shouldCreateValidEmail() {
        Email email = Email.of("test@test.com");

        assertEquals("test@test.com", email.getValue());
    }

    @Test
    @DisplayName("Should create normalize email")
    void shouldNormalizeEmail() {
        Email email = Email.of("TEST@TEST.com");

        assertEquals("test@test.com", email.getValue());
    }

    @Test
    @DisplayName("Should reject null email")
    void shouldRejectNullEmail() {
        assertThrows(IllegalArgumentException.class, () -> Email.of(""));
        assertThrows(IllegalArgumentException.class, () -> Email.of(null));
    }

    @Test
    @DisplayName("Should reject invalid email")
    void shouldRejectInvalidEmail() {
        assertThrows(IllegalArgumentException.class, () -> Email.of("test"));
    }


    @Test
    @DisplayName("Should accept plus addressing and trim surrounding spaces")
    void shouldAcceptPlusAndTrim() {
        assertEquals("juan+test@gmail.com", Email.of("juan+test@gmail.com").getValue());
        assertEquals("a@b.com", Email.of("  A@B.com ").getValue());
    }
}
