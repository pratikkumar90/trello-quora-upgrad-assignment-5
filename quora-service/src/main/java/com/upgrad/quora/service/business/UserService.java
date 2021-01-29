package com.upgrad.quora.service.business;

import com.upgrad.quora.service.dao.UserDao;
import com.upgrad.quora.service.entity.UserAuthEntity;
import com.upgrad.quora.service.entity.UserEntity;
import com.upgrad.quora.service.exception.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.util.UUID;

@Service
public class UserService {

    @Autowired
    private UserDao userDao;

    @Transactional(propagation = Propagation.REQUIRED)
    public UserEntity signUpUser(UserEntity userEntity) throws SignUpRestrictedException {
        String userName = userEntity.getUsername();
        String email = userEntity.getEmail();
        UserEntity userByEmail = getUserByEmail(email);
        UserEntity userByUserName = getUserByUserName(userName);
        if (userByUserName != null) {
            throw new SignUpRestrictedException("SGR-001", "Try any other Username, this Username has already been taken");
        } else if (userByEmail != null) {
            throw new SignUpRestrictedException("SGR-002", "This user has already been registered, try with any other emailId");
        } else {
            createUser(userEntity);
            return userEntity;
        }
    }

    public UserEntity getUserByEmail(String email) {
        return userDao.getUserByEmail(email);
    }

    public UserEntity getUserByUuid(String userUuid) {
        return userDao.getUserByUuid(userUuid);
    }

    public UserEntity getUserByUserName(String username) {
        return userDao.getUserByUserName(username);
    }


    public UserEntity createUser(UserEntity user) {
        return userDao.createUser(user);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public UserAuthEntity authenticate(String username, String password) throws AuthenticationFailedException {
        UserEntity userByUserName = getUserByUserName(username);
        if (userByUserName == null) {
            throw new AuthenticationFailedException("ATH-001", "This username does not exist");
        } else {
            String encodedPassword = PasswordCryptographyProvider.encrypt(password, userByUserName.getSalt());
            if (encodedPassword.equals(userByUserName.getPassword())) {
                JwtTokenProvider jwtTokenProvider = new JwtTokenProvider(userByUserName.getPassword());

                UserAuthEntity userAuth = new UserAuthEntity();
                String accessToken = jwtTokenProvider.generateToken(userByUserName.getUuid(), ZonedDateTime.now(), ZonedDateTime.now());
                userAuth.setAccessToken(accessToken);
                userAuth.setUserId(userByUserName);
                userAuth.setExpiresAt(ZonedDateTime.now());
                userAuth.setLoginAt(ZonedDateTime.now());
                userAuth.setUuid(UUID.randomUUID().toString());

                userDao.createUserAuth(userAuth);
                return userAuth;

            } else {
                throw new AuthenticationFailedException("ATH-002", "Password failed");
            }
        }
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public UserAuthEntity signOutUser(String accessToken) throws SignOutRestrictedException {
        UserAuthEntity userAuthEntity = userDao.getUserAuthEntity(accessToken);
        if (userAuthEntity == null) {
            throw new SignOutRestrictedException("SGR-001", "User is not Signed in");
        } else {
            userAuthEntity.setLogoutAt(ZonedDateTime.now());
            return userDao.updateUserAuthEntity(userAuthEntity);
        }
    }

    public UserAuthEntity checkIfAccessTokenExists(String accessToken) throws AuthorizationFailedException {
        UserAuthEntity userAuthEntity = userDao.getUserAuthEntity(accessToken);
        if (userAuthEntity == null) {
            throw new AuthorizationFailedException("ATHR-001", "User has not signed in");
        } else {
            if (userAuthEntity.getLogoutAt() != null) {
                throw new AuthorizationFailedException("ATHR-002", "User is signed out.Sign in first to get user details");
            }
        }
        return userAuthEntity;
    }

    public boolean isAdminUser(UserAuthEntity userAuthEntity) throws AuthorizationFailedException {
        if (userAuthEntity.getUserId().getRole().equals("nonadmin")) {
            throw new AuthorizationFailedException("ATHR-003", "Unauthorized Access, Entered user is not an admin");
        } else {
            return true;
        }
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void deleteUser(UserAuthEntity userAuth, String userId) throws UserNotFoundException, AuthorizationFailedException {
        if (isAdminUser(userAuth)) {
            UserEntity user = getUserByUuid(userId);
            if (user == null) {
                throw new UserNotFoundException("USR-001", "User with entered uuid to be deleted does not exist");
            } else {
                userDao.deleteUser(user);
            }
        }
    }
}
