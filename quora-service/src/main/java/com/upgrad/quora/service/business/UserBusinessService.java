package com.upgrad.quora.service.business;

import com.upgrad.quora.service.dao.UserDao;
import com.upgrad.quora.service.entity.UserAuthEntity;
import com.upgrad.quora.service.entity.UserEntity;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import com.upgrad.quora.service.exception.SignUpRestrictedException;
import com.upgrad.quora.service.exception.UserNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.util.Base64;

@Service
public class UserBusinessService {

    @Autowired
    private UserDao userDao;

    @Autowired
    private PasswordCryptographyProvider passwordCryptographyProvider;

    @Transactional(propagation = Propagation.REQUIRED)
    public UserEntity signup(UserEntity userEntity) throws SignUpRestrictedException {

        if (userDao.getUserByUserName((userEntity.getUsername())) != null) {
            throw new SignUpRestrictedException("SGR-001", "Try any other Username, this Username has already been taken");
        } else if (userDao.getUserByEmail(userEntity.getEmail()) != null) {
            throw new SignUpRestrictedException("SGR-002", "This user has already been registered, try with any other emailId");
        }

        String[] encryptedText = passwordCryptographyProvider.encrypt(userEntity.getPassword());
        userEntity.setSalt(encryptedText[0]);
        userEntity.setPassword(encryptedText[1]);
        return userDao.createUser(userEntity);
    }

    public UserEntity getUserByUuid(String uuid, String authorization) throws AuthorizationFailedException, UserNotFoundException {
        UserEntity userEntity = null;
        if(authenticate(authorization))
        {
            userEntity = userDao.getUserByUuid(uuid);
            if (userEntity == null) {
                throw new UserNotFoundException("USR-001", "User with entered uuid does not exist");
            }
        }
        return userEntity;
    }

    public UserEntity getUserFromAuth(String authorization) throws AuthorizationFailedException {
        UserEntity userEntity = null;

        if(authenticate(authorization))
        {
            byte[] decode = Base64.getDecoder().decode(authorization.split("Basic ")[1]);
            String decodedText = new String(decode);
            String[] decodedArray = decodedText.split(":");
            userEntity = userDao.getUserByUserName(decodedArray[0]);
        }
        return userEntity;
    }

    public boolean authenticate(String authorization) throws AuthorizationFailedException
    {
        UserAuthEntity userAuthEntity = userDao.getUserAuth(authorization);
        if (userAuthEntity != null) {
            if (isUserSignedIn(userAuthEntity)) {
                return true;
            }
            throw new AuthorizationFailedException("ATHR-002", "User is signed out.Sign in first to get user details");
        }
        throw new AuthorizationFailedException("ATHR-001", "User has not signed in");
    }

    private boolean isUserSignedIn(UserAuthEntity userAuthEntity)
    {
        if (userAuthEntity.getExpiresAt().isAfter(ZonedDateTime.now()) && (userAuthEntity.getLogoutAt() == null
                || userAuthEntity.getLogoutAt().isAfter(ZonedDateTime.now())))
        {
            return true;
        }
        return false;
    }

}
