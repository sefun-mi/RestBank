package com.RestBank.modules.operations.api;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DepositRequest {
    @NotBlank(message = "account number must be provided")
    private String accountNumber;

    @DecimalMin(value = "1.0", message = "Deposit amount must not be less than 1 Naira or more than 1 million Naira")
    @DecimalMax(value = "1000000.0", message = "Deposit amount must not be less than 1 Naira or more than 1 million Naira")
    @NotNull(message = "a value must be provided for deposit amount")
    private Double amount;

    private String narration;
}
