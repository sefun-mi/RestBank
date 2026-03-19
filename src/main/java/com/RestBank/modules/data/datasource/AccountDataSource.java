package com.RestBank.modules.data.datasource;

import com.RestBank.modules.data.model.Account;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Component
public class AccountDataSource {
    private final Map<String, Account> dataSource = new HashMap<>();

    public Map<String, Account> get(){
        return dataSource;
    }

    public void save(Account account){
        String accountNumber = generateUniqueAccountNumber();
        account.setAccountNumber(accountNumber);
        dataSource.put(accountNumber, account);
    }

    private String generateUniqueAccountNumber(){
        String accountNumber;
        do{
            accountNumber = RandomStringUtils.randomNumeric(10);
        }while (dataSource.containsKey(accountNumber));

        return accountNumber;
    }

    public void update(Account account){
        dataSource.put(account.getAccountNumber(), account);
    }

    public boolean existsByAccountName(String accountName){
        boolean exists = false;
        for(Account account : dataSource.values()){
            if(accountName.equalsIgnoreCase(account.getAccountName())){
                exists = true;
            }
        }

        return exists;
    }

    public Optional<Account> retrieveByAccountNumber(String accountNumber){
        return Optional.ofNullable(dataSource.get(accountNumber));
    }
}
