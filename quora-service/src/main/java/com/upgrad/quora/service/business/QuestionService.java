package com.upgrad.quora.service.business;

import com.upgrad.quora.service.dao.QuestionDao;
import com.upgrad.quora.service.dao.UserDao;
import com.upgrad.quora.service.entity.QuestionEntity;
import com.upgrad.quora.service.entity.UserEntity;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import com.upgrad.quora.service.exception.InvalidQuestionException;
import com.upgrad.quora.service.exception.UserNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class QuestionService {

    @Autowired
    private QuestionDao questionDao;

    @Autowired
    private UserDao userDao;

    @Transactional(propagation = Propagation.REQUIRED)
    public QuestionEntity createQuestion(QuestionEntity question) {
        questionDao.createQuestion(question);
        return question;
    }

    public List<QuestionEntity> getAllQuestions() {
        return questionDao.getAllQuestions();
    }

    public QuestionEntity getQuestion(String questionId) {
        return questionDao.getQuestion(questionId);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public QuestionEntity editQuestion(String questionId, String userId, String content) throws InvalidQuestionException, AuthorizationFailedException {
        QuestionEntity question = getQuestion(questionId);
        if (question == null) {
            throw new InvalidQuestionException("QUES-001", "Entered question uuid does not exist");
        } else if (question.getUserId().getUuid() != userId) {
            throw new AuthorizationFailedException("ATHR-003", "Only the question owner can edit the question");
        } else {
            question.setContent(content);
            questionDao.updateQuestion(question);
            return question;
        }
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void deleteQuestion(String questionId, UserEntity user) throws InvalidQuestionException, AuthorizationFailedException {
        QuestionEntity question = getQuestion(questionId);

        if (question == null) {
            throw new InvalidQuestionException("QUES-001", "Entered question uuid does not exist");
        }

        if (question.getUserId() == user || !user.getRole().equals("nonadmin")) {
            questionDao.deleteQuestion(question);
        } else {
            throw new AuthorizationFailedException("ATHR-003", "Only the question owner or admin can delete the question");
        }
    }

    public List<QuestionEntity> getAllQuestionsForUser(String userId) throws UserNotFoundException {
        UserEntity user = userDao.getUserByUuid(userId);
        if (user == null) {
            throw new UserNotFoundException("USR-001", "User with entered uuid whose question details are to be seen does not exist");
        } else {
            return questionDao.getAllQuestionsForUser(user);
        }
    }

}
