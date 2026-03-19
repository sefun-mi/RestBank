package com.RestBank.modules.operations.service.accountaction;

import com.RestBank.modules.data.datasource.AccountDataSource;
import com.RestBank.modules.data.datasource.TransactionDataSource;
import com.RestBank.modules.data.enums.TransactionType;
import com.RestBank.modules.data.model.Account;
import com.RestBank.modules.data.model.Transaction;
import com.RestBank.modules.operations.api.WithdrawalRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service("WITHDRAWAL")
@RequiredArgsConstructor
public class WithdrawalAction implements AccountAction{
    private final TransactionDataSource transactionDataSource;
    private final AccountDataSource accountDataSource;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void effect(Object argument) {
        WithdrawalRequest withdrawalRequest = (WithdrawalRequest) argument;

        Account account = accountDataSource.retrieveByAccountNumber(withdrawalRequest.getAccountNumber())
                .orElseThrow(()-> new ResponseStatusException(HttpStatus.NOT_FOUND,"Account Number does not exist"));
        if(! passwordEncoder.matches(withdrawalRequest.getPassword(), account.getPassword())){
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED,"Incorrect password");
        }

        Double amount = withdrawalRequest.getAmount();
        Double newBalance = account.getBalance() - amount;

        if(newBalance < 500){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Insufficient balance");
        }

        account.setBalance(newBalance);
        accountDataSource.update(account);

        Transaction transaction = new Transaction();
        transaction.setTransactionType(TransactionType.WITHDRAWAL);
        transaction.setNarration(withdrawalRequest.getNarration());
        transaction.setAmount(amount);
        transaction.setAccountBalance(newBalance);
        transactionDataSource.save(withdrawalRequest.getAccountNumber(), transaction);
    }

    @Override
    public TransactionType getType() {
        return TransactionType.WITHDRAWAL;
    }
}
