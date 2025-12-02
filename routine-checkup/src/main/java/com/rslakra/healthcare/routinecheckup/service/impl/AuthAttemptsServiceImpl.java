package com.rslakra.healthcare.routinecheckup.service.impl;

import com.rslakra.healthcare.routinecheckup.entity.UserLoginAttempts;
import com.rslakra.healthcare.routinecheckup.entity.UserRegistrationAttempts;
import com.rslakra.healthcare.routinecheckup.repository.UserLoginAttemptsRepository;
import com.rslakra.healthcare.routinecheckup.repository.UserRegistrationAttemptsRepository;
import com.rslakra.healthcare.routinecheckup.service.AuthAttemptsService;
import com.rslakra.healthcare.routinecheckup.service.AuthAttemptType;
import com.rslakra.healthcare.routinecheckup.utils.components.holder.LoginAttemptsConstants;
import com.rslakra.healthcare.routinecheckup.utils.components.holder.RegistrationConstants;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Optional;

/**
 * Unified service implementation for handling all authentication attempt tracking
 *
 * @author Rohtash Lakra
 * @created 8/12/21 4:20 PM
 */
@Service
@RequiredArgsConstructor
public class AuthAttemptsServiceImpl implements AuthAttemptsService {

    private final UserLoginAttemptsRepository userLoginAttemptsRepository;
    private final UserRegistrationAttemptsRepository userRegistrationAttemptsRepository;
    private final LoginAttemptsConstants loginAttemptsConstants;
    private final RegistrationConstants registrationConstants;

    @Override
    public UserLoginAttempts commitNewLoginAttempt(String userIp) {
        return (UserLoginAttempts) commitNewAttempt(AuthAttemptType.LOGIN, userIp);
    }

    @Override
    public UserRegistrationAttempts commitNewRegistrationAttempt(String userIp) {
        return (UserRegistrationAttempts) commitNewAttempt(AuthAttemptType.REGISTRATION, userIp);
    }

    @Override
    public Object commitNewAttempt(AuthAttemptType attemptType, String userIp) {
        switch (attemptType) {
            case LOGIN:
                return commitLoginAttempt(userIp);
            case REGISTRATION:
                return commitRegistrationAttempt(userIp);
            default:
                throw new IllegalArgumentException("Unsupported attempt type: " + attemptType);
        }
    }

    @Override
    public boolean isExtraCurrentLogin(String userIp) {
        return isExtraAttempt(AuthAttemptType.LOGIN, userIp, true);
    }

    @Override
    public boolean isExtraCurrentRegistration(String userIp) {
        return isExtraAttempt(AuthAttemptType.REGISTRATION, userIp, true);
    }

    @Override
    public boolean isExtraLastRegistration(String userIp) {
        return isExtraAttempt(AuthAttemptType.REGISTRATION, userIp, false);
    }

    @Override
    public boolean isExtraAttempt(AuthAttemptType attemptType, String userIp, boolean commitNewAttempt) {
        switch (attemptType) {
            case LOGIN:
                UserLoginAttempts loginAttempts = commitNewAttempt 
                    ? commitLoginAttempt(userIp) 
                    : getLoginAttempts(userIp);
                return loginAttempts.getCurrentAttemptsCount() > loginAttemptsConstants.getMaxAttemptsCount();
            
            case REGISTRATION:
                UserRegistrationAttempts registrationAttempts = commitNewAttempt 
                    ? commitRegistrationAttempt(userIp) 
                    : getRegistrationAttempts(userIp);
                return registrationAttempts.getCurrentAttemptsCount() > registrationConstants.getMaxAttemptsCount();
            
            default:
                throw new IllegalArgumentException("Unsupported attempt type: " + attemptType);
        }
    }

    private UserLoginAttempts commitLoginAttempt(String userIp) {
        Optional<UserLoginAttempts> attemptsOptional = userLoginAttemptsRepository.findById(userIp);

        if (attemptsOptional.isPresent()) {
            UserLoginAttempts attempts = attemptsOptional.get();
            attempts.setCurrentAttemptsCount(getCurrentLoginAttemptCount(attempts));
            attempts.setLastAttemptDate(new Date());
            userLoginAttemptsRepository.save(attempts);
            return attempts;
        }

        UserLoginAttempts attempts = new UserLoginAttempts(userIp);
        return userLoginAttemptsRepository.save(attempts);
    }

    private UserRegistrationAttempts commitRegistrationAttempt(String userIp) {
        Optional<UserRegistrationAttempts> attemptsOptional = userRegistrationAttemptsRepository.findById(userIp);

        if (attemptsOptional.isPresent()) {
            UserRegistrationAttempts attempts = attemptsOptional.get();
            attempts.setCurrentAttemptsCount(getCurrentRegistrationAttemptCount(attempts));
            attempts.setLastAttemptDate(new Date());
            userRegistrationAttemptsRepository.save(attempts);
            return attempts;
        }

        UserRegistrationAttempts attempts = new UserRegistrationAttempts(userIp);
        return userRegistrationAttemptsRepository.save(attempts);
    }

    private UserLoginAttempts getLoginAttempts(String userIp) {
        Optional<UserLoginAttempts> attemptsOptional = userLoginAttemptsRepository.findById(userIp);
        return attemptsOptional.orElseGet(() -> {
            UserLoginAttempts newAttempt = new UserLoginAttempts(userIp);
            return userLoginAttemptsRepository.save(newAttempt);
        });
    }

    private UserRegistrationAttempts getRegistrationAttempts(String userIp) {
        Optional<UserRegistrationAttempts> attemptsOptional = userRegistrationAttemptsRepository.findById(userIp);
        return attemptsOptional.orElseGet(() -> {
            UserRegistrationAttempts newAttempt = new UserRegistrationAttempts(userIp);
            return userRegistrationAttemptsRepository.save(newAttempt);
        });
    }

    private Integer getCurrentLoginAttemptCount(UserLoginAttempts attempts) {
        Date lastAttemptDate = attempts.getLastAttemptDate();
        Date currentDate = new Date();
        Date expirationDate = new Date(
            lastAttemptDate.getTime() + loginAttemptsConstants.getMaxAllowableTimeSpanMS()
        );

        if (currentDate.after(expirationDate)) {
            return 1;
        } else {
            return attempts.getCurrentAttemptsCount() + 1;
        }
    }

    private Integer getCurrentRegistrationAttemptCount(UserRegistrationAttempts attempts) {
        Date lastAttemptDate = attempts.getLastAttemptDate();
        Date currentDate = new Date();
        Date expirationDate = new Date(
            lastAttemptDate.getTime() + registrationConstants.getMaxAllowableTimeSpanMS()
        );

        if (currentDate.after(expirationDate)) {
            return 1;
        } else {
            return attempts.getCurrentAttemptsCount() + 1;
        }
    }
}

