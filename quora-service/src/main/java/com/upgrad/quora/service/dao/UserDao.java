package com.upgrad.quora.service.dao;

import com.upgrad.quora.service.entity.UserAuthEntity;
import com.upgrad.quora.service.entity.UserEntity;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceException;

@Repository
public class UserDao {

    @PersistenceContext
    private EntityManager entityManager;

    public UserEntity getUserByEmail(final String email) {
        try {
            return entityManager.createNamedQuery("userByEmail", UserEntity.class).setParameter("email", email).getSingleResult();
        } catch (PersistenceException pe) {
            return null;
        }
    }

    public UserEntity getUserByUuid(final String userUuid) {
        try {
            return entityManager.createNamedQuery("userByUuid", UserEntity.class).setParameter("uuid", userUuid).getSingleResult();
        } catch (PersistenceException pe) {
            return null;
        }
    }

    public UserEntity getUserByUserName(final String userName) {
        try {
            return entityManager.createNamedQuery("userByUserName", UserEntity.class).setParameter("username", userName).getSingleResult();
        } catch (PersistenceException pe) {
            return null;
        }
    }

    public UserEntity createUser(UserEntity user) {
        entityManager.persist(user);
        return user;
    }

    public UserAuthEntity createUserAuth(UserAuthEntity userAuth) {
        entityManager.persist(userAuth);
        return userAuth;
    }

    public UserAuthEntity getUserAuthEntity(String accessToken) {
        try {
            return entityManager.createNamedQuery("userAuthEntityByAccessToken", UserAuthEntity.class).setParameter("accessToken", accessToken).getSingleResult();
        } catch (PersistenceException pe) {
            return null;
        }
    }

    public UserAuthEntity updateUserAuthEntity(UserAuthEntity userAuthEntity) {
        entityManager.merge(userAuthEntity);
        return userAuthEntity;
    }

    public void deleteUser(UserEntity userEntity) {
        entityManager.remove(userEntity);
    }
}
