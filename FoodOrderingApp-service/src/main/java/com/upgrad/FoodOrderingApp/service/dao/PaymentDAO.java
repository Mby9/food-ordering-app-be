package com.upgrad.FoodOrderingApp.service.dao;

import com.upgrad.FoodOrderingApp.service.entity.PaymentEntity;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import java.util.List;

@Repository
public class PaymentDAO {
    @PersistenceContext
    private EntityManager entityManager;


    //To get Payment Methods from the db
    public List<PaymentEntity> getPaymentMethods() {
        try {
            return entityManager.createNamedQuery("getAllPaymentMethods", PaymentEntity.class).getResultList();
        } catch (NoResultException nre) {
            return null;
        }
    }

    //To get Payment By UUID from the db
    public PaymentEntity getPaymentMethodByUUID(final String paymentUuid) {
        try {
            return entityManager.createNamedQuery("getPaymentMethodByUuid", PaymentEntity.class)
                    .setParameter("paymentUuid", paymentUuid).getSingleResult();
        } catch (NoResultException nre) {
            return null;
        }
    }
}