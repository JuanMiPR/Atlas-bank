package com.atlas.bank.atlas_bank.infrastructure.shared.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Getter
@Embeddable
@NoArgsConstructor
@EqualsAndHashCode
public class Email {

    private static final String VALIDATION_REGEXP = "^[\\w.+%-]+@[\\w.-]+\\.[a-zA-Z]{2,}$";

    @Column(nullable = false)
    private String value;


    private Email(String email) {

        if (email == null) {
            throw new IllegalArgumentException("Email is mandatory");
        }

        if (!email.matches(VALIDATION_REGEXP)) {
            throw new IllegalArgumentException("Invalid email address");
        }

        this.value = email;
    }

    public static Email of(String email) {
        return new Email(email);
    }


    @Override
    public String toString() {
        return "The email is: " + value;
    }
}
