package com.upgrad.FoodOrderingApp.api.config;

import com.upgrad.FoodOrderingApp.api.model.*;
import com.upgrad.FoodOrderingApp.service.dao.StateDAO;
import com.upgrad.FoodOrderingApp.service.entity.AddressEntity;
import com.upgrad.FoodOrderingApp.service.entity.CustomerEntity;
import com.upgrad.FoodOrderingApp.service.entity.PaymentEntity;
import com.upgrad.FoodOrderingApp.service.entity.StateEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Component
public class Assembler {

    @Autowired
    StateDAO stateDAO;

    public CustomerEntity getCustomerEntityFromRequest(SignupCustomerRequest signupCustomerRequest) {

        CustomerEntity customerEntity = new CustomerEntity();
        customerEntity.setUuid(UUID.randomUUID());
        customerEntity.setFirstName(signupCustomerRequest.getFirstName());
        customerEntity.setLastName(signupCustomerRequest.getLastName());
        customerEntity.setEmail(signupCustomerRequest.getEmailAddress());
        customerEntity.setNumber(signupCustomerRequest.getContactNumber());
        customerEntity.setPassword(signupCustomerRequest.getPassword());
        customerEntity.setSalt("ABCD#123");

        return customerEntity;
    }

    public AddressEntity addressRequestToEntity(SaveAddressRequest request) {

        AddressEntity address = new AddressEntity();
        address.setUuid(UUID.randomUUID());
        address.setCity(request.getCity());
        address.setFlatNumber(request.getFlatBuildingName());
        address.setLocality(request.getLocality());
        address.setPincode(request.getPincode());

        return address;
    }

    public AddressListResponse addressEntityToResponse(Set<AddressEntity> addressEntities) {
        AddressListResponse addresses = new AddressListResponse();

        for (AddressEntity addressEntity: addressEntities) {

            AddressList addressList = new AddressList();
            addressList.setCity(addressEntity.getCity());
            addressList.setFlatBuildingName(addressEntity.getFlatNumber());
            addressList.setId(addressEntity.getUuid());
            addressList.setLocality(addressEntity.getLocality());
            addressList.setPincode(addressEntity.getPincode());
            addressList.setState(stateEntityToAddressListState(addressEntity.getStateEntity()));

            addresses.addAddressesItem(addressList);
        }

        return addresses;
    }

    public AddressListState stateEntityToAddressListState(StateEntity stateEntity) {

        AddressListState state = new AddressListState();
        state.setId(stateEntity.getUuid());
        state.setStateName(stateEntity.getStateName());

        return state;
    }

    public StatesListResponse stateEntityListToStateListResponse(List<StateEntity> states) {
        StatesListResponse response = new StatesListResponse();
        for (StateEntity state: states) {
            StatesList statesList = new StatesList();
            statesList.setId(state.getUuid());
            statesList.setStateName(state.getStateName());

            response.addStatesItem(statesList);
        }

        return response;
    }

    public PaymentListResponse paymentEntityToPaymentListResponse(List<PaymentEntity> payments) {

        PaymentListResponse response = new PaymentListResponse();

        for (PaymentEntity payment: payments) {
            PaymentResponse res = new PaymentResponse();
            res.setId(payment.getUuid());
            res.setPaymentName(payment.getPaymentName());

            response.addPaymentMethodsItem(res);
        }

        return response;
    }
}
