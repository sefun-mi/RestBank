package com.RestBank.modules.operations.api;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class WithdrawalRequest {
    @NotBlank(message = "account number must be provided")
    private String accountNumber;

    @DecimalMin(value = "1.0", message = "Withdrawal amount must not be less than 1 Naira")
    @NotNull(message = "a value must be provided for deposit amount")
    private Double amount;

    @NotBlank(message = "password must be provided")
    private String password;

    //todo @Pattern sanitation
    private String narration;
}
