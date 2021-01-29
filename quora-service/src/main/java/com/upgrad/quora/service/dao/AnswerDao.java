package com.upgrad.quora.service.dao;

import com.upgrad.quora.service.entity.AnswerEntity;
import com.upgrad.quora.service.entity.QuestionEntity;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceException;
import java.util.List;

@Repository
public class AnswerDao {

    @PersistenceContext
    private EntityManager entityManager;

    public AnswerEntity createAnswer(AnswerEntity answerEntity) {
        entityManager.persist(answerEntity);
        return answerEntity;
    }

    public AnswerEntity getAnswer(String answedId) {
        try {
            return entityManager.createNamedQuery("answerForUuid", AnswerEntity.class).setParameter("uuid", answedId).getSingleResult();
        } catch (PersistenceException pe) {
            return null;
        }
    }

    public List<AnswerEntity> getAnswersForQuestion(QuestionEntity questionEntity) {
        try {
            return entityManager.createNamedQuery("answersForQuestion", AnswerEntity.class).setParameter("question", questionEntity).getResultList();
        } catch (PersistenceException pe) {
            return null;
        }
    }

    public AnswerEntity updateAnswer(AnswerEntity answerEntity) {
        entityManager.merge(answerEntity);
        return answerEntity;
    }

    public void deleteAnswer(AnswerEntity answerEntity) {
        entityManager.remove(answerEntity);
    }
}
