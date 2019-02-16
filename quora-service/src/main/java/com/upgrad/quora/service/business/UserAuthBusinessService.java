package com.upgrad.quora.service.business;

import com.upgrad.quora.service.dao.UserDao;
import com.upgrad.quora.service.entity.UserAuthEntity;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import com.upgrad.quora.service.exception.SignOutRestrictedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;


@Service
public class UserAuthBusinessService {

    @Autowired
    private UserDao userDao;


    @Transactional
    public UserAuthEntity getUser(final String authorizationToken) throws AuthorizationFailedException {

/*
        String[] bearerToken = authorizationToken.split("Bearer ");
*/
        UserAuthEntity userAuthEntity = userDao.getUserAuth(authorizationToken);
        if (userAuthEntity == null) {
            throw new AuthorizationFailedException("ATHR-001", "User has not signed in");
        }
        return userAuthEntity;
    }

    @Transactional
    public UserAuthEntity getUserSignOut(final String authorizationToken) throws SignOutRestrictedException  {

        UserAuthEntity userAuthToken = userDao.getUserAuth(authorizationToken);
        if (userAuthToken == null){
            throw new SignOutRestrictedException("SGR-001" , "User is not Signed in");
        }
        else if (userAuthToken.getLogoutAt()!= null){
            throw new SignOutRestrictedException("SGR-002" , "User is already SignOut");
        }

        final ZonedDateTime now = ZonedDateTime.now();
        userAuthToken.setLogoutAt(now);
        userDao.setUserLogout(userAuthToken);
            /*UserEntity userEntity = userAuthToken.getUser();
            userDao.updateUserAuth(userEntity);
            */
        return userAuthToken;
    }




}

