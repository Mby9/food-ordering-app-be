package com.upgrad.FoodOrderingApp.service.businness;

import com.upgrad.FoodOrderingApp.service.dao.AddressDAO;
import com.upgrad.FoodOrderingApp.service.entity.AddressEntity;
import com.upgrad.FoodOrderingApp.service.exception.SaveAddressException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AddressService {

    @Autowired
    AddressDAO addressDAO;

    @Autowired
    Validator validator;

    public void save(AddressEntity address) throws SaveAddressException {

        validator.validateSaveAddressRequest(address);
        addressDAO.save(address);

    }
}
