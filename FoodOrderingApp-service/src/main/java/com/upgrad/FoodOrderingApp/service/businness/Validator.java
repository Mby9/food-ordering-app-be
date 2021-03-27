package com.upgrad.FoodOrderingApp.service.businness;

import com.upgrad.FoodOrderingApp.service.dao.CustomerDAO;
import com.upgrad.FoodOrderingApp.service.entity.AddressEntity;
import com.upgrad.FoodOrderingApp.service.entity.CustomerAuthEntity;
import com.upgrad.FoodOrderingApp.service.entity.CustomerEntity;
import com.upgrad.FoodOrderingApp.service.exception.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.ZonedDateTime;
import java.util.Set;
import java.util.stream.Collectors;

class Validator {

    @Autowired
    CustomerService customerService;

    @Autowired
    CustomerDAO customerDAO;

    final String PASSWORD_REGEX = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[#@$%&*!^])(?=\\S+$).{8,30}$";
    final String PHONE_NUMBER_REGEX = "^\\d{10}$";
    final String EMAIL_REGEX = "^[\\w-_\\.+]*[\\w-_\\.]\\@([\\w]+\\.)+[\\w]+[\\w]$";
    final String PINCODE_REGEX = "^[1-9][0-9]{5}$";

    void validateSignUpRequest(CustomerEntity customerEntity) throws SignUpRestrictedException{

        CustomerEntity savedCustomerEntity = customerDAO.getUserByContact(customerEntity.getNumber());
        if (savedCustomerEntity != null){
            throw new SignUpRestrictedException("SGR-001",
                    "This contact number is already registered! Try other contact number.");
        }

        if (customerEntity.getEmail() == null || customerEntity.getFirstName() == null || customerEntity.getNumber() == null
            || customerEntity.getPassword() == null) {
            throw new SignUpRestrictedException("SGR-005", "Except last name all fields should be filled.");
        }

        if (!customerEntity.getEmail().matches(EMAIL_REGEX)) {
            throw new SignUpRestrictedException("SGR-002", "Invalid email-id format!");
        }

        if (!customerEntity.getNumber().matches(PHONE_NUMBER_REGEX)) {
            throw new SignUpRestrictedException("SGR-003", "Invalid contact number!");
        }

        if (!customerEntity.getPassword().matches(PASSWORD_REGEX)) {
            throw new SignUpRestrictedException("SGR-004", "Weak password!");
        }
    }

    void validateAccessToken(CustomerAuthEntity customerAuthEntity) throws AuthorizationFailedException {

        if (customerAuthEntity == null) {
            throw new AuthorizationFailedException("ATHR-001", "Customer is not Logged in.");
        }

        if (customerAuthEntity.getLogoutAt().isBefore(ZonedDateTime.now())) {
            throw new AuthorizationFailedException("ATHR-002",
                    "Customer is logged out. Log in again to access this endpoint.");
        }

        if (customerAuthEntity.getExpiresAt().isBefore(ZonedDateTime.now())) {
            throw new AuthorizationFailedException("ATHR-003",
                    "Your session is expired. Log in again to access this endpoint.");
        }
    }

    void validateUpdateUserRequest(String firstName) throws UpdateCustomerException {

        if (firstName == null) {
            throw new UpdateCustomerException("UCR-002", "First name field should not be empty");
        }
    }

    void validateUpdatePasswordRequest(String oldPassword, String newPassword, CustomerEntity customer)
            throws UpdateCustomerException{

        if (oldPassword == null || newPassword == null) {
            throw new UpdateCustomerException("UCR-003", "No field should be empty");
        }

        String hashedPassword = PasswordCryptographyProvider.encrypt(oldPassword, customer.getSalt());
        if (hashedPassword.equals(customer.getPassword())) {
            throw new UpdateCustomerException("UCR-004", "Incorrect old password!");
        }

        if (!newPassword.matches(PASSWORD_REGEX)) {
            throw new UpdateCustomerException("UCR-001", "Weak password!");
        }

    }

    void validateSaveAddressRequest(AddressEntity address) throws SaveAddressException {

        if (address.getCity() == null || address.getFlatNumber() == null || address.getLocality() == null
            || address.getPincode() == null) {
            throw new SaveAddressException("SAR-001", "No field can be empty");
        }

        if (!address.getPincode().matches(PINCODE_REGEX)) {
            throw new SaveAddressException("SAR-002", "Invalid pincode");
        }

    }

    void validateDeleteAddressRequest(String addressId, CustomerEntity customer)
            throws AddressNotFoundException, AuthorizationFailedException {

        if (addressId == null || "".equals(addressId)) {
            throw new AddressNotFoundException("ANF-005", "Address id can not be empty");
        }

        Set<AddressEntity> customerAddresses = customer.getAddresses();
        Set<String> addressIds = customerAddresses.stream()
                .map(address -> address.getUuid().toString())
                .collect(Collectors.toSet());
        if (!addressIds.contains(addressId)) {
            throw new AuthorizationFailedException("ATHR-004",
                    "You are not authorized to view/update/delete any one else's address");
        }
    }
}
