package com.RestBank.modules.operations.service;


import com.RestBank.modules.operations.api.LoginRequest;
import com.RestBank.modules.operations.api.LoginResponse;
import com.RestBank.modules.operations.service.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

@Component
@RequiredArgsConstructor
public class AuthenticationService {
    private final UserDetailsService userDetailsService;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public LoginResponse authenticate(LoginRequest loginRequest){
        String accountNumber = loginRequest.getAccountNumber();
        String password = loginRequest.getPassword();

        UserDetails details = authenticate(accountNumber, password);

        String token = jwtUtil.generateToken(details.getUsername());

        return new LoginResponse(token);
    }

    private UserDetails authenticate(String username, String password) {
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);
        if(! passwordEncoder.matches(password, userDetails.getPassword() )){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Incorrect credentials");
        }
        return userDetails;
    }
}