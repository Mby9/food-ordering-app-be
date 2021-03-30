package com.upgrad.FoodOrderingApp.service.dao;

import com.upgrad.FoodOrderingApp.service.entity.StateEntity;
import com.upgrad.FoodOrderingApp.service.exception.AddressNotFoundException;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import java.util.List;

public class StateDAO {

    @PersistenceContext
    EntityManager entityManager;

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

    public List<StateEntity> getAllStates() {
        return entityManager
                .createNamedQuery("getAllStates", StateEntity.class)
                .getResultList();
    }

    public StateEntity getStateByID(final Integer stateId) {
        try {
            return entityManager.createNamedQuery("stateById", StateEntity.class).setParameter("id", stateId)
                    .getSingleResult();
        } catch(NoResultException nre) {
            return null;
        }
    }

    public StateEntity getStateByUuid(final String stateUuid) {
        try {
            return entityManager.createNamedQuery("stateByUuid", StateEntity.class).setParameter("uuid", stateUuid)
                    .getSingleResult();
        } catch(NoResultException nre) {
            return null;
        }
    }
}
