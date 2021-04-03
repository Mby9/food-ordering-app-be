package com.upgrad.FoodOrderingApp.service.dao;


import org.springframework.stereotype.Repository;

import com.upgrad.FoodOrderingApp.service.entity.CustomerEntity;
import com.upgrad.FoodOrderingApp.service.entity.CustomerAuthEntity;

import javax.persistence.PersistenceContext;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import java.time.ZonedDateTime;

@Repository
public class CustomerDAO {
    @PersistenceContext
    private EntityManager entityManager;

    public CustomerEntity getUserByContactNumber(String contactNumber) {
        try {
            return this.entityManager.createNamedQuery("getUserByContactNumber", CustomerEntity.class)
                    .setParameter("contactNumber", contactNumber).getSingleResult();
        } catch (NoResultException nRE) {
            return null;
        }
    }

    public CustomerEntity getCustomerEntityById(int customerId) {
        try {
            return this.entityManager.createNamedQuery("getUserByCustomerId", CustomerEntity.class)
                    .setParameter("id", customerId).getSingleResult();
        } catch (NoResultException nRE) {
            return null;
        }
    }

    public CustomerAuthEntity getCustomerEntityByAccessToken(String jwt) {
        System.out.println(">_ checking to see if customer auth entity is there or not by token id...");
        try {
            return this.entityManager.createNamedQuery("getEntityByToken", CustomerAuthEntity.class)
                    .setParameter("accessToken", jwt).getSingleResult();
        } catch (NoResultException nRE) {
            return null;
        }
    }

    public void registerNewCustomer(CustomerEntity newCustomer) {
        this.entityManager.persist(newCustomer);
    }

    public CustomerAuthEntity registerLoginSession(CustomerEntity customer, String jwt, ZonedDateTime now, ZonedDateTime expiresAt) {
        CustomerAuthEntity authEntity = new CustomerAuthEntity();
        authEntity.setExpiresAt(expiresAt);
        authEntity.setCustomer(customer);
        authEntity.setLoginAt(now);
        authEntity.setAccessToken(jwt);
        authEntity.setUuid(customer.getUuid());
        this.entityManager.persist(authEntity);
        return authEntity;
    }

    public CustomerEntity getCustomerByUuid(String customerUuid) {
        try {
            CustomerEntity customer =
                    entityManager.createNamedQuery("getUserByCustomerUUID", CustomerEntity.class).setParameter("customerId", customerUuid).getSingleResult();
            return customer;
        } catch (NoResultException nre) {
            return null;
        }
    }

    /* updates the customer auth entity in the 'custsomer_auth' table */
    public void updateCustomerAuthEntity(CustomerAuthEntity authEntity) {
        entityManager.merge(authEntity);
    }

    /* updates the customer entity in the 'customer' table */
    public void updateCustomerEntity(CustomerEntity customer) {
        entityManager.merge(customer);
    }
}
