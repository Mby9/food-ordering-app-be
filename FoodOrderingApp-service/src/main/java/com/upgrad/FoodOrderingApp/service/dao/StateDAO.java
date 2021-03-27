package com.upgrad.FoodOrderingApp.service.dao;

import com.upgrad.FoodOrderingApp.service.entity.StateEntity;
import com.upgrad.FoodOrderingApp.service.exception.AddressNotFoundException;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;

public class StateDAO {

    @PersistenceContext
    EntityManager entityManager;

    /**
     * Get the question for the given id.
     *
     * @param questionId id of the required question.
     * @return QuestionEntity if question with given id is found else null.
     */
    public StateEntity getStateById(final String questionId) throws AddressNotFoundException {
        try {
            return entityManager
                    .createNamedQuery("getStateById", StateEntity.class)
                    .setParameter("uuid", questionId)
                    .getSingleResult();
        } catch (NoResultException nre) {
            throw new AddressNotFoundException("ANF-002", "No state by this id");
        }
    }
}
