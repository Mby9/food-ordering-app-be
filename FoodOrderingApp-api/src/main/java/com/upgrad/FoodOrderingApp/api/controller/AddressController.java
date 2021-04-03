package com.upgrad.FoodOrderingApp.api.controller;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import com.upgrad.FoodOrderingApp.service.businness.CustomerService;
import com.upgrad.FoodOrderingApp.service.businness.AddressService;
import com.upgrad.FoodOrderingApp.service.entity.StateEntity;
import com.upgrad.FoodOrderingApp.service.entity.AddressEntity;
import com.upgrad.FoodOrderingApp.api.model.StatesList;
import com.upgrad.FoodOrderingApp.api.model.StatesListResponse;
import com.upgrad.FoodOrderingApp.api.model.SaveAddressRequest;
import com.upgrad.FoodOrderingApp.api.model.SaveAddressResponse;
import com.upgrad.FoodOrderingApp.api.model.AddressListResponse;
import com.upgrad.FoodOrderingApp.api.model.DeleteAddressResponse;
import com.upgrad.FoodOrderingApp.api.model.AddressList;
import com.upgrad.FoodOrderingApp.api.model.AddressListState;
import com.upgrad.FoodOrderingApp.service.entity.CustomerEntity;
import com.upgrad.FoodOrderingApp.service.entity.CustomerAddressEntity;
import com.upgrad.FoodOrderingApp.service.exception.AuthorizationFailedException;
import com.upgrad.FoodOrderingApp.service.exception.AddressNotFoundException;

import java.util.List;
import java.util.ArrayList;
import java.util.UUID;

@RestController
public class AddressController {
    @Autowired
    CustomerService customerService;

    @Autowired
    AddressService addressService;

    @RequestMapping(
            path = "/address",
            method = RequestMethod.POST,
            consumes = MediaType.APPLICATION_JSON_UTF8_VALUE,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE
    )
    public ResponseEntity<SaveAddressResponse> saveAddress(
            @RequestHeader("authorization") String authorization, @RequestBody SaveAddressRequest addressRequest
    ) throws Exception {

        if (!authorization.contains("Bearer ")) {
            throw new AuthorizationFailedException("ATH-004", "Bearer not found in the authorizaton header section");
        } else {
            String jwt = authorization.split("Bearer ")[1];

            CustomerEntity customerEntity = customerService.getCustomer(jwt);

            boolean isAllOk = addressService.validateNewAddressRequirements(
                    addressRequest.getFlatBuildingName(), addressRequest.getLocality(), addressRequest.getCity(),
                    addressRequest.getPincode(), addressRequest.getStateUuid()
            );

            String addressUuid = UUID.randomUUID().toString();
            StateEntity state = addressService.getStateByUUID(addressRequest.getStateUuid());
            AddressEntity newAddress = new AddressEntity();
            newAddress.setFlatBuilNo(addressRequest.getFlatBuildingName());
            newAddress.setLocality(addressRequest.getLocality());
            newAddress.setCity(addressRequest.getCity());
            newAddress.setPincode(addressRequest.getPincode());
            newAddress.setUuid(addressUuid);
            newAddress.setState(state);

            AddressEntity newAddressEntity = addressService.saveAddress(newAddress, customerEntity.getId());

            SaveAddressResponse saveAddressResponse = new SaveAddressResponse();
            saveAddressResponse.setId(newAddressEntity.getUuid());
            saveAddressResponse.setStatus("ADDRESS SUCCESSFULLY SAVED");
            return new ResponseEntity(saveAddressResponse, HttpStatus.CREATED);
        }
    }

    @RequestMapping(
            path = "/address/delete/{address_id}",
            method = RequestMethod.DELETE,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE
    )
    public ResponseEntity<DeleteAddressResponse> deleteAddress(
            @RequestHeader("authorization") String authorization, @PathVariable("address_id") String addressId)
            throws AuthorizationFailedException, AddressNotFoundException {
        if (!authorization.contains("Bearer ")) {
            throw new AuthorizationFailedException("ATH-004", "Bearer not found in the authorizaton header section");
        } else {
            String jwt = authorization.split("Bearer ")[1];

            CustomerEntity customerEntity = customerService.getCustomer(jwt);

            AddressEntity addressEntity = addressService.getAddressByUUID(addressId, customerEntity);
            CustomerAddressEntity customerAddressEntity = addressService.getEntityByAddressId(addressEntity.getId());
            int customerId = customerEntity.getId();
            int customerIdFromCustomerAddressEntity = customerAddressEntity.getCustomerEntity().getId();
            if (customerIdFromCustomerAddressEntity != customerId) {
                throw new AuthorizationFailedException("ATHR-004", "You are not authorized to view/update/delete any one else's address ");
            } else {
                AddressEntity deletedAddrEntity = addressService.deleteAddress(addressEntity);
                String deletedUUID = deletedAddrEntity.getUuid();

                DeleteAddressResponse deleteResponse = new DeleteAddressResponse();
                deleteResponse.setId(UUID.fromString(deletedUUID));
                deleteResponse.setStatus("ADDRESS DELETED SUCCESSFULLY");
                return new ResponseEntity(deleteResponse, HttpStatus.OK);
            }
        }
    }

    @RequestMapping(
            path = "/address/customer",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE
    )
    public ResponseEntity<AddressListResponse> getAllAddresses(@RequestHeader("authorization") String authorization)
            throws AuthorizationFailedException, Exception {
        if (authorization.indexOf("Bearer ") == -1) {
            throw new AuthorizationFailedException("ATH-004", "Bearer not found in the authorizaton header section");
        } else {
            String jwt = authorization.split("Bearer ")[1];

            CustomerEntity customerEntity = customerService.getCustomer(jwt);

            List<AddressEntity> listOfAddresses = addressService.getAllAddress(customerEntity);

            AddressListResponse addressListResponse = new AddressListResponse();
            for (AddressEntity address : listOfAddresses) {
                AddressListState state = new AddressListState();
                state.setStateName(address.getState().getStateName());
                state.setId(UUID.fromString(address.getState().getUuid()));

                AddressList addressItem = new AddressList();
                addressItem.setId(UUID.fromString(address.getUuid()));
                addressItem.setFlatBuildingName(address.getFlatBuilNo());
                addressItem.setLocality(address.getLocality());
                addressItem.setCity(address.getCity());
                addressItem.setPincode(address.getPincode());
                addressItem.setState(state);

                addressListResponse.addAddressesItem(addressItem);
            }

            return new ResponseEntity(addressListResponse, HttpStatus.OK);
        }
    }

    @RequestMapping(
            path = "/states",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE
    )
    public ResponseEntity<StatesListResponse> getAllStates() {
        List<StateEntity> states = addressService.getAllStates();

        List<StatesList> list = new ArrayList<StatesList>();
        for (StateEntity state : states) {
            StatesList listItem = new StatesList();
            listItem.setId(UUID.fromString(state.getUuid()));
            listItem.setStateName(state.getStateName());
            list.add(listItem);
        }

        StatesListResponse apiResponse = new StatesListResponse();
        apiResponse.setStates(list);
        return new ResponseEntity(apiResponse, HttpStatus.OK);
    }
}
