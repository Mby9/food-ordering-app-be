package com.upgrad.FoodOrderingApp.service.businness;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.upgrad.FoodOrderingApp.service.dao.CustomerDAO;
import com.upgrad.FoodOrderingApp.service.dao.AddressDAO;
import com.upgrad.FoodOrderingApp.service.dao.CustomerAddressDAO;
import com.upgrad.FoodOrderingApp.service.entity.CustomerEntity;
import com.upgrad.FoodOrderingApp.service.entity.StateEntity;
import com.upgrad.FoodOrderingApp.service.entity.AddressEntity;
import com.upgrad.FoodOrderingApp.service.entity.CustomerAddressEntity;
import com.upgrad.FoodOrderingApp.service.exception.SaveAddressException;
import com.upgrad.FoodOrderingApp.service.exception.AddressNotFoundException;

import java.util.List;
import javax.transaction.Transactional;

@Service
public class AddressService {
    @Autowired
    AddressDAO addressDao;

    @Autowired
    CustomerDAO customerDao;

    @Autowired
    ServiceUtility serviceUtility;

    @Autowired
    CustomerAddressDAO customerAddressDao;

    public List<StateEntity> getAllStates() {
        return addressDao.getAllStates();
    }

    public List<AddressEntity> getAllAddress(CustomerEntity customerEntity) {
        return addressDao.getAllAddresses();
    }

    /* helps to fetch the required state entity based on uuid */
    public StateEntity getStateByUUID(String stateUuid) throws AddressNotFoundException {
        StateEntity state = addressDao.getStateByUuid(stateUuid);
        if (state == null) {
            throw new AddressNotFoundException("ANF-002", "No state by this id");
        } else {
            return state;
        }
    }

    /* helps to validate the new address details */
    public boolean validateNewAddressRequirements(
            String building, String locality, String city, String pincode, String stateUuid
    ) throws SaveAddressException, Exception {
        if (
                serviceUtility.isStringNullOrEmpty(building) ||
                        serviceUtility.isStringNullOrEmpty(locality) ||
                        serviceUtility.isStringNullOrEmpty(city) ||
                        serviceUtility.isStringNullOrEmpty(pincode) ||
                        serviceUtility.isStringNullOrEmpty(stateUuid)
        ) {
            throw new SaveAddressException("SAR-001", "No field can be empty");
        } else if (!serviceUtility.isValidPincode(pincode)) {
            throw new SaveAddressException("SAR-002", "Invalid pincode");
        } else {
            return true;
        }
    }


    /* validates the details of the address being sent and if ok, will help insert them in the db */
    @Transactional
    public AddressEntity saveAddress(AddressEntity newAddress, int customerId)
            throws SaveAddressException {
        /* make the request */
        addressDao.createNewAddressEntity(newAddress);

        /* get customer entity by id */
        CustomerEntity customer = customerDao.getCustomerEntityById(customerId);

        /* create a customer address entity */
        CustomerAddressEntity customerAddressEntity = new CustomerAddressEntity();
        customerAddressEntity.setCustomerEntity(customer);
        customerAddressEntity.setAddressEntity(newAddress);

        /* set entry in the customer_address table */
        customerAddressDao.createNewCustomerAddressEntry(customerAddressEntity);

        /* return value to the controller */
        return newAddress;
    }

    /* fetches the address entity based on id */
    public AddressEntity getAddressByUUID(String addressId, CustomerEntity customerEntity) throws AddressNotFoundException {
        if (serviceUtility.isStringNullOrEmpty(addressId)) {
            throw new AddressNotFoundException("ANF-005", "Address id can not be empty");
        } else {
            AddressEntity addressEntity = addressDao.getAddressByUuid(addressId);
            if (addressEntity == null) {
                throw new AddressNotFoundException("ANF-003", "No address by this id");
            } else {
                return addressEntity;
            }
        }
    }

    /* fetches the customer address entity based on address id */
    public CustomerAddressEntity getEntityByAddressId(Integer addressEntityId) {
        CustomerAddressEntity customerAddressEntity = customerAddressDao.getEntityByAddressId(addressEntityId);
        return customerAddressEntity;
    }

    /* will perform the delete action */
    @Transactional
    public AddressEntity deleteAddress(AddressEntity addressEntity) throws AddressNotFoundException {
        String uuid = addressEntity.getUuid();
        addressDao.deleteAddressEntityByUuid(uuid);
        return addressEntity;
    }
}
