package com.upgrad.quora.api.controller;

import com.upgrad.quora.api.model.*;
import com.upgrad.quora.service.business.AnswerService;
import com.upgrad.quora.service.business.QuestionService;
import com.upgrad.quora.service.business.UserService;
import com.upgrad.quora.service.entity.AnswerEntity;
import com.upgrad.quora.service.entity.QuestionEntity;
import com.upgrad.quora.service.entity.UserAuthEntity;
import com.upgrad.quora.service.exception.AnswerNotFoundException;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import com.upgrad.quora.service.exception.InvalidQuestionException;
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
public class AnswerController {

    @Autowired
    private UserService userService;

    @Autowired
    private QuestionService questionService;

    @Autowired
    private AnswerService answerService;

    @RequestMapping(value = "/question/{questionId}/answer/create", method = RequestMethod.POST)
    public ResponseEntity<AnswerResponse> createAnswer(@RequestBody AnswerRequest answerRequest, @RequestHeader String authorization, @PathVariable String questionId) throws AuthorizationFailedException, InvalidQuestionException {
        String[] tokenData = authorization.split("Bearer ");
        if (tokenData.length == 2) {
            String accessToken = authorization.split("Bearer ")[1];
            UserAuthEntity userAuth = userService.checkIfAccessTokenExists(accessToken);
            QuestionEntity question = questionService.getQuestion(questionId);
            if (question == null) {
                throw new InvalidQuestionException("QUES-001", "The question entered is invalid");
            } else {
                AnswerEntity answerEntity = new AnswerEntity();
                answerEntity.setAnswer(answerRequest.getAnswer());
                answerEntity.setDate(ZonedDateTime.now());
                answerEntity.setUuid(UUID.randomUUID().toString());
                answerEntity.setQuestion(question);
                answerEntity.setUser(userAuth.getUserId());
                answerService.createAnswer(answerEntity);

                return new ResponseEntity<AnswerResponse>(new AnswerResponse().id(answerEntity.getUuid()).status("ANSWER CREATED"), HttpStatus.CREATED);
            }
        }
        throw new AuthorizationFailedException("ATHR-001", "User has not signed in");
    }

    @RequestMapping(value = "/answer/edit/{answerId}", method = RequestMethod.PUT)
    public ResponseEntity<AnswerEditResponse> editAnswer(@RequestBody AnswerEditRequest answerEditRequest, @RequestHeader String authorization, @PathVariable String answerId) throws AuthorizationFailedException, InvalidQuestionException, AnswerNotFoundException {
        String[] tokenData = authorization.split("Bearer ");
        if (tokenData.length == 2) {
            String accessToken = authorization.split("Bearer ")[1];
            UserAuthEntity userAuth = userService.checkIfAccessTokenExists(accessToken);
            AnswerEntity answerEntity = answerService.getAnswer(answerId);
            if (answerEntity == null) {
                throw new AnswerNotFoundException("ANS-001", "Entered answer uuid does not exist");
            } else {
                if (!answerEntity.getUser().getUuid().equals(userAuth.getUserId().getUuid())) {
                    throw new AuthorizationFailedException("ATHR-003", "Only the answer owner can edit the answer");
                } else {
                    answerEntity.setAnswer(answerEditRequest.getContent());
                    answerService.editAnswer(answerEntity);
                    return new ResponseEntity<AnswerEditResponse>(new AnswerEditResponse().id(answerId).status("ANSWER EDITED"), HttpStatus.OK);
                }
            }
        }
        throw new AuthorizationFailedException("ATHR-001", "User has not signed in");
    }

    @RequestMapping(value = "/answer/delete/{answerId}", method = RequestMethod.DELETE)
    public ResponseEntity<AnswerDeleteResponse> deleteAnswer(@RequestHeader String authorization, @PathVariable String answerId) throws AuthorizationFailedException, InvalidQuestionException, AnswerNotFoundException {
        String[] tokenData = authorization.split("Bearer ");
        if (tokenData.length == 2) {
            String accessToken = authorization.split("Bearer ")[1];
            UserAuthEntity userAuth = userService.checkIfAccessTokenExists(accessToken);
            AnswerEntity answerEntity = answerService.getAnswer(answerId);
            if (answerEntity == null) {
                throw new AnswerNotFoundException("ANS-001", "Entered answer uuid does not exist");
            } else {
                answerService.deleteAnswer(answerEntity, userAuth.getUserId());

                return new ResponseEntity<AnswerDeleteResponse>(new AnswerDeleteResponse().status("ANSWER DELETED").id(answerId), HttpStatus.OK);
            }
        }
        throw new AuthorizationFailedException("ATHR-001", "User has not signed in");
    }

    @RequestMapping(value = "/answer/all/{questionId}", method = RequestMethod.GET)
    public ResponseEntity<List<AnswerDetailsResponse>> getAnswer(@RequestHeader String authorization, @PathVariable String questionId) throws AuthorizationFailedException, InvalidQuestionException, AnswerNotFoundException {
        String[] tokenData = authorization.split("Bearer ");
        if (tokenData.length == 2) {
            String accessToken = authorization.split("Bearer ")[1];
            UserAuthEntity userAuth = userService.checkIfAccessTokenExists(accessToken);
            QuestionEntity questionEntity = questionService.getQuestion(questionId);

            if (questionEntity == null) {
                throw new InvalidQuestionException("QUES-001", "The question with entered uuid whose details are to be seen does not exist");
            } else {
                List<AnswerEntity> answers = answerService.getAllAnswerForQuestion(questionEntity);
                List<AnswerDetailsResponse> response = new ArrayList<>();
                answers.forEach(i -> response.add(new AnswerDetailsResponse().answerContent(i.getAnswer()).id(i.getUuid())));
                return new ResponseEntity<List<AnswerDetailsResponse>>(response, HttpStatus.OK);
            }
        }
        throw new AuthorizationFailedException("ATHR-001", "User has not signed in");
    }
}
