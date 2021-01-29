package com.upgrad.quora.service.dao;

import com.upgrad.quora.service.entity.QuestionEntity;
import com.upgrad.quora.service.entity.UserEntity;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceException;
import java.util.List;

@Repository
public class QuestionDao {

    @PersistenceContext
    private EntityManager entityManager;

    public QuestionEntity createQuestion(QuestionEntity question) {
        entityManager.persist(question);
        return question;
    }

    public List<QuestionEntity> getAllQuestions() {
        return entityManager.createNamedQuery("allQuestions").getResultList();
    }

    public QuestionEntity getQuestion(String questionId) {
        try {
            return entityManager.createNamedQuery("questionForUuid", QuestionEntity.class).setParameter("uuid", questionId).getSingleResult();
        } catch (PersistenceException pe) {
            return null;
        }
    }

    public QuestionEntity updateQuestion(QuestionEntity question) {
        entityManager.merge(question);
        return question;
    }

    public void deleteQuestion(QuestionEntity question) {
        entityManager.remove(question);
    }

    public List<QuestionEntity> getAllQuestionsForUser(UserEntity user) {
        try {
            return entityManager.createNamedQuery("questionForUserId", QuestionEntity.class).setParameter("userId", user).getResultList();
        } catch (PersistenceException pe) {
            return null;
        }
    }
}
