package com.RestBank.modules.operations.api;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginRequest {
    @NotBlank(message = "Account Number is required")
    private String accountNumber;

    @NotBlank(message = "Password is required")
    private String password;
}
