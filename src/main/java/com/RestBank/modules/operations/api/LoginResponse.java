package com.RestBank.modules.operations.api;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginResponse {
    private String token;
    private String message = "Logged in successfully";

    public LoginResponse(String token){
        this.token=token;
    }
}