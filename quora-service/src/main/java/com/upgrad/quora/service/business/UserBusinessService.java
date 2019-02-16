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

    //this method takes uuid and userAuthEntity of user to get the Details of user
    public UserEntity userProfile(String uuid , UserAuthEntity userAuthEntity) throws AuthorizationFailedException, UserNotFoundException {

        UserEntity userEntity = userDao.getUserByUuid(uuid);
        if (userAuthEntity.getLogoutAt()!=null) {
            throw new AuthorizationFailedException("ATHR-002", "User is signed out.Sign in first to get user details");
        }else if (userEntity == null) {
            throw new UserNotFoundException("USR-001", "User with entered uuid does not exist");
        }
        return userEntity;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public UserEntity deleteUser(String uuid, String authorization) throws UserNotFoundException, AuthorizationFailedException {
        UserAuthEntity userAuthEntity = userDao.getUserAuth(authorization);
        if (userAuthEntity != null) {
            if ((userAuthEntity.getLogoutAt() == null)) {
                UserEntity userEntity = userDao.getUserByUuid(uuid);
                if (userEntity == null) {
                    throw new UserNotFoundException("USR-001", "User with entered uuid does not exist");
                }
                String role = userEntity.getRole();
                if(role.equalsIgnoreCase("admin"))
                {
                    userDao.deleteUser(userEntity);
                }
                throw new AuthorizationFailedException("ATHR-003", "Unauthorized Access, Entered user is not an admin");
            }
            throw new AuthorizationFailedException("ATHR-002", "User is signed out");
        }
        throw new AuthorizationFailedException("ATHR-001", "User has not signed in");


    }


}
