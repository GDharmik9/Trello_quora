package com.upgrad.quora.service.business;

import com.upgrad.quora.service.dao.UserDao;
import com.upgrad.quora.service.entity.UserAuthEntity;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserAuthBusinessService {

    @Autowired
    private UserDao userDao;

    public UserAuthEntity getUser(final String authorizationToken) throws AuthorizationFailedException {

        UserAuthEntity userAuthEntity = userDao.getUserAuth(authorizationToken);
        if (userAuthEntity == null){
            throw new AuthorizationFailedException("ATHR-001" , "User has not signed in");
        }else if (userAuthEntity.getLogoutAt()!=null){
            throw new AuthorizationFailedException("ATHR-002", "User is signed out.Sign in first to post a question");
        }else {
            return userAuthEntity;
        }
    }
}
