package com.upgrad.FoodOrderingApp.api.controller;

import com.upgrad.FoodOrderingApp.api.config.Assembler;
import com.upgrad.FoodOrderingApp.api.model.*;
import com.upgrad.FoodOrderingApp.service.businness.AddressService;
import com.upgrad.FoodOrderingApp.service.businness.AuthService;
import com.upgrad.FoodOrderingApp.service.businness.CustomerService;
import com.upgrad.FoodOrderingApp.service.dao.StateDAO;
import com.upgrad.FoodOrderingApp.service.entity.AddressEntity;
import com.upgrad.FoodOrderingApp.service.entity.CustomerEntity;
import com.upgrad.FoodOrderingApp.service.entity.StateEntity;
import com.upgrad.FoodOrderingApp.service.exception.AddressNotFoundException;
import com.upgrad.FoodOrderingApp.service.exception.AuthorizationFailedException;
import com.upgrad.FoodOrderingApp.service.exception.SaveAddressException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@RestController
@RequestMapping("/")
public class AddressController {

    @Autowired
    AuthService authService;

    @Autowired
    AddressService addressService;

    @Autowired
    Assembler assembler;

    @Autowired
    StateDAO stateDAO;

    @Autowired
    CustomerService customerService;

    @PostMapping(path = "/address")
    public ResponseEntity<SaveAddressResponse> saveAddress(@RequestHeader("authorization") String authHeader,
                                                           @RequestBody SaveAddressRequest request)
            throws AuthorizationFailedException, SaveAddressException, AddressNotFoundException {

        CustomerEntity customer = authService.authorizeToken(authHeader);
        AddressEntity address = assembler.addressRequestToEntity(request);
        Set<CustomerEntity> customerEntitySet = new HashSet<>();
        customerEntitySet.add(customer);
        address.setCustomers(customerEntitySet);
        address.setStateEntity(stateDAO.getStateById(request.getStateUuid()));
        addressService.save(address);

        SaveAddressResponse response = new SaveAddressResponse();
        response.setId(address.getUuid().toString());
        response.setStatus("ADDRESS SUCCESSFULLY REGISTERED");

        return new ResponseEntity<>(response, HttpStatus.OK);

    }

    @GetMapping(path = "/address/customer")
    public ResponseEntity<AddressListResponse> getAddresses(@RequestHeader("authorization") String authHeader)
        throws AuthorizationFailedException {

        CustomerEntity customer = authService.authorizeToken(authHeader);
        Set<AddressEntity> addressEntities = customerService.getAddressesForCustomer(customer);

        AddressListResponse addresses = assembler.addressEntityToResponse(addressEntities);

        return new ResponseEntity<>(addresses, HttpStatus.OK);

    }

    @DeleteMapping(path = "/address/{address_id}")
    public ResponseEntity<DeleteAddressResponse> deleteAddress(@RequestHeader("authorization") String authHeader,
                                                               @PathVariable(value = "address_id") String addressId)
            throws AuthorizationFailedException, AddressNotFoundException {

        CustomerEntity customer = authService.authorizeToken(authHeader);
        addressService.deleteAddress(addressId, customer);
        DeleteAddressResponse response = new DeleteAddressResponse();
        response.setId(UUID.fromString(addressId));
        response.setStatus("ADDRESS DELETED SUCCESSFULLY");

        return new ResponseEntity<>(response, HttpStatus.OK);

    }

    @GetMapping(path = "/states")
    public ResponseEntity<StatesListResponse> getStates() {

        List<StateEntity> states = addressService.getAllStates();
        StatesListResponse response = assembler.stateEntityListToStateListResponse(states);

        return new ResponseEntity<>(response, HttpStatus.OK);

    }
}
