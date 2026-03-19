package com.RestBank.modules.data.datasource;

import com.RestBank.modules.data.model.Account;
import com.RestBank.modules.data.model.Transaction;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class TransactionDataSource {
    private final Map<String, List<Transaction>> dataSource = new HashMap<>();

    public void save(String accountNumber, Transaction newTransaction){
        String reference = generateUniqueReference();
        newTransaction.setReference(reference);
        newTransaction.setTransactionDate(LocalDateTime.now());

        List<Transaction> transactions = dataSource.getOrDefault(accountNumber, new ArrayList<>());
        transactions.add(newTransaction);

        dataSource.put(accountNumber, transactions);
    }

    public List<Transaction> retrieveByAccountNumber(String accountNumber){
        return dataSource.getOrDefault(accountNumber, new ArrayList<>());
    }

    private String generateUniqueReference(){
        String reference;
        do{
            reference = RandomStringUtils.randomNumeric(5);
        }while (dataSource.containsKey(reference));

        return reference;
    }
}
