package com.upgrad.quora.service.dao;

import com.upgrad.quora.service.entity.UserAuthEntity;
import com.upgrad.quora.service.entity.UserEntity;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;

@Repository
public class UserDao {

    @PersistenceContext
    private EntityManager entityManager;

    public UserEntity createUser(UserEntity userEntity) {
        entityManager.persist(userEntity);
        return userEntity;
    }


    public UserAuthEntity createAuthToken(final UserAuthEntity userAuthEntity){
        entityManager.persist(userAuthEntity);
        return userAuthEntity;
    }

    public UserEntity getUserByEmail(final String userEmail) {
        try {
            return entityManager.createNamedQuery("userByEmail", UserEntity.class).setParameter("email", userEmail)
                    .getSingleResult();
        } catch(NoResultException nre) {
            return null;
        }
    }

    public void deleteUser(final UserEntity userEntity) {
        entityManager.remove(userEntity);
    }

    public UserEntity getUserByUserName(final String useName) {
        try {
            return entityManager.createNamedQuery("userByName", UserEntity.class).setParameter("username", useName)
                    .getSingleResult();
        } catch(NoResultException nre) {
            return null;
        }
    }

    public UserEntity getUserByUuid(final String uuid) {
        try {
            return entityManager.createNamedQuery("userByUuid", UserEntity.class).setParameter("uuid",uuid).getSingleResult();
        } catch(NoResultException nre) {
            return null;
        }
    }

    public UserAuthEntity getUserAuth(final String accessToken){
        try {
            return entityManager.createNamedQuery("userAuthByAccessToken", UserAuthEntity.class).setParameter("accessToken" ,accessToken).getSingleResult();
        }
        catch (NoResultException nre){
            return null;
        }
    }

    public UserAuthEntity setUserLogout(final UserAuthEntity userAuthEntity){
        entityManager.persist(userAuthEntity);
        return userAuthEntity;
    }

    public void updateUser(final UserEntity updatedUserEntity){
        entityManager.merge(updatedUserEntity);
    }

    public  void updateUserAuth(final  UserEntity updatedUserAuth){
        entityManager.merge(updatedUserAuth);
    }


}
