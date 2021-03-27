package com.upgrad.FoodOrderingApp.service.dao;

import com.upgrad.FoodOrderingApp.service.entity.AddressEntity;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;

@Repository
public class AddressDAO {

    @PersistenceContext
    EntityManager entityManager;

    public void save(AddressEntity address) {
        entityManager.persist(address);
    }

    public AddressEntity getAddressByUUID(String id) {
        try {
            return entityManager
                    .createNamedQuery("addressbyUUID", AddressEntity.class)
                    .setParameter("uuid", id)
                    .getSingleResult();
        } catch (NoResultException nre) {
            return null;
        }
    }

}
