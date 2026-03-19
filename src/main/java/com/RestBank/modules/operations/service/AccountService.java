package com.RestBank.modules.operations.service;


import com.RestBank.modules.data.datasource.AccountDataSource;
import com.RestBank.modules.data.datasource.TransactionDataSource;
import com.RestBank.modules.data.enums.TransactionType;
import com.RestBank.modules.data.model.Account;
import com.RestBank.modules.operations.api.*;
import com.RestBank.modules.operations.service.accountaction.AccountAction;
import com.RestBank.modules.operations.service.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AccountService {
    private final PasswordEncoder passwordEncoder;
    private final AccountDataSource accountDataSource;
    private final TransactionDataSource transactionDataSource;
    private final ApplicationContext applicationContext;

    public CreateAccountResponse createAccount(CreateAccountRequest createAccountRequest){
        if(accountDataSource.existsByAccountName(createAccountRequest.getAccountName())){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"An account has already been created for this entity");
        }

        String encodedPassword = passwordEncoder.encode(createAccountRequest.getAccountPassword());
        Account account = new Account();
        account.setAccountName(createAccountRequest.getAccountName());
        account.setPassword(encodedPassword);
        account.setBalance(createAccountRequest.getInitialDeposit());

        accountDataSource.save(account);

        return new CreateAccountResponse(account.getAccountNumber(), createAccountRequest.getInitialDeposit());
    }

    public AccountInfoResponse accountInfo(String accountNumber){
        accountAuthCheck(accountNumber);

        Account account = accountDataSource.retrieveByAccountNumber(accountNumber)
                .orElseThrow(()-> new ResponseStatusException(HttpStatus.NOT_FOUND,"Account Number does not exist"));

        AccountInfoResponse accountInfoResponse = new AccountInfoResponse();
        accountInfoResponse.setAccountName(account.getAccountName());
        accountInfoResponse.setAccountNumber(accountNumber);
        accountInfoResponse.setBalance(account.getBalance());

        return accountInfoResponse;
    }

    public List<TransactionHistoryResponse> accountStatement(String accountNumber){
        accountAuthCheck(accountNumber);

        return transactionDataSource.retrieveByAccountNumber(accountNumber)
                .stream().map(transaction -> {
                    TransactionHistoryResponse response = new TransactionHistoryResponse();
                    response.setTransactionDate(transaction.getTransactionDate().toString());
                    response.setTransactionType(transaction.getTransactionType());
                    response.setNarration(transaction.getNarration());
                    response.setAmount(transaction.getAmount());
                    response.setAccountBalance(transaction.getAccountBalance());
                    return response;
                }).collect(Collectors.toList());
    }

    public void deposit(DepositRequest depositRequest){
        accountAuthCheck(depositRequest.getAccountNumber());

        AccountAction accountAction = applicationContext.getBean("DEPOSIT", AccountAction.class);
        if(accountAction == null){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Operation not supported");
        }

        accountAction.effect(depositRequest);
    }

    public void withdraw(WithdrawalRequest withdrawalRequest){
        accountAuthCheck(withdrawalRequest.getAccountNumber());

        AccountAction accountAction = applicationContext.getBean("WITHDRAWAL", AccountAction.class);
        if(accountAction == null){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Operation not supported");
        }

        accountAction.effect(withdrawalRequest);
    }

    private void accountAuthCheck(String accountNumber){
        if (! accountNumber.equalsIgnoreCase(JwtUtil.extractCurrentAccountNumber())){
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,"this account number does not belong to you");
        }
    }
}