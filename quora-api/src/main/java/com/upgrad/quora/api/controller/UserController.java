package com.upgrad.quora.api.controller;

import com.upgrad.quora.api.model.SigninResponse;
import com.upgrad.quora.api.model.SignoutResponse;
import com.upgrad.quora.api.model.SignupUserRequest;
import com.upgrad.quora.api.model.SignupUserResponse;
import com.upgrad.quora.service.business.PasswordCryptographyProvider;
import com.upgrad.quora.service.business.UserService;
import com.upgrad.quora.service.entity.UserAuthEntity;
import com.upgrad.quora.service.entity.UserEntity;
import com.upgrad.quora.service.exception.AuthenticationFailedException;
import com.upgrad.quora.service.exception.SignOutRestrictedException;
import com.upgrad.quora.service.exception.SignUpRestrictedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.Base64;
import java.util.UUID;

@Controller
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private PasswordCryptographyProvider passwordCryptographyProvider;

    @RequestMapping(value = "/signup", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<SignupUserResponse> signupUser(@RequestBody SignupUserRequest signupUserRequest) throws SignUpRestrictedException {
        UserEntity userEntity = createUserEntity(signupUserRequest);
        UserEntity processedUserEntity = userService.signUpUser(userEntity);
        SignupUserResponse response = createSignUpResponse(userEntity);
        return new ResponseEntity<SignupUserResponse>(response, HttpStatus.CREATED);
    }

    @RequestMapping(value = "/signin", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<SigninResponse> signInUser(@RequestHeader final String authorization) throws AuthenticationFailedException {
        byte[] authData = Base64.getDecoder().decode(authorization.split("Basic ")[1]);
        String decodedAuthData = new String(authData);

        String username = decodedAuthData.split(":")[0];
        String password = decodedAuthData.split(":")[1];

        UserAuthEntity userAuth = userService.authenticate(username, password);
        SigninResponse signInResponse = new SigninResponse().id(userAuth.getUserId().getUuid()).message("SIGNED IN SUCCESSFULLY");

        HttpHeaders headers = new HttpHeaders();
        headers.add("access_token", "Bearer " + userAuth.getAccessToken());

        return new ResponseEntity<>(signInResponse, headers, HttpStatus.OK);
    }

    @RequestMapping(value = "/signout", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<SignoutResponse> signOutUser(@RequestHeader final String authorization) throws SignOutRestrictedException {
        String[] tokenData = authorization.split("Bearer ");
        if(tokenData.length == 2){
            String authToken = authorization.split("Bearer ")[1];
            UserAuthEntity userAuthEntity = userService.signOutUser(authToken);
            return new ResponseEntity<>(new SignoutResponse().id(userAuthEntity.getUserId().getUuid()).message("SIGNED OUT SUCCESSFULLY"), HttpStatus.OK);
        } else {
            throw new SignOutRestrictedException("SGR-001", "User is not Signed in");
        }
    }


    private SignupUserResponse createSignUpResponse(UserEntity userEntity) {
        SignupUserResponse response = new SignupUserResponse();
        response.setId(userEntity.getUuid());
        response.setStatus("USER SUCCESSFULLY REGISTERED");
        return response;
    }

    private UserEntity createUserEntity(SignupUserRequest signupUserRequest) {
        UserEntity userEntity = new UserEntity();
        userEntity.setAboutme(signupUserRequest.getAboutMe());
        userEntity.setContactnumber(signupUserRequest.getContactNumber());
        userEntity.setCountry(signupUserRequest.getCountry());
        userEntity.setDob(signupUserRequest.getDob());
        userEntity.setEmail(signupUserRequest.getEmailAddress());
        userEntity.setFirstname(signupUserRequest.getFirstName());
        userEntity.setLastname(signupUserRequest.getLastName());

        String[] encodedPassword = passwordCryptographyProvider.encrypt(signupUserRequest.getPassword());
        userEntity.setPassword(encodedPassword[1]);
        userEntity.setSalt(encodedPassword[0]);

        userEntity.setUsername(signupUserRequest.getUserName());
        userEntity.setUuid(UUID.randomUUID().toString());

        userEntity.setRole("nonadmin");
        return userEntity;
    }
}
