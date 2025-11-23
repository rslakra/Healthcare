package com.rslakra.healthcare.routinecheckup.repository;


import com.rslakra.healthcare.routinecheckup.entity.UserRegistrationAttempts;
import org.springframework.data.keyvalue.repository.KeyValueRepository;
import org.springframework.stereotype.Repository;

/**
 * @author Rohtash Lakra
 * @created 8/12/21 4:12 PM
 */
@Repository
public interface UserRegistrationAttemptsRepository extends KeyValueRepository<UserRegistrationAttempts, String> {

}

