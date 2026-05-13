package com.RestBank.modules.operations.controller;

import com.RestBank.modules.common.idempotency.Idempotent;
import com.RestBank.modules.common.response.WebResponseBuilder;
import com.RestBank.modules.operations.api.CreateAccountRequest;
import com.RestBank.modules.operations.api.DepositRequest;
import com.RestBank.modules.operations.api.WithdrawalRequest;
import com.RestBank.modules.operations.service.AccountService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/")
@RequiredArgsConstructor
public class AccountController {
    private final AccountService accountService;

    @Idempotent
    @PostMapping("/create_account")
    public ResponseEntity<Object> createAccount(@Valid @RequestBody CreateAccountRequest createAccountRequest){

        return WebResponseBuilder.buildSuccessResponse(accountService.createAccount(createAccountRequest));
    }

    @Idempotent
    @PostMapping("/deposit")
    public ResponseEntity<Object> deposit(@Valid @RequestBody DepositRequest depositRequest){
        accountService.deposit(depositRequest);
        return WebResponseBuilder.buildSuccessResponse(null);
    }

    @Idempotent
    @PostMapping("/withdrawal")
    public ResponseEntity<Object> withdraw(@Valid @RequestBody WithdrawalRequest withdrawalRequest){
        accountService.withdraw(withdrawalRequest);
        return WebResponseBuilder.buildSuccessResponse(null);
    }

    @GetMapping("/account_info/{accountNumber}")
    public ResponseEntity<Object> accountInfo(@PathVariable String accountNumber){

        return WebResponseBuilder.buildSuccessResponse(accountService.accountInfo(accountNumber));
    }

    @GetMapping("/account_statement/{accountNumber}")
    public ResponseEntity<Object> accountStatement(@PathVariable String accountNumber){

        return WebResponseBuilder.buildSuccessResponse(accountService.accountStatement(accountNumber));
    }
}
