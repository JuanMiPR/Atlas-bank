package com.atlas.bank.atlas_bank.domain.shared.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Getter
@NoArgsConstructor
@EqualsAndHashCode
public class Email {

    private static final String VALIDATION_REGEXP = "^[\\w.+%-]+@[\\w.-]+\\.[a-zA-Z]{2,}$";

    private String value;


    private Email(String email) {

        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("Email is mandatory");
        }

        String normalized = email.trim().toLowerCase();

        if (!normalized.matches(VALIDATION_REGEXP)) {
            throw new IllegalArgumentException("Invalid email address");
        }

        this.value = normalized;
    }

    public static Email of(String email) {
        return new Email(email);
    }


    @Override
    public String toString() {
        return "The email is: " + value;
    }
}
