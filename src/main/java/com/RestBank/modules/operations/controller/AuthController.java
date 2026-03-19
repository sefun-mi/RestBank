package com.RestBank.modules.operations.controller;


import com.RestBank.modules.common.response.WebResponseBuilder;
import com.RestBank.modules.operations.api.LoginRequest;
import com.RestBank.modules.operations.service.AuthenticationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/")
@RequiredArgsConstructor
public class AuthController {
    private final AuthenticationService authenticator;

    @PostMapping("/login")
    public ResponseEntity<Object> login(@Valid @RequestBody LoginRequest loginRequest){

        return WebResponseBuilder.buildSuccessResponse(authenticator.authenticate(loginRequest));
    }

}