package com.upgrad.FoodOrderingApp.api.controller;

import com.upgrad.FoodOrderingApp.api.config.Assembler;
import com.upgrad.FoodOrderingApp.api.model.PaymentListResponse;
import com.upgrad.FoodOrderingApp.service.businness.PaymentService;
import com.upgrad.FoodOrderingApp.service.entity.PaymentEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/payment")
public class PaymentController {

    @Autowired
    Assembler assembler;

    @Autowired
    PaymentService paymentService;

    @GetMapping(path = "/")
    public ResponseEntity<PaymentListResponse> getPayments() {

        List<PaymentEntity> payments = paymentService.getPayments();
        PaymentListResponse response = assembler.paymentEntityToPaymentListResponse(payments);

        return new ResponseEntity<>(response, HttpStatus.OK);

    }

}
