package com.upgrad.FoodOrderingApp.service.businness;

import com.upgrad.FoodOrderingApp.service.dao.CustomerDAO;
import com.upgrad.FoodOrderingApp.service.entity.CustomerAuthEntity;
import com.upgrad.FoodOrderingApp.service.entity.CustomerEntity;
import com.upgrad.FoodOrderingApp.service.exception.AuthenticationFailedException;
import com.upgrad.FoodOrderingApp.service.exception.AuthorizationFailedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;

@Service
public class AuthService {

    @Autowired
    CustomerDAO customerDAO;

    @Autowired
    Validator validator;

    @Transactional(propagation = Propagation.REQUIRED)
    public CustomerAuthEntity login(String contact, String password) throws AuthenticationFailedException {

        CustomerEntity customer = customerDAO.getUserByContact(contact);
        if (customer == null) {
            throw new AuthenticationFailedException("ATH-001", "This contact number has not been registered!");
        }

        String hashedPassword = PasswordCryptographyProvider.encrypt(password, customer.getSalt());

        if (!hashedPassword.equals(customer.getPassword())) {
            throw new AuthenticationFailedException("ATH-002", "Invalid Credentials");
        }

        JwtTokenProvider provider = new JwtTokenProvider(hashedPassword);
        CustomerAuthEntity customerAuthEntity = new CustomerAuthEntity();
        customerAuthEntity.setCustomerEntity(customer);

        ZonedDateTime now = ZonedDateTime.now();
        ZonedDateTime expiresAt = now.plusHours(8);

        customerAuthEntity.setAccessToken(provider.generateToken(customer.getUuid().toString(), now, expiresAt));
        customerAuthEntity.setLoginAt(now);
        customerAuthEntity.setLogoutAt(expiresAt);
        customerAuthEntity.setUuid(customer.getUuid().toString());

        customerDAO.createAuthToken(customerAuthEntity);

        return customerAuthEntity;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public CustomerEntity logout(String token) throws AuthorizationFailedException{

        CustomerAuthEntity customerAuthEntity = validateToken(token);

        customerAuthEntity.setLogoutAt(ZonedDateTime.now());
        customerDAO.updateAuthToken(customerAuthEntity);
        return customerAuthEntity.getCustomerEntity();

    }

    public CustomerAuthEntity validateToken(String token) throws AuthorizationFailedException {

        CustomerAuthEntity customerAuthEntity = customerDAO.getUserAuthToken(token);
        validator.validateAccessToken(customerAuthEntity);
        return customerAuthEntity;

    }

    public CustomerEntity authorizeToken(String authHeader) throws AuthorizationFailedException {
        String token = authHeader.split("Bearer")[1];
        CustomerAuthEntity customerAuthEntity = validateToken(token);
        return customerAuthEntity.getCustomerEntity();
    }
}
