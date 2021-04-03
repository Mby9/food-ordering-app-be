package com.upgrad.FoodOrderingApp.api.controller;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpHeaders;

import java.util.Base64;

import com.upgrad.FoodOrderingApp.service.businness.CustomerService;
import com.upgrad.FoodOrderingApp.service.businness.ServiceUtility;
import com.upgrad.FoodOrderingApp.api.model.SignupCustomerRequest;
import com.upgrad.FoodOrderingApp.api.model.SignupCustomerResponse;
import com.upgrad.FoodOrderingApp.api.model.LoginResponse;
import com.upgrad.FoodOrderingApp.api.model.LogoutResponse;
import com.upgrad.FoodOrderingApp.api.model.UpdateCustomerRequest;
import com.upgrad.FoodOrderingApp.api.model.UpdateCustomerResponse;
import com.upgrad.FoodOrderingApp.api.model.UpdatePasswordRequest;
import com.upgrad.FoodOrderingApp.api.model.UpdatePasswordResponse;
import com.upgrad.FoodOrderingApp.service.entity.CustomerEntity;
import com.upgrad.FoodOrderingApp.service.entity.CustomerAuthEntity;
import com.upgrad.FoodOrderingApp.service.exception.SignUpRestrictedException;
import com.upgrad.FoodOrderingApp.service.exception.AuthenticationFailedException;
import com.upgrad.FoodOrderingApp.service.exception.AuthorizationFailedException;
import com.upgrad.FoodOrderingApp.service.exception.UpdateCustomerException;

@RestController
public class CustomerController {
    @Autowired
    private CustomerService customerService;

    @Autowired
    private ServiceUtility serviceUtility;

    @CrossOrigin
    @RequestMapping(
            path = "/customer/signup",
            method = RequestMethod.POST,
            consumes = MediaType.APPLICATION_JSON_UTF8_VALUE,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE
    )
    public ResponseEntity<SignupCustomerResponse> signup(@RequestBody SignupCustomerRequest signUpRequest)
            throws SignUpRestrictedException, Exception {
        String firstName = signUpRequest.getFirstName();
        String lastName = signUpRequest.getLastName();
        String email = signUpRequest.getEmailAddress();
        String contact = signUpRequest.getContactNumber();
        String password = signUpRequest.getPassword();

        boolean isValid = customerService.validateSignUpRequest(firstName, lastName, email, contact, password);

        CustomerEntity customer = serviceUtility.createNewCustomerEntity(firstName, lastName, email, contact, password);
        CustomerEntity savedCustomer = customerService.saveCustomer(customer);
        String newUuid = savedCustomer.getUuid();

        SignupCustomerResponse signUpResponse = new SignupCustomerResponse();
        signUpResponse.setId(newUuid);
        signUpResponse.setStatus("CUSTOMER SUCCESSFULLY REGISTERED");
        return new ResponseEntity(signUpResponse, HttpStatus.CREATED);
    }

    @CrossOrigin
    @RequestMapping(
            path = "/customer/login",
            method = RequestMethod.POST,
            consumes = MediaType.APPLICATION_JSON_UTF8_VALUE,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE
    )
    public ResponseEntity<LoginResponse> login(@RequestHeader("authorization") String authorization)
            throws AuthenticationFailedException {
        if (authorization.indexOf("Basic ") == -1) {
            throw new AuthenticationFailedException("ATH-003", "Incorrect format of decoded customer name and password");
        } else {
            String authUserName;
            String authPassword;
            byte[] decode;

            try {
                decode = Base64.getDecoder().decode(authorization.split("Basic ")[1]);
            } catch (Exception e) {
                throw new AuthenticationFailedException("ATH-003", "Incorrect format of decoded customer name and password");
            }

            String decodedText = new String(decode);
            String[] decodedArray = decodedText.split(":");
            authUserName = decodedArray[0];
            authPassword = decodedArray[1];

            CustomerAuthEntity signInResponse = customerService.authenticate(authUserName, authPassword);

            HttpHeaders headers = new HttpHeaders();
            headers.add("access-token", signInResponse.getAccessToken());
            CustomerEntity customer = signInResponse.getCustomer();

            LoginResponse loginResponse = new LoginResponse();
            loginResponse.setMessage("LOGGED IN SUCCESSFULLY.");
            loginResponse.setId(customer.getId().toString());
            loginResponse.setFirstName(customer.getFirstName());
            loginResponse.setLastName(customer.getLastName());
            loginResponse.setEmailAddress(customer.getEmail());
            loginResponse.setContactNumber((String) customer.getContactNumber());
            return new ResponseEntity(loginResponse, headers, HttpStatus.OK);
        }
    }

