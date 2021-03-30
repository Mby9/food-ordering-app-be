package com.upgrad.FoodOrderingApp.service.businness;

import com.upgrad.FoodOrderingApp.service.dao.PaymentDAO;
import com.upgrad.FoodOrderingApp.service.entity.PaymentEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@Service
public class PaymentService {

    @Autowired
    PaymentDAO paymentDAO;

    public List<PaymentEntity> getPayments() {

        return paymentDAO.getPayments();

    }
    @Transactional
    public PaymentEntity getPaymentById(final Long paymentId) {
        return paymentDAO.getPaymentById(paymentId);
    }

    @Transactional
    public PaymentEntity getPaymentByUuid(final String paymentUuid) {
        return paymentDAO.getPaymentByUuid(paymentUuid);
    }
}
