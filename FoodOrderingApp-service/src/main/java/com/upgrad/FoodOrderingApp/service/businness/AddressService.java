package com.upgrad.FoodOrderingApp.service.businness;

import com.upgrad.FoodOrderingApp.service.dao.AddressDAO;
import com.upgrad.FoodOrderingApp.service.dao.CustomerAddressDAO;
import com.upgrad.FoodOrderingApp.service.dao.StateDAO;
import com.upgrad.FoodOrderingApp.service.entity.*;
import com.upgrad.FoodOrderingApp.service.exception.AddressNotFoundException;
import com.upgrad.FoodOrderingApp.service.exception.AuthorizationFailedException;
import com.upgrad.FoodOrderingApp.service.exception.SaveAddressException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;

import javax.transaction.Transactional;
import java.util.List;

@Service
public class AddressService {

    @Autowired
    AddressDAO addressDAO;

    @Autowired
    Validator validator;

    @Autowired
    StateDAO stateDAO;

    @Autowired
    CustomerAddressDAO customerAddressDAO;

    public AddressEntity saveAddress(AddressEntity address, CustomerEntity customerEntity) throws SaveAddressException {

        validator.validateSaveAddressRequest(address);
        AddressEntity persistedAddressEntity = addressDAO.createAddress(address);

        final CustomerAddressEntity customerAddressEntity = new CustomerAddressEntity();
        customerAddressEntity.setAddress(persistedAddressEntity);
        customerAddressEntity.setCustomer(customerEntity);
        createCustomerAddress(customerAddressEntity);
        return persistedAddressEntity;

    }

    //Get state details by UUID
    public StateEntity getStateByUUID(final String stateUuid) throws AddressNotFoundException, SaveAddressException{

        StateEntity stateEntity = stateDAO.getStateByUuid(stateUuid);
        if(stateEntity == null){
            throw new AddressNotFoundException("ANF-002","No state by this id");
        }
        else {
            return stateEntity;
        }
    }

    public void deleteAddress(String addressId, CustomerEntity customer)
            throws AddressNotFoundException, AuthorizationFailedException {

        validator.validateDeleteAddressRequest(addressId, customer);
        AddressEntity addressEntity = addressDAO.getAddressByUUID(addressId);
        if (addressEntity == null) {
            throw new AddressNotFoundException("ANF-003", "No address by this id");
        }

    }

    //Get all addresses for a customer entity
    @Transactional
    public List<AddressEntity> getAllAddress (final CustomerEntity customerEntity){
        return customerAddressDAO.getCustomerAddressListByCustomer(customerEntity);
    }

    @Transactional
    public AddressEntity deleteAddress(AddressEntity addressEntity){
        return addressDAO.deleteAddress(addressEntity);
    }

    public List<StateEntity> getAllStates() {

        return stateDAO.getAllStates();

    }

    @Transactional
    public AddressEntity getAddressById(final Integer addressId) {
        return addressDAO.getAddressById(addressId);
    }

    @Transactional
    public AddressEntity getAddressByUuid(final String addressUuid) {
        return addressDAO.getAddressByUuid(addressUuid);
    }

    @Transactional
    public CustomerAddressEntity createCustomerAddress(CustomerAddressEntity customerAddressEntity) {

        customerAddressDAO.createCustomerAddress(customerAddressEntity);
        return customerAddressEntity;

    }

    public CustomerAddressEntity getCustomerAddress(CustomerEntity customerEntity, AddressEntity addressEntity) {

        return customerAddressDAO.getCustAddressByCustIdAddressId(customerEntity, addressEntity);

    }


}
