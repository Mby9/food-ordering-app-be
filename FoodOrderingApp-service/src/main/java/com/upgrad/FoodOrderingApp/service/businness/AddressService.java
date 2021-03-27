package com.upgrad.FoodOrderingApp.service.businness;

import com.upgrad.FoodOrderingApp.service.dao.AddressDAO;
import com.upgrad.FoodOrderingApp.service.dao.StateDAO;
import com.upgrad.FoodOrderingApp.service.entity.AddressEntity;
import com.upgrad.FoodOrderingApp.service.entity.CustomerEntity;
import com.upgrad.FoodOrderingApp.service.entity.StateEntity;
import com.upgrad.FoodOrderingApp.service.exception.AddressNotFoundException;
import com.upgrad.FoodOrderingApp.service.exception.AuthorizationFailedException;
import com.upgrad.FoodOrderingApp.service.exception.SaveAddressException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AddressService {

    @Autowired
    AddressDAO addressDAO;

    @Autowired
    Validator validator;

    @Autowired
    StateDAO stateDAO;

    public void save(AddressEntity address) throws SaveAddressException {

        validator.validateSaveAddressRequest(address);
        addressDAO.save(address);

    }

    public void deleteAddress(String addressId, CustomerEntity customer)
            throws AddressNotFoundException, AuthorizationFailedException {

        validator.validateDeleteAddressRequest(addressId, customer);
        AddressEntity addressEntity = addressDAO.getAddressByUUID(addressId);
        if (addressEntity == null) {
            throw new AddressNotFoundException("ANF-003", "No address by this id");
        }

    }

    public List<StateEntity> getAllStates() {

        return stateDAO.getAllStates();

    }
}
