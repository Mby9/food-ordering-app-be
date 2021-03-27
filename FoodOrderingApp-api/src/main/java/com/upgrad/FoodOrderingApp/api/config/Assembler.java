package com.upgrad.FoodOrderingApp.api.config;

import com.upgrad.FoodOrderingApp.api.model.AddressList;
import com.upgrad.FoodOrderingApp.api.model.AddressListState;
import com.upgrad.FoodOrderingApp.api.model.SaveAddressRequest;
import com.upgrad.FoodOrderingApp.api.model.SignupCustomerRequest;
import com.upgrad.FoodOrderingApp.service.dao.StateDAO;
import com.upgrad.FoodOrderingApp.service.entity.AddressEntity;
import com.upgrad.FoodOrderingApp.service.entity.CustomerEntity;
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

    public List<AddressList> addressEntityToResponse(Set<AddressEntity> addressEntities) {
        List<AddressList> addresses = new ArrayList<>();

        for (AddressEntity addressEntity: addressEntities) {

            AddressList addressList = new AddressList();
            addressList.setCity(addressEntity.getCity());
            addressList.setFlatBuildingName(addressEntity.getFlatNumber());
            addressList.setId(addressEntity.getUuid());
            addressList.setLocality(addressEntity.getLocality());
            addressList.setPincode(addressEntity.getPincode());
            addressList.setState(stateEntityToAddressListState(addressEntity.getStateEntity()));

            addresses.add(addressList);
        }

        return addresses;
    }

    private AddressListState stateEntityToAddressListState(StateEntity stateEntity) {
        AddressListState state = new AddressListState();
        state.setId(stateEntity.getUuid());
        state.setStateName(stateEntity.getStateName());

        return state;
    }
}
