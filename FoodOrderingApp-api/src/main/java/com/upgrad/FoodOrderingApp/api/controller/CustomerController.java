package com.upgrad.FoodOrderingApp.api.controller;

import com.upgrad.FoodOrderingApp.api.config.Assembler;
import com.upgrad.FoodOrderingApp.api.model.*;
import com.upgrad.FoodOrderingApp.service.businness.AuthService;
import com.upgrad.FoodOrderingApp.service.businness.CustomerService;
import com.upgrad.FoodOrderingApp.service.entity.CustomerAuthEntity;
import com.upgrad.FoodOrderingApp.service.entity.CustomerEntity;
import com.upgrad.FoodOrderingApp.service.exception.AuthenticationFailedException;
import com.upgrad.FoodOrderingApp.service.exception.AuthorizationFailedException;
import com.upgrad.FoodOrderingApp.service.exception.SignUpRestrictedException;
import com.upgrad.FoodOrderingApp.service.exception.UpdateCustomerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Base64;

@RestController
@RequestMapping("/customer")
public class CustomerController {

    @Autowired
    CustomerService customerService;

    @Autowired
    Assembler assembler;

    @Autowired
    AuthService authService;

    @PostMapping(path = "/", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<SignupCustomerResponse> signUp(SignupCustomerRequest signupCustomerRequest)
            throws SignUpRestrictedException {

        CustomerEntity customerEntity = assembler.getCustomerEntityFromRequest(signupCustomerRequest);
        customerService.createCustomer(customerEntity);

        SignupCustomerResponse response = new SignupCustomerResponse();
        response.setId(customerEntity.getUuid().toString());
        response.setStatus("CUSTOMER SUCCESSFULLY REGISTERED");
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PostMapping(path = "/login", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<LoginResponse> login(@RequestHeader("authorization") String authorizationHeader)
            throws AuthenticationFailedException {

        byte[] decode = null;
        try {
            decode = Base64.getDecoder().decode(authorizationHeader.split("Basic ")[1]);
        } catch (Exception ioe) {
            throw new AuthenticationFailedException("ATH-003",
                    "Incorrect format of decoded customer name and password");
        }
        String decodedText = new String(decode);
        String[] decodedArray = decodedText.split(":");
        CustomerAuthEntity userAuthTokenEntity = authService.login(decodedArray[0],decodedArray[1]);
        CustomerEntity customer = userAuthTokenEntity.getCustomerEntity();

        LoginResponse loggedInUser = new LoginResponse();
        loggedInUser.setId(customer.getUuid().toString());
        loggedInUser.setMessage("LOGGED IN SUCCESSFULLY");
        loggedInUser.setFirstName(customer.getFirstName());
        loggedInUser.setLastName(customer.getLastName());
        loggedInUser.setEmailAddress(customer.getEmail());
        loggedInUser.setContactNumber(customer.getNumber());

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("access-token", userAuthTokenEntity.getAccessToken());

        return new ResponseEntity<>(loggedInUser, httpHeaders, HttpStatus.OK);
    }

    @PostMapping(path = "/logout", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<LogoutResponse> logout(@RequestHeader("authorization") String authHeader)
            throws AuthorizationFailedException {

        String token = authHeader.split("Bearer")[1];
        CustomerEntity customer = authService.logout(token);

        LogoutResponse response = new LogoutResponse();
        response.setId(customer.getUuid().toString());
        response.setMessage("LOGGED OUT SUCCESSFULLY");

        return new ResponseEntity<>(response, HttpStatus.OK);

    }

    @PutMapping(path = "/")
    public ResponseEntity<UpdateCustomerResponse> update(@RequestHeader("authorization") String authHeader,
                                                         @RequestBody UpdateCustomerRequest request)
            throws UpdateCustomerException, AuthorizationFailedException {

        CustomerEntity customer = authService.authorizeToken(authHeader);
        customerService.updateCustomer(request.getFirstName(), request.getLastName(), customer);

        UpdateCustomerResponse response = new UpdateCustomerResponse();
        response.setId(customer.getUuid().toString());
        response.setFirstName(customer.getFirstName());
        response.setLastName(customer.getLastName());
        response.setStatus("CUSTOMER DETAILS UPDATED SUCCESSFULLY");

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PutMapping(path = "/password")
    public ResponseEntity<UpdatePasswordResponse> updatePassword(@RequestHeader("authorization") String authHeader,
                                                                 @RequestBody UpdatePasswordRequest request)
            throws UpdateCustomerException, AuthorizationFailedException {

        CustomerEntity customer = authService.authorizeToken(authHeader);
        customerService.updatePassword(request.getOldPassword(), request.getNewPassword(), customer);

        UpdatePasswordResponse response = new UpdatePasswordResponse();
        response.setId(customer.getUuid().toString());
        response.setStatus("CUSTOMER PASSWORD UPDATED SUCCESSFULLY");

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

}
