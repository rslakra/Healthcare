package com.rslakra.healthcare.routinecheckup.service.impl.security;

import com.rslakra.healthcare.routinecheckup.entity.UserEntity;
import com.rslakra.healthcare.routinecheckup.service.security.TokenService;
import com.rslakra.healthcare.routinecheckup.utils.components.holder.JwtConstants;
import com.rslakra.healthcare.routinecheckup.utils.components.holder.Messages;
import com.rslakra.healthcare.routinecheckup.utils.components.holder.RegistrationConstants;
import com.rslakra.healthcare.routinecheckup.exceptions.JwtTokenExpiredException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.impl.DefaultClaims;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import lombok.RequiredArgsConstructor;

import static java.util.concurrent.TimeUnit.SECONDS;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.util.WebUtils;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * @author Rohtash Lakra
 * @created 8/12/21 4:23 PM
 */
@Component
@RequiredArgsConstructor
public class TokenServiceImpl implements TokenService {

    private final JwtConstants jwtConstants;
    private final RegistrationConstants registrationConstants;
    private final Messages messages;


    @Override
    public String generateRegistrationToken(UserEntity userEntity) {
        String result = generateToken(userEntity.getLogin(), registrationConstants.getRegistrationTimeMs(), registrationConstants.getRegistrationTokenKey(), jwtConstants.getLoginFieldName());

        return result;
    }

    @Override
    public String generateToken(UserDetails userDetails) {
        String result = generateToken(userDetails.getUsername(), jwtConstants.getExpirationTimeMs(), jwtConstants.getJwtKey(), jwtConstants.getLoginFieldName());

        return result;
    }

    @Override
    public DefaultClaims parse(String token) {
        SecretKey key = Keys.hmacShaKeyFor(jwtConstants.getJwtKey().getBytes(StandardCharsets.UTF_8));
        DefaultClaims body = (DefaultClaims) Jwts.parser().verifyWith(key).build().parseSignedClaims(token).getPayload();

        validateTokenClaims(body);

        return body;
    }

    @Override
    public DefaultClaims parseRegistrationToken(String token) {
        SecretKey key = Keys.hmacShaKeyFor(registrationConstants.getRegistrationTokenKey().getBytes(StandardCharsets.UTF_8));
        DefaultClaims body = (DefaultClaims) Jwts.parser().verifyWith(key).build().parseSignedClaims(token).getPayload();

        validateTokenClaims(body);

        return body;
    }

    @Override
    public String getLoginFromToken(String token) {
        DefaultClaims claims = parse(token);
        String login = getLoginFromClaims(claims);
        return login;
    }

    @Override
    public String getLoginFromRegistrationToken(String token) {
        DefaultClaims claims = parseRegistrationToken(token);
        String login = getLoginFromClaims(claims);
        return login;
    }

    /**
     * @param request
     * @return
     */
    @Override
    public Optional<String> getTokenFromRequest(HttpServletRequest request) {
        Cookie cookie = WebUtils.getCookie(request, jwtConstants.getParameterName());
        Optional<String> token = Optional.ofNullable(cookie).map(Cookie::getValue);

        return token;
    }

    /**
     * @param claims
     * @return
     */
    private String getLoginFromClaims(DefaultClaims claims) {
        return claims.get(jwtConstants.getLoginFieldName(), String.class);
    }

    /**
     * @param claims
     */
    private void validateTokenClaims(DefaultClaims claims) {
        Date expirationDate = claims.get(Claims.EXPIRATION, Date.class);
        Date currentDate = new Date();
        if (currentDate.after(expirationDate)) {
            throw new JwtTokenExpiredException(messages.getTokenExpired());
        }
    }

    /**
     * @param login
     * @param tokenLifeMs
     * @param tokenKey
     * @param loginFieldName
     * @return
     */
    private String generateToken(String login, Long tokenLifeMs, String tokenKey, String loginFieldName) {
        Map<String, Object> claims = getBaseClaims(login, loginFieldName);
        Long issuedAtSeconds = (Long) claims.get(Claims.ISSUED_AT);
        Long issuedAtMs = issuedAtSeconds == null ? (new Date()).getTime() : issuedAtSeconds * SECONDS.toMillis(1);
        long expirationAtMs = issuedAtMs + tokenLifeMs;

        // Convert string key to SecretKey for HS512 (requires at least 512 bits = 64 characters)
        SecretKey key = Keys.hmacShaKeyFor(tokenKey.getBytes(StandardCharsets.UTF_8));
        String result = Jwts.builder()
                .claims(claims)
                .expiration(new Date(expirationAtMs))
                .signWith(key)
                .compact();

        return result;
    }

    /**
     * @param username
     * @param loginFieldName
     * @return
     */
    private Map<String, Object> getBaseClaims(String username, String loginFieldName) {
        Map<String, Object> claims = new HashMap<>();
        Date issuedAt = new Date();
        long issuedAtSeconds = issuedAt.getTime() / SECONDS.toMillis(1);
        claims.put(Claims.ISSUED_AT, issuedAtSeconds);
        claims.put(Claims.NOT_BEFORE, issuedAtSeconds);
        claims.put(loginFieldName, username);

        return claims;
    }

}

