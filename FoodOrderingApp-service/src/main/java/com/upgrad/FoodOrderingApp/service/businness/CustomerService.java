package com.upgrad.FoodOrderingApp.service.businness;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/* java imports */
import javax.transaction.Transactional;
import java.time.ZonedDateTime;

/* project imports */
import com.upgrad.FoodOrderingApp.service.dao.CustomerDAO;
import com.upgrad.FoodOrderingApp.service.entity.CustomerEntity;
import com.upgrad.FoodOrderingApp.service.entity.CustomerAuthEntity;
import com.upgrad.FoodOrderingApp.service.exception.SignUpRestrictedException;
import com.upgrad.FoodOrderingApp.service.exception.AuthenticationFailedException;
import com.upgrad.FoodOrderingApp.service.exception.AuthorizationFailedException;
import com.upgrad.FoodOrderingApp.service.exception.UpdateCustomerException;

@Service
public class CustomerService {
    @Autowired
    private CustomerDAO customerDao;

    @Autowired
    private ServiceUtility serviceUtility;

    @Autowired
    private PasswordCryptographyProvider cryptoProvider;

    @Transactional
    public CustomerEntity saveCustomer(CustomerEntity customerToRegister) {
        customerDao.registerNewCustomer(customerToRegister);
        return customerToRegister;
    }

    @Transactional
    public CustomerAuthEntity authenticate(String username, String password) throws AuthenticationFailedException {
        CustomerEntity registeredCustomer = customerDao.getUserByContactNumber(username);
        if (registeredCustomer == null) {
            throw new AuthenticationFailedException("ATH-001", "This contact number has not been registered!");
        }
        else {
            String hashedPassword = cryptoProvider.encrypt(password, registeredCustomer.getSalt());
            if (!hashedPassword.equals(registeredCustomer.getPassword())) {
                throw new AuthenticationFailedException("ATH-002", "Invalid Credentials");
            } else {
                /* build the jwt */
                JwtTokenProvider jwtTokenProvider = new JwtTokenProvider(hashedPassword);
                final ZonedDateTime now = ZonedDateTime.now();
                final ZonedDateTime expiresAt = now.plusHours(8);
                String jwt = jwtTokenProvider.generateToken(registeredCustomer.getUuid(), now, expiresAt);

                /* make a registry in the customer_auth table in the database - and return the required value */
                return customerDao.registerLoginSession(registeredCustomer, jwt, now, expiresAt);
            }
        }
    }

    @Transactional
    public CustomerAuthEntity logout(String jwt) throws AuthorizationFailedException {
        /* 1. validate the jwt */
        CustomerAuthEntity entity = this.validateAccessToken(jwt);

        /* 2. set the logout time */
        entity.setLogoutAt(ZonedDateTime.now());

        /* 3. perform work with the dao */
        customerDao.updateCustomerAuthEntity(entity);

        return entity;
    }

    public boolean validateSignUpRequest(String firstName, String lastName, String email, String contact, String password)
            throws SignUpRestrictedException {
        /* only last name is optional */
        if (
                serviceUtility.isStringNullOrEmpty(firstName) ||
                        serviceUtility.isStringNullOrEmpty(email) ||
                        serviceUtility.isStringNullOrEmpty(contact) ||
                        serviceUtility.isStringNullOrEmpty(password)
        ) {
            throw new SignUpRestrictedException("SGR-005", "Except last name all fields should be filled");
        } else if (!serviceUtility.isValidEmailString(email)) {
            throw new SignUpRestrictedException("SGR-002", "Invalid email-id format!");
        } else if (!serviceUtility.isValidContactNumber(contact)) {
            throw new SignUpRestrictedException("SGR-003", "Invalid contact number!");
        } else if (!serviceUtility.isValidAndStrongPassword(password)) {
            throw new SignUpRestrictedException("SGR-004", "Weak password!");
        } else {
            CustomerEntity entityWasFound = customerDao.getUserByContactNumber(contact);
            if (entityWasFound != null) {
                throw new SignUpRestrictedException("SGR-001", "This contact number is already registered! Try other contact number.");
            } else {
                return true;
            }
        }
    }

    public CustomerAuthEntity validateAccessToken(String jwt)
            throws AuthorizationFailedException {
        CustomerAuthEntity customerAuthEntity = customerDao.getCustomerEntityByAccessToken(jwt);
        if (customerAuthEntity == null) {
            throw new AuthorizationFailedException("ATHR-001", "Customer is not Logged in.");
        } else {
            ZonedDateTime logoutStamp = customerAuthEntity.getLogoutAt();
            if (logoutStamp != null) {
                throw new AuthorizationFailedException("ATHR-002", "Customer is logged out. Log in again to access this endpoint.");
            } else {
                boolean isJwtValid = serviceUtility.checkIfTokenHasExpired(customerAuthEntity.getExpiresAt().toString());
                if (!isJwtValid) {
                    customerAuthEntity.setLogoutAt(ZonedDateTime.now());
                    customerDao.updateCustomerAuthEntity(customerAuthEntity);
                    throw new AuthorizationFailedException("ATHR-003", "Your session is expired. Log in again to access this endpoint.");
                } else {
                    return customerAuthEntity;
                }
            }
        }
    }

    public CustomerEntity getCustomer(String jwt) throws AuthorizationFailedException {
        CustomerAuthEntity entity = this.validateAccessToken(jwt);
        return entity.getCustomer();
    }

    @Transactional
    public CustomerEntity updateCustomer(CustomerEntity customerEntity) {
        customerDao.updateCustomerEntity(customerEntity);
        return customerEntity;
    }

    @Transactional
    public CustomerEntity updateCustomerPassword(String oldPassword, String newPassword, CustomerEntity customer)
            throws UpdateCustomerException, Exception {
        /* check to see if the old password is the correct password for the customer entity */
        String hashedPassword = cryptoProvider.encrypt(oldPassword, customer.getSalt());
        if (!hashedPassword.equals(customer.getPassword())) {
            throw new UpdateCustomerException("UCR-004", "Incorrect old password!");
        }
        /* validate the strength of the new password */
        else if (!serviceUtility.isValidAndStrongPassword(newPassword)) {
            throw new UpdateCustomerException("UCR-001", "Weak password!");
        } else {
            /* update the customer entity with the new password details */
            String[] arrayOfEncryptedString = this.cryptoProvider.encrypt(newPassword);
            customer.setSalt(arrayOfEncryptedString[0]);
            customer.setPassword(arrayOfEncryptedString[1]);
            customerDao.updateCustomerEntity(customer);

            return customer;
        }
    }
}
