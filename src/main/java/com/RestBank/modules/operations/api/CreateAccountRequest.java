package com.RestBank.modules.operations.api;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateAccountRequest {
    @NotBlank(message = "Account name is required")
    private String accountName;

    @NotBlank(message = "Password is required")
    private String accountPassword;

    @DecimalMin(value = "500.0", message = "initial deposit must not be less than 500 Naira")
    @NotNull(message = "a value must be provided for initial deposit")
    private Double initialDeposit;
}
