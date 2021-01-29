package com.upgrad.quora.service.business;

import com.upgrad.quora.service.dao.AnswerDao;
import com.upgrad.quora.service.entity.AnswerEntity;
import com.upgrad.quora.service.entity.QuestionEntity;
import com.upgrad.quora.service.entity.UserEntity;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AnswerService {

    @Autowired
    private AnswerDao answerDao;

    @Autowired
    private QuestionService questionService;

    @Transactional(propagation = Propagation.REQUIRED)
    public AnswerEntity createAnswer(AnswerEntity answerEntity) {
        return answerDao.createAnswer(answerEntity);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public AnswerEntity editAnswer(AnswerEntity answerEntity) {
        return answerDao.updateAnswer(answerEntity);
    }

    public AnswerEntity getAnswer(String answerId) {
        return answerDao.getAnswer(answerId);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void deleteAnswer(AnswerEntity answer, UserEntity user) throws AuthorizationFailedException {
        if (answer.getUser() == user || !user.getRole().equals("nonadmin")) {
            answerDao.deleteAnswer(answer);
        } else {
            throw new AuthorizationFailedException("ATHR-003", "Only the answer owner or admin can delete the answer");
        }
    }

    public List<AnswerEntity> getAllAnswerForQuestion(QuestionEntity questionEntity) {
        return answerDao.getAnswersForQuestion(questionEntity);
    }
}
