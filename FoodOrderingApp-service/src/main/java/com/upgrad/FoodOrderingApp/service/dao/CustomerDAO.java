package com.upgrad.FoodOrderingApp.service.dao;

import com.upgrad.FoodOrderingApp.service.entity.CustomerAuthEntity;
import com.upgrad.FoodOrderingApp.service.entity.CustomerEntity;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;

@Repository
public class CustomerDAO {

    @PersistenceContext
    private EntityManager entityManager;

    public CustomerEntity getUserByContact(String contact) {
        try {
            return entityManager
                    .createNamedQuery("customerByContact", CustomerEntity.class)
                    .setParameter("contact", contact)
                    .getSingleResult();
        } catch (NoResultException nre) {
            return null;
        }
    }

    public void createUser(CustomerEntity customerEntity) {
        entityManager.persist(customerEntity);
    }

    public void createAuthToken(CustomerAuthEntity customerAuthEntity) {
        entityManager.persist(customerAuthEntity);
    }

    public CustomerAuthEntity getUserAuthToken(String token) {

        try {
            return entityManager.createNamedQuery("customerAuthByAccessToken", CustomerAuthEntity.class).
                    setParameter("accessToken", token).getSingleResult();
        } catch (NoResultException e){
            return null;
        }

    }

    public void updateAuthToken(CustomerAuthEntity customerAuthEntity) {
        entityManager.merge(customerAuthEntity);
    }

    public void updateCustomer(CustomerEntity customerEntity) {
        entityManager.merge(customerEntity);
    }
}