    @CrossOrigin
    @RequestMapping(
            path = "/customer/logout",
            method = RequestMethod.POST,
            consumes = MediaType.APPLICATION_JSON_UTF8_VALUE,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE
    )
    public ResponseEntity<LogoutResponse> logout(@RequestHeader("authorization") String authorization)
            throws AuthorizationFailedException, Exception {
        if (authorization.indexOf("Bearer ") == -1) {
            throw new AuthorizationFailedException("ATH-004", "Bearer not found in the authorizaton header section");
        } else {
            String jwt = authorization.split("Bearer ")[1];

            CustomerAuthEntity entity = customerService.logout(jwt);

            LogoutResponse logoutResponse = new LogoutResponse();
            logoutResponse.setId(entity.getCustomer().getUuid());
            logoutResponse.setMessage("LOGGED OUT SUCCESSFULLY");
            return new ResponseEntity(logoutResponse, HttpStatus.OK);
        }
    }

    @CrossOrigin
    @RequestMapping(
            path = "/customer",
            method = RequestMethod.PUT,
            consumes = MediaType.APPLICATION_JSON_UTF8_VALUE,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE
    )
    public ResponseEntity<UpdateCustomerResponse> updateCustomer(
            @RequestHeader("authorization") String authorization, @RequestBody UpdateCustomerRequest updateRequest
    ) throws AuthorizationFailedException, UpdateCustomerException, Exception {
        if (authorization.indexOf("Bearer ") == -1) {
            throw new AuthorizationFailedException("ATH-004", "Bearer not found in the authorizaton header section");
        } else {
            String jwt = authorization.split("Bearer ")[1];

            CustomerEntity customerEntity = customerService.getCustomer(jwt);

            String newFirstName = updateRequest.getFirstName();
            String newLastName = updateRequest.getLastName();
            if (serviceUtility.isStringNullOrEmpty(newFirstName)) {
                throw new UpdateCustomerException("UCR-002", "First name field should not be empty");
            } else {
                customerEntity.setFirstName(newFirstName);
                customerEntity.setLastName(newLastName);
                CustomerEntity updatedCustomer = customerService.updateCustomer(customerEntity);

                UpdateCustomerResponse updateCustomerResponse = new UpdateCustomerResponse();
                updateCustomerResponse.setId(updatedCustomer.getUuid());
                updateCustomerResponse.setStatus("CUSTOMER DETAILS UPDATED SUCCESSFULLY");
                updateCustomerResponse.setFirstName(updatedCustomer.getFirstName());
                updateCustomerResponse.setLastName(updatedCustomer.getLastName());
                return new ResponseEntity(updateCustomerResponse, HttpStatus.OK);
            }
        }
    }

    @CrossOrigin
    @RequestMapping(
            path = "/customer/password",
            method = RequestMethod.PUT,
            consumes = MediaType.APPLICATION_JSON_UTF8_VALUE,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE
    )
    public ResponseEntity<UpdatePasswordResponse> updatePassword(
            @RequestHeader("authorization") String authorization, @RequestBody UpdatePasswordRequest updateRequest
    ) throws AuthorizationFailedException, UpdateCustomerException, Exception {
        String oldPassword = updateRequest.getOldPassword();
        String newPassword = updateRequest.getNewPassword();
        if (
                serviceUtility.isStringNullOrEmpty(oldPassword) ||
                        serviceUtility.isStringNullOrEmpty(newPassword)) {
            throw new UpdateCustomerException("UCR-003", "No field should be empty");
        } else {
            if (authorization.indexOf("Bearer ") == -1) {
                throw new AuthorizationFailedException("ATH-004", "Bearer not found in the authorizaton header section");
            } else {
                String jwt = authorization.split("Bearer ")[1];

                CustomerEntity customerEntity = customerService.getCustomer(jwt);

                CustomerEntity updatedCustomerEntity = customerService.updateCustomerPassword(
                        updateRequest.getOldPassword(), updateRequest.getNewPassword(), customerEntity);

                UpdatePasswordResponse updatedResponse = new UpdatePasswordResponse();
                updatedResponse.setId(updatedCustomerEntity.getUuid());
                updatedResponse.setStatus("CUSTOMER PASSWORD UPDATED SUCCESSFULLY");
                return new ResponseEntity(updatedResponse, HttpStatus.OK);
            }
        }
    }
}
