package com.rslakra.healthcare.routinecheckup.service.impl.security;

import com.rslakra.healthcare.routinecheckup.entity.UserCsrfToken;
import com.rslakra.healthcare.routinecheckup.repository.UserCsrfTokenRepository;
import com.rslakra.healthcare.routinecheckup.service.security.TokenService;
import com.rslakra.healthcare.routinecheckup.utils.components.holder.CsrfConstants;
import lombok.RequiredArgsConstructor;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.security.web.csrf.CsrfTokenRepository;
import org.springframework.security.web.csrf.DefaultCsrfToken;
import org.springframework.stereotype.Component;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Optional;
import java.util.UUID;

/**
 * @author Rohtash Lakra
 * @created 8/12/21 4:22 PM
 */
@Component
@RequiredArgsConstructor
public class CustomCsrfTokenRepository implements CsrfTokenRepository {

    private final TokenService tokenService;
    private final CsrfConstants csrfConstants;
    private final UserCsrfTokenRepository userCsrfTokenRepository;

    @Override
    public CsrfToken generateToken(HttpServletRequest request) {
        String tokenString = UUID.randomUUID().toString();
        CsrfToken token = new DefaultCsrfToken(csrfConstants.getCsrfHeaderName(), csrfConstants.getCsrfParameterName(), tokenString);

        return token;
    }

    @Override
    public void saveToken(CsrfToken token, HttpServletRequest request, HttpServletResponse response) {
        Optional<String> jwtOpt = tokenService.getTokenFromRequest(request);
        if (!jwtOpt.isPresent()) {
            return;
        }
        String jwt = jwtOpt.get();
        UserCsrfToken userCsrfToken = new UserCsrfToken(jwt, token);
        userCsrfTokenRepository.save(userCsrfToken);
    }

    @Override
    public CsrfToken loadToken(HttpServletRequest request) {
        Optional<String> jwtOpt = tokenService.getTokenFromRequest(request);
        if (!jwtOpt.isPresent()) {
            return null;
        }
        String jwt = jwtOpt.get();

        Optional<UserCsrfToken> csrfOpt = userCsrfTokenRepository.findById(jwt);
        if (!csrfOpt.isPresent()) {
            return null;
        }

        UserCsrfToken userCsrfToken = csrfOpt.get();
        CsrfToken token = userCsrfToken.getToken();
        return token;
    }

}
