package com.upgrad.FoodOrderingApp.service.businness;

import com.upgrad.FoodOrderingApp.service.dao.CustomerDAO;
import com.upgrad.FoodOrderingApp.service.entity.CustomerEntity;
import com.upgrad.FoodOrderingApp.service.exception.SignUpRestrictedException;
import com.upgrad.FoodOrderingApp.service.exception.UpdateCustomerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CustomerService {

    @Autowired
    Validator validator;

    @Autowired
    PasswordCryptographyProvider passwordCryptographyProvider;

    @Autowired
    CustomerDAO customerDAO;

    public void createCustomer(CustomerEntity customerEntity) throws SignUpRestrictedException {

        validator.validateSignUpRequest(customerEntity);
        String hashedPassword = passwordCryptographyProvider.encrypt(customerEntity.getPassword(),
                customerEntity.getSalt());
        customerEntity.setPassword(hashedPassword);

        customerDAO.createUser(customerEntity);
    }

    public void updateCustomer(String firstName, String lastName, CustomerEntity customer)
            throws UpdateCustomerException {

        validator.validateUpdateUserRequest(firstName);
        customer.setFirstName(firstName);
        customer.setLastName(lastName);

        customerDAO.updateCustomer(customer);

    }

    public void updatePassword(String oldPassword, String newPassword, CustomerEntity customer)
            throws UpdateCustomerException {

        validator.validateUpdatePasswordRequest(oldPassword, newPassword, customer);
        String hashedPassword = passwordCryptographyProvider.encrypt(newPassword, customer.getSalt());
        customer.setPassword(hashedPassword);

        customerDAO.updateCustomer(customer);
    }
}