package com.rslakra.healthcare.routinecheckup.service.security;

/**
 * @author Rohtash Lakra
 * @created 8/12/21 4:24 PM
 */

import com.rslakra.healthcare.routinecheckup.entity.UserEntity;
import io.jsonwebtoken.impl.DefaultClaims;
import org.springframework.security.core.userdetails.UserDetails;

import jakarta.servlet.http.HttpServletRequest;
import java.util.Optional;

public interface TokenComponent {

    Integer MILLIS_IN_SECOND = 1000;

    String generateRegistrationToken(UserEntity userEntity);

    String generateToken(UserDetails userDetails);

    DefaultClaims parse(String token);

    DefaultClaims parseRegistrationToken(String token);

    String getLoginFromToken(String token);

    String getLoginFromRegistrationToken(String token);

    Optional<String> getTokenFromRequest(HttpServletRequest request);

}
