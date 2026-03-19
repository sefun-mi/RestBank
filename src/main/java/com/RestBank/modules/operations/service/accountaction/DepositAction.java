package com.RestBank.modules.operations.service.accountaction;

import com.RestBank.modules.data.datasource.AccountDataSource;
import com.RestBank.modules.data.datasource.TransactionDataSource;
import com.RestBank.modules.data.enums.TransactionType;
import com.RestBank.modules.data.model.Account;
import com.RestBank.modules.data.model.Transaction;
import com.RestBank.modules.operations.api.DepositRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service("DEPOSIT")
@RequiredArgsConstructor
public class DepositAction implements AccountAction{
    private final TransactionDataSource transactionDataSource;
    private final AccountDataSource accountDataSource;


    @Override
    public void effect(Object argument) {
        DepositRequest depositRequest = (DepositRequest) argument;

        Account account = accountDataSource.retrieveByAccountNumber(depositRequest.getAccountNumber())
                .orElseThrow(()-> new ResponseStatusException(HttpStatus.NOT_FOUND,"Account Number does not exist"));

        Double amount = depositRequest.getAmount();
        Double newBalance = account.getBalance() + amount;

        account.setBalance(newBalance);
        accountDataSource.update(account);

        Transaction transaction = new Transaction();
        transaction.setTransactionType(TransactionType.DEPOSIT);
        transaction.setNarration(depositRequest.getNarration());
        transaction.setAmount(amount);
        transaction.setAccountBalance(newBalance);
        transactionDataSource.save(depositRequest.getAccountNumber(), transaction);
    }

    @Override
    public TransactionType getType() {
        return TransactionType.DEPOSIT;
    }
}
