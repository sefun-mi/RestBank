package com.RestBank.modules.operations.service.util;


import com.nimbusds.jwt.JWT;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;

@Service
@RequiredArgsConstructor
public class JwtUtil {
    private final JwtEncoder oauth2JwtEncoder;
    private final JwtDecoder oauth2JwtDecoder;

    public String generateToken(String accountNumber) {


        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer("RestBank")
                .issuedAt(Instant.now())
                .expiresAt(LocalDateTime.now().plusDays(1).atZone(ZoneId.systemDefault()).toInstant())
                .subject(accountNumber)
                .build();

        return oauth2JwtEncoder.encode(JwtEncoderParameters.from(JwsHeader.with(MacAlgorithm.from("HS256")).build(),claims)).getTokenValue();
    }

    public String extractAccountNumber(String token) {
        Jwt jwt = oauth2JwtDecoder.decode(token);
        return jwt.getSubject();
    }

    public String extractClaimFromToken(String claim, String token) {
        Jwt jwt = oauth2JwtDecoder.decode(token);
        return jwt.getClaimAsString(claim);
    }

    public JwtAuthenticationToken createAuthInstance(String token) {
        Jwt jwt = oauth2JwtDecoder.decode(token);
        return new JwtAuthenticationToken(jwt);
    }

    public void validateToken(String token) {
        try{
            oauth2JwtDecoder.decode(token);
        }catch (Exception e){
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED,"Invalid auth token");
        }
    }

    public static String extractClaimFromCurrentToken(String claim) {
        Authentication currentAuthentication = SecurityContextHolder.getContext().getAuthentication();
        if(currentAuthentication instanceof JwtAuthenticationToken jwtAuthenticationToken){
            Map<String, Object> claims = jwtAuthenticationToken.getTokenAttributes();
            return (String) claims.getOrDefault(claim,"");
        }
        return "";
    }

    public static String extractCurrentAccountNumber() {
        return extractClaimFromCurrentToken("sub");
    }

    public static byte[] toFixed256Bytes(String input) {
        byte[] originalBytes = input.getBytes(StandardCharsets.UTF_8);

        if (originalBytes.length > 256) {
            // Truncate if longer than 256 bytes
            return Arrays.copyOf(originalBytes, 256);
        } else if (originalBytes.length < 256) {
            // Pad with zeros if shorter than 256 bytes
            byte[] padded = new byte[256];
            System.arraycopy(originalBytes, 0, padded, 0, originalBytes.length);
            return padded;
        } else {
            // Exactly 256 bytes
            return originalBytes;
        }
    }

}