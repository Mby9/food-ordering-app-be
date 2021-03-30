package com.upgrad.FoodOrderingApp.api.config;

import com.upgrad.FoodOrderingApp.api.model.*;
import com.upgrad.FoodOrderingApp.service.businness.*;
import com.upgrad.FoodOrderingApp.service.dao.StateDAO;
import com.upgrad.FoodOrderingApp.service.entity.*;
import com.upgrad.FoodOrderingApp.service.exception.*;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Component
public class Assembler {

    @Autowired
    StateDAO stateDAO;

    @Autowired
    Validator validator;

    @Autowired
    AddressService addressService;

    @Autowired
    PaymentService paymentService;

    @Autowired
    RestaurantService restaurantService;

    @Autowired
    CouponService couponService;

    @Autowired
    ItemService itemService;

    @Autowired
    StateService stateService;

    public CustomerEntity getCustomerEntityFromRequest(SignupCustomerRequest signupCustomerRequest) {

        CustomerEntity customerEntity = new CustomerEntity();
        customerEntity.setUuid(UUID.randomUUID());
        customerEntity.setFirstName(signupCustomerRequest.getFirstName());
        customerEntity.setLastName(signupCustomerRequest.getLastName());
        customerEntity.setEmail(signupCustomerRequest.getEmailAddress());
        customerEntity.setNumber(signupCustomerRequest.getContactNumber());
        customerEntity.setPassword(signupCustomerRequest.getPassword());
        customerEntity.setSalt("ABCD#123");

        return customerEntity;
    }

    public AddressEntity addressRequestToEntity(SaveAddressRequest request) {

        AddressEntity address = new AddressEntity();
        address.setUuid(UUID.randomUUID());
        address.setCity(request.getCity());
        address.setFlatNumber(request.getFlatBuildingName());
        address.setLocality(request.getLocality());
        address.setPincode(request.getPincode());

        return address;
    }

    public AddressListResponse addressEntityToResponse(Set<AddressEntity> addressEntities) {
        AddressListResponse addresses = new AddressListResponse();

        for (AddressEntity addressEntity: addressEntities) {

            AddressList addressList = new AddressList();
            addressList.setCity(addressEntity.getCity());
            addressList.setFlatBuildingName(addressEntity.getFlatNumber());
            addressList.setId(addressEntity.getUuid());
            addressList.setLocality(addressEntity.getLocality());
            addressList.setPincode(addressEntity.getPincode());
            addressList.setState(stateEntityToAddressListState(addressEntity.getStateEntity()));

            addresses.addAddressesItem(addressList);
        }

        return addresses;
    }

    public AddressListState stateEntityToAddressListState(StateEntity stateEntity) {

        AddressListState state = new AddressListState();
        state.setId(stateEntity.getUuid());
        state.setStateName(stateEntity.getStateName());

        return state;
    }

    public StatesListResponse stateEntityListToStateListResponse(List<StateEntity> states) {
        StatesListResponse response = new StatesListResponse();
        for (StateEntity state: states) {
            StatesList statesList = new StatesList();
            statesList.setId(state.getUuid());
            statesList.setStateName(state.getStateName());

            response.addStatesItem(statesList);
        }

        return response;
    }

    public PaymentListResponse paymentEntityToPaymentListResponse(List<PaymentEntity> payments) {

        PaymentListResponse response = new PaymentListResponse();

        for (PaymentEntity payment: payments) {
            PaymentResponse res = new PaymentResponse();
            res.setId(payment.getUuid());
            res.setPaymentName(payment.getPaymentName());

            response.addPaymentMethodsItem(res);
        }

        return response;
    }

