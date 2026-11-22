package com.atlas.bank.atlas_bank.domain.customer.model;

import com.atlas.bank.atlas_bank.domain.customer.enums.CustomerStatusEnum;
import com.atlas.bank.atlas_bank.domain.shared.model.Email;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Builder
public class Customer {

    @EqualsAndHashCode.Include
    private Long id;

    private String name;

    private Email email;

    private CustomerStatusEnum status;

    private LocalDateTime createdAt;

    public boolean isActive() {
        return status == CustomerStatusEnum.ACTIVE;
    }

    public void initDefaults() {
        if (createdAt == null) createdAt = LocalDateTime.now();

        if (status == null) status = CustomerStatusEnum.ACTIVE;
    }
}
