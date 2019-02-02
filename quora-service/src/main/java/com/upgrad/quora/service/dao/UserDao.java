package com.upgrad.quora.service.dao;

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

    public UserEntity getUserByEmail(final String userEmail) {
        try {
            return entityManager.createNamedQuery("userByEmail", UserEntity.class).setParameter("email", userEmail)
                    .getSingleResult();
        } catch(NoResultException nre) {
            return null;
        }
    }

    public UserEntity getUserByName(final String userName) {
        try {
            return entityManager.createNamedQuery("userByName", UserEntity.class).setParameter("username", userName)
                    .getSingleResult();
        } catch(NoResultException nre) {
            return null;
        }
    }
}
