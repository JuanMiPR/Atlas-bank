package com.atlas.bank.atlas_bank.infrastructure.customer.adapter.out.persistence;

import com.atlas.bank.atlas_bank.domain.customer.enums.CustomerStatusEnum;
import com.atlas.bank.atlas_bank.infrastructure.shared.persistence.Email;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "customers")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class CustomerJpaEntity {

    @EqualsAndHashCode.Include
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, name = "name")
    private String name;

    @Embedded
    @AttributeOverride(name = "value", column = @Column(name = "email", nullable = false, unique = true))
    private Email email;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, name = "status", length = 20)
    private CustomerStatusEnum status;

    @Column(nullable = false, name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {
        if (createdAt == null) createdAt = LocalDateTime.now();

        if (status == null) status = CustomerStatusEnum.ACTIVE;
    }
}

