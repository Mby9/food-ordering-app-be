package com.upgrad.FoodOrderingApp.api.controller;

import com.upgrad.FoodOrderingApp.api.config.Assembler;
import com.upgrad.FoodOrderingApp.api.model.*;
import com.upgrad.FoodOrderingApp.service.businness.*;
import com.upgrad.FoodOrderingApp.service.entity.*;
import com.upgrad.FoodOrderingApp.service.exception.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RestController
@CrossOrigin
@RequestMapping("/")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @Autowired
    private CouponService couponService;

    @Autowired
    private PaymentService paymentService;

    @Autowired
    private AuthService authService;

    @Autowired
    private Validator validator;

    @Autowired
    private AddressService addressService;

    @Autowired
    private StateService stateService;

    @Autowired
    private ItemService itemService;

    @Autowired
    private Assembler assembler;

    /**
     *
     * @param authorization, coupon_name
     * @return Coupon details of the coupon name provided
     * @throws AuthorizationFailedException - When customer is not logged in or logged out or login expired
     *         CouponNotFoundException - When the coupon provided is invalid
     */
    @RequestMapping(method = RequestMethod.GET, path = "/order/coupon/{coupon_name}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<CouponDetailsResponse> getCoupon(@RequestHeader("authorization")  final String authorization, @PathVariable("coupon_name") final String couponName)
            throws AuthorizationFailedException, CouponNotFoundException {

        // Splits the Bearer authorization text as Bearer and bearerToken
        String[] bearerToken = authorization.split( "Bearer ");

        // Throw exception if path variable(coupon_name) is empty
        if(couponName == null || couponName.isEmpty() || couponName.equalsIgnoreCase("\"\"")){
            throw new CouponNotFoundException("CPF-002", "Coupon name field should not be empty");
        }

        // Calls getCouponByName with couponName and bearerToken as arguments
        CouponEntity couponEntity = orderService.getCouponByName(couponName, bearerToken[1]);

        // Throw exception if there is no coupon with the name provided
        if (couponEntity == null) {
            throw new CouponNotFoundException("CPF-001", "No coupon by this name");
        }

        // Loads the CouponDetailsResponse with uuid, couponName and percent of the coupon found
        CouponDetailsResponse couponDetailsResponse = new CouponDetailsResponse().id(UUID.fromString(couponEntity.getUuid()))
                .couponName(couponEntity.getCouponName()).percent(couponEntity.getPercent());

        // Returns CouponDetailsResponse with OK https status
        return new ResponseEntity<>(couponDetailsResponse, HttpStatus.OK);

    }

    @RequestMapping(method = RequestMethod.GET, path = "/order", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<CustomerOrderResponse> getCustomerOrders(@RequestHeader("authorization") final String authorization)
            throws AuthorizationFailedException {

        String[] bearerToken = authorization.split( "Bearer ");

        // Validates the access token retrieved from database
        CustomerAuthEntity customerAuthEntity = authService.validateToken(bearerToken[1]);
        validator.validateAccessToken(customerAuthEntity);


        CustomerEntity customerEntity = authService.authorizeToken(bearerToken[1]);


        // Gets all the past orders of the customer
        final List<OrdersEntity> ordersEntityList = orderService.getCustomerOrders(customerEntity);

        CustomerOrderResponse customerOrderResponse = new CustomerOrderResponse();

        for (OrdersEntity ordersEntity: ordersEntityList) {

            OrderListCustomer orderListCustomer = new OrderListCustomer();
            orderListCustomer.setId((ordersEntity.getCustomer().getUuid()));
            orderListCustomer.setFirstName(ordersEntity.getCustomer().getFirstName());
            orderListCustomer.setLastName(ordersEntity.getCustomer().getLastName());
            orderListCustomer.setContactNumber(ordersEntity.getCustomer().getNumber());
            orderListCustomer.setEmailAddress(ordersEntity.getCustomer().getEmail());

            OrderListAddressState orderListAddressState = new OrderListAddressState();
            orderListAddressState.setId((ordersEntity.getAddress().getStateEntity().getUuid()));
            orderListAddressState.setStateName(ordersEntity.getAddress().getStateEntity().getStateName());

            OrderListAddress orderListAddress = new OrderListAddress();
            orderListAddress.setId((ordersEntity.getAddress().getUuid()));
            orderListAddress.setFlatBuildingName(ordersEntity.getAddress().getFlatNumber());
            orderListAddress.setLocality(ordersEntity.getAddress().getLocality());
            orderListAddress.setCity(ordersEntity.getAddress().getCity());
            orderListAddress.setPincode(ordersEntity.getAddress().getPincode());
            orderListAddress.setState(orderListAddressState);

            OrderListCoupon orderListCoupon = new OrderListCoupon();
            orderListCoupon.setId(UUID.fromString(ordersEntity.getCoupon().getUuid()));
            orderListCoupon.setCouponName(ordersEntity.getCoupon().getCouponName());
            orderListCoupon.setPercent(ordersEntity.getCoupon().getPercent());

            OrderListPayment orderListPayment = new OrderListPayment();
            orderListPayment.setId(UUID.fromString(ordersEntity.getUuid()));
            orderListPayment.setPaymentName(ordersEntity.getPayment().getPaymentName());

            OrderList orderList = new OrderList();
            orderList.setId(UUID.fromString(ordersEntity.getUuid()));
            orderList.setDate(ordersEntity.getDate().toString());
            orderList.setAddress(orderListAddress);
            orderList.setCustomer(orderListCustomer);
            orderList.setPayment(orderListPayment);
            orderList.setCoupon(orderListCoupon);
            orderList.setBill(ordersEntity.getBill());
            orderList.setDiscount(ordersEntity.getDiscount());

            for (OrderItemEntity orderItemEntity : itemService.getItemsByOrder(ordersEntity)) {

                ItemQuantityResponseItem itemQuantityResponseItem = new ItemQuantityResponseItem();
                itemQuantityResponseItem.setId(UUID.fromString(orderItemEntity.getItem().getUuid()));
                itemQuantityResponseItem.setItemName(orderItemEntity.getItem().getItemName());
                itemQuantityResponseItem.setItemPrice(orderItemEntity.getItem().getPrice());
                itemQuantityResponseItem.setType(ItemQuantityResponseItem.TypeEnum.valueOf(orderItemEntity.getItem().getType().toString()));

                ItemQuantityResponse itemQuantityResponse = new ItemQuantityResponse();
                itemQuantityResponse.setItem(itemQuantityResponseItem);
                itemQuantityResponse.setPrice(orderItemEntity.getPrice());
                itemQuantityResponse.setQuantity(orderItemEntity.getQuantity());

                orderList.addItemQuantitiesItem(itemQuantityResponse);
            }

            customerOrderResponse.addOrdersItem(orderList);

        }

        return new ResponseEntity<>(customerOrderResponse, HttpStatus.OK);

    }

    /**
     *
     * @param saveOrderRequest, authorization
     * @return Response of the saved order
     * @throws AuthorizationFailedException - When customer is not logged in or logged out or login expired
     *         CouponNotFoundException - When the coupon provided is invalid
     *         AddressNotFoundException - When the address provided is invalid
     *         PaymentMethodNotFoundException - When the payment method is invalid
     *         RestaurantNotFoundException - When the restaurant is invalid
     *
     */
    @CrossOrigin
    @RequestMapping(method = RequestMethod.POST, path = "/order", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<SaveOrderResponse> saveOrder(@RequestHeader("authorization") final String authorization,
                                                       @RequestBody SaveOrderRequest saveOrderRequest)
            throws AuthorizationFailedException, CouponNotFoundException,
            AddressNotFoundException, PaymentMethodNotFoundException,
            RestaurantNotFoundException, ItemNotFoundException {


        // Gets the customerAuthToken details from customerDao
        CustomerEntity customerEntity = authService.authorizeToken(authorization);

        OrdersEntity ordersEntity = assembler.saveOrderRequestToOrdersEntity(saveOrderRequest, customerEntity);
        List<OrderItemEntity> orderItems = assembler.saveOrderRequestToOrderItems(saveOrderRequest);

        // Calls the saveOrder method of orderService and recieves the order entity
        final OrdersEntity savedOrderEntity = orderService.saveOrder(ordersEntity, orderItems);

        // Loads the uuid of the saved order and respective status message to SaveOrderResponse
        SaveOrderResponse saveOrderResponse = new SaveOrderResponse().id(savedOrderEntity.getUuid())
                .status("ORDER SUCCESSFULLY PLACED");

        // Returns the SaveOrderResponse with Created http status
        return new ResponseEntity<>(saveOrderResponse, HttpStatus.CREATED);
    }
}
