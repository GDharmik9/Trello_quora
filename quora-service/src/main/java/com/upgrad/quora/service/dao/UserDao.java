package com.upgrad.quora.service.dao;

import com.upgrad.quora.service.entity.UserAuthEntity;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;

@Repository
public class UserDao {

    @PersistenceContext
    private EntityManager entityManager;

    public UserAuthEntity getUserAuth(final String accessToken){
        try {
            return entityManager.createNamedQuery("userAuthByAccessToken", UserAuthEntity.class)
                    .setParameter("accessToken" ,accessToken).getSingleResult();
        }catch (NoResultException nre){
            return null;
        }
    }
}