    public OrdersEntity saveOrderRequestToOrdersEntity(SaveOrderRequest saveOrderRequest, CustomerEntity customerEntity)
            throws PaymentMethodNotFoundException, CouponNotFoundException, RestaurantNotFoundException,
            AddressNotFoundException, AuthorizationFailedException {

        // Gets the address details from addressService
        AddressEntity addressEntity = addressService.getAddressByUuid(saveOrderRequest.getAddressId());

        // Gets the Customer address details from customerAddressDao
        CustomerAddressEntity customerAddressEntity = addressService.getCustomerAddress(customerEntity, addressEntity);

        ZonedDateTime now = ZonedDateTime.now();

        OrdersEntity ordersEntity = new OrdersEntity();

        // Loads the ordersEntity with all the obtained details
        ordersEntity.setAddress(addressEntity);
        ordersEntity.setCoupon(couponService.getCouponByUuid(saveOrderRequest.getCouponId().toString()));
        ordersEntity.setPayment(paymentService.getPaymentByUuid(saveOrderRequest.getPaymentId().toString()));
        ordersEntity.setRestaurant(restaurantService.getRestaurantByUUId(saveOrderRequest.getRestaurantId()
                .toString()));
        ordersEntity.setUuid(UUID.randomUUID().toString());
        ordersEntity.setCustomer(customerEntity);
        ordersEntity.setAddress(addressEntity);
        ordersEntity.setBill(saveOrderRequest.getBill());
        ordersEntity.setDiscount(saveOrderRequest.getDiscount());
        ordersEntity.setDate(now);

        validator.validateSaveOrderRequest(ordersEntity, customerAddressEntity);

        return ordersEntity;

    }

    public List<OrderItemEntity> saveOrderRequestToOrderItems(SaveOrderRequest saveOrderRequest)
            throws ItemNotFoundException {

        List<OrderItemEntity> orderItems = new ArrayList<>();

        for (ItemQuantity itemQuantity : saveOrderRequest.getItemQuantities()) {
            OrderItemEntity orderItemEntity = new OrderItemEntity();
            orderItemEntity.setItem(itemService.getItemEntityByUuid(itemQuantity.getItemId().toString()));
            orderItemEntity.setQuantity(itemQuantity.getQuantity());
            orderItemEntity.setPrice(itemQuantity.getPrice());

            orderItems.add(orderItemEntity);
        }

        return orderItems;

    }

    public RestaurantList getRestaurantList(RestaurantEntity restaurantEntity) {
        RestaurantList detail = new RestaurantList();
        detail.setId(UUID.fromString(restaurantEntity.getUuid()));
        detail.setRestaurantName(restaurantEntity.getRestaurantName());
        detail.setPhotoURL(restaurantEntity.getPhotoUrl());
        detail.setCustomerRating(restaurantEntity.getCustomerRating());
        detail.setAveragePrice(restaurantEntity.getAvgPriceForTwo());
        detail.setNumberCustomersRated(restaurantEntity.getNumCustomersRated());

        // Getting address of restaurant from address entity
        AddressEntity addressEntity = addressService.getAddressById(restaurantEntity.getAddress().getId());
        RestaurantDetailsResponseAddress responseAddress = new RestaurantDetailsResponseAddress();

        responseAddress.setId((addressEntity.getUuid()));
        responseAddress.setFlatBuildingName(addressEntity.getFlatNumber());
        responseAddress.setLocality(addressEntity.getLocality());
        responseAddress.setCity(addressEntity.getCity());
        responseAddress.setPincode(addressEntity.getPincode());

        // Getting state for current address from state entity
        StateEntity stateEntity = stateService.getStateById(addressEntity.getStateEntity().getId());
        RestaurantDetailsResponseAddressState responseAddressState = new RestaurantDetailsResponseAddressState();

        responseAddressState.setId((stateEntity.getUuid()));
        responseAddressState.setStateName(stateEntity.getStateName());
        responseAddress.setState(responseAddressState);

        // Setting address with state into restaurant object
        detail.setAddress(responseAddress);
        return detail;
    }

    public RestaurantDetailsResponse getRestaurantDetailsResponse(RestaurantEntity restaurantEntity) {

        RestaurantList list = getRestaurantList(restaurantEntity);
        RestaurantDetailsResponse detailsResponse = new RestaurantDetailsResponse();

        BeanUtils.copyProperties(detailsResponse, list);

        return detailsResponse;

    }

    public List<ItemList> getItemList(CategoryEntity categoryEntity) {
        List<ItemList> itemLists = new ArrayList<>();
        for (ItemEntity itemEntity: categoryEntity.getItemEntities()) {
            ItemList itemDetail = new ItemList();
            itemDetail.setId(UUID.fromString(itemEntity.getUuid()));
            itemDetail.setItemName(itemEntity.getItemName());
            itemDetail.setPrice(itemEntity.getPrice());
            itemDetail.setItemType(ItemList.ItemTypeEnum.fromValue(itemEntity.getType()));
            itemLists.add(itemDetail);
        }

        return itemLists;
    }
}
