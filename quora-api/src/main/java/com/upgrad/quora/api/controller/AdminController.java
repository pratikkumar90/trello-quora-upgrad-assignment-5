package com.upgrad.quora.api.controller;

import com.upgrad.quora.api.model.UserDeleteResponse;
import com.upgrad.quora.service.business.UserService;
import com.upgrad.quora.service.entity.UserAuthEntity;
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
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private UserService userService;

    @RequestMapping(value = "/user/{userId}", method = RequestMethod.DELETE)
    public ResponseEntity<UserDeleteResponse> deleteUser(@PathVariable final String userId, @RequestHeader final String authorization)
            throws AuthorizationFailedException, UserNotFoundException {
        String[] tokenData = authorization.split("Bearer ");
        if (tokenData.length == 2) {
            String accessToken = authorization.split("Bearer ")[1];
            UserAuthEntity userAuth = userService.checkIfAccessTokenExists(accessToken);
            userService.deleteUser(userAuth, userId);
            return new ResponseEntity<UserDeleteResponse>(new UserDeleteResponse().id("userId").status("USER SUCCESSFULLY DELETED"), HttpStatus.OK);
        }
        throw new AuthorizationFailedException("ATHR-001", "User has not signed in");
    }

}
