package com.RestBank.modules.operations.service;

import com.RestBank.modules.data.datasource.AccountDataSource;
import com.RestBank.modules.data.model.Account;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {
    private final AccountDataSource userInfoDataSource;

    @Override
    public UserDetails loadUserByUsername(String accountNumber) throws UsernameNotFoundException {
        Account account = userInfoDataSource.retrieveByAccountNumber(accountNumber)
                .orElseThrow(()->new ResponseStatusException(HttpStatus.BAD_REQUEST,"Incorrect credentials"));
        return account;
    }

}