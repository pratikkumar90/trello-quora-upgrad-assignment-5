package com.upgrad.quora.api.controller;

import com.upgrad.quora.api.model.*;
import com.upgrad.quora.service.business.QuestionService;
import com.upgrad.quora.service.business.UserService;
import com.upgrad.quora.service.entity.QuestionEntity;
import com.upgrad.quora.service.entity.UserAuthEntity;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import com.upgrad.quora.service.exception.InvalidQuestionException;
import com.upgrad.quora.service.exception.UserNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Controller
@RequestMapping("/question")
public class QuestionController {

    @Autowired
    private UserService userService;

    @Autowired
    private QuestionService questionService;

    @RequestMapping(value = "/create", method = RequestMethod.POST)
    public ResponseEntity<QuestionResponse> createQuestion(@RequestBody QuestionRequest questionRequest, @RequestHeader String authorization) throws AuthorizationFailedException {
        String[] tokenData = authorization.split("Bearer ");
        if (tokenData.length == 2) {
            String accessToken = authorization.split("Bearer ")[1];
            UserAuthEntity userAuth = userService.checkIfAccessTokenExists(accessToken);

            QuestionEntity question = new QuestionEntity();
            question.setDate(ZonedDateTime.now());
            question.setUserId(userAuth.getUserId());
            question.setUuid(UUID.randomUUID().toString());
            question.setContent(questionRequest.getContent());

            questionService.createQuestion(question);
            return new ResponseEntity<QuestionResponse>(new QuestionResponse().id(question.getUuid()).status("QUESTION CREATED"), HttpStatus.CREATED);
        }
        throw new AuthorizationFailedException("ATHR-001", "User has not signed in");
    }

    @RequestMapping(value = "/all", method = RequestMethod.GET)
    public ResponseEntity<List<QuestionDetailsResponse>> getAllQuestions(@RequestHeader String authorization) throws AuthorizationFailedException {
        String[] tokenData = authorization.split("Bearer ");
        if (tokenData.length == 2) {
            String accessToken = authorization.split("Bearer ")[1];
            UserAuthEntity userAuth = userService.checkIfAccessTokenExists(accessToken);
            List<QuestionEntity> questions = questionService.getAllQuestions();

            List<QuestionDetailsResponse> response = new ArrayList<>();

            questions.forEach(i -> response.add(new QuestionDetailsResponse().content(i.getContent()).id(i.getUuid())));

            return new ResponseEntity<List<QuestionDetailsResponse>>(response, HttpStatus.OK);
        }
        throw new AuthorizationFailedException("ATHR-001", "User has not signed in");
    }

    @RequestMapping(value = "/edit/{questionId}", method = RequestMethod.PUT)
    public ResponseEntity<QuestionEditResponse> editQuestion(@PathVariable final String questionId, @RequestBody final QuestionEditRequest questionEditRequest, @RequestHeader final String authorization) throws AuthorizationFailedException, InvalidQuestionException {
        String[] tokenData = authorization.split("Bearer ");
        if (tokenData.length == 2) {
            String accessToken = authorization.split("Bearer ")[1];
            UserAuthEntity userAuth = userService.checkIfAccessTokenExists(accessToken);
            questionService.editQuestion(questionId, userAuth.getUserId().getUuid(), questionEditRequest.getContent());

            return new ResponseEntity<QuestionEditResponse>(new QuestionEditResponse().status("QUESTION EDITED").id(questionId), HttpStatus.ACCEPTED);
        }
        throw new AuthorizationFailedException("ATHR-001", "User has not signed in");
    }

    @RequestMapping(value = "/delete/{questionId}", method = RequestMethod.DELETE)
    public ResponseEntity<QuestionDeleteResponse> deleteQuestion(@PathVariable String questionId, @RequestHeader String authorization) throws AuthorizationFailedException, InvalidQuestionException {
        String[] tokenData = authorization.split("Bearer ");
        if (tokenData.length == 2) {
            String accessToken = authorization.split("Bearer ")[1];
            UserAuthEntity userAuth = userService.checkIfAccessTokenExists(accessToken);
            questionService.deleteQuestion(questionId, userAuth.getUserId());
            return new ResponseEntity<>(new QuestionDeleteResponse().id(questionId).status("QUESTION DELETED"), HttpStatus.OK);
        }
        throw new AuthorizationFailedException("ATHR-001", "User has not signed in");
    }

    @RequestMapping(method = RequestMethod.GET, value = "/all/{userId}")
    public ResponseEntity<List<QuestionDetailsResponse>> getAllQuestionsForUser(@PathVariable String userId, @RequestHeader String authorization) throws AuthorizationFailedException, UserNotFoundException {
        String[] tokenData = authorization.split("Bearer ");
        if (tokenData.length == 2) {
            String accessToken = authorization.split("Bearer ")[1];
            UserAuthEntity userAuth = userService.checkIfAccessTokenExists(accessToken);
            List<QuestionEntity> questions = questionService.getAllQuestionsForUser(userId);

            List<QuestionDetailsResponse> response = new ArrayList<>();
            questions.forEach(i -> response.add(new QuestionDetailsResponse().content(i.getContent()).id(i.getUuid())));

            return new ResponseEntity<List<QuestionDetailsResponse>>(response, HttpStatus.OK);
        }
        throw new AuthorizationFailedException("ATHR-001", "User has not signed in");
    }

}
