package com.upgrad.quora.api.controller;

import com.upgrad.quora.api.model.UserDetailsResponse;
import com.upgrad.quora.service.business.UserService;
import com.upgrad.quora.service.entity.UserEntity;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import com.upgrad.quora.service.exception.UserNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@RequestMapping("/userprofile")
public class CommonController {

    @Autowired
    private UserService userService;

    @RequestMapping(value = "/{userId}", method = RequestMethod.GET)
    public ResponseEntity<UserDetailsResponse> fetchUserDetails(@PathVariable final String userId, @RequestHeader final String authorization) throws AuthorizationFailedException, UserNotFoundException {
        String[] tokenData = authorization.split("Bearer ");
        if (tokenData.length == 2) {
            String accessToken = authorization.split("Bearer ")[1];
            userService.checkIfAccessTokenExists(accessToken);

            UserEntity user = userService.getUserByUuid(userId);
            if (user == null) {
                throw new UserNotFoundException("USR-001","User with entered uuid does not exist");
            } else {
                return new ResponseEntity<UserDetailsResponse>(new UserDetailsResponse().aboutMe(user.getAboutme()).
                        contactNumber(user.getContactnumber()).country(user.getCountry()).
                        dob(user.getDob()).emailAddress(user.getEmail()).firstName(user.getFirstname()).
                        lastName(user.getLastname()).userName(user.getUsername()), HttpStatus.OK);
            }
        }
        throw new AuthorizationFailedException("ATHR-001", "User has not signed in");
    }
}
