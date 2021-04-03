package com.upgrad.FoodOrderingApp.api.controller;

import com.upgrad.FoodOrderingApp.api.model.*;
import com.upgrad.FoodOrderingApp.service.businness.*;
import com.upgrad.FoodOrderingApp.service.entity.*;
import com.upgrad.FoodOrderingApp.service.exception.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.*;

@RestController
@RequestMapping("")
@CrossOrigin
public class OrderController {

    @Autowired
    private OrderService orderService;

    @Autowired
    private CustomerService customerService;

    @Autowired
    private AddressService addressService;

    @Autowired
    private PaymentService paymentService;

    @Autowired
    private RestaurantService restaurantService;

    @Autowired
    private ItemService itemService;

    @RequestMapping(
            path = "/order/coupon/{coupon_name}",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<CouponDetailsResponse> getCouponByCouponName(@RequestHeader("authorization") final String authorization,
                                                                       @PathVariable("coupon_name") final String couponName)
            throws AuthorizationFailedException, CouponNotFoundException {

        String[] authParts = authorization.split("Bearer ");
        final String accessToken = authParts[1];

        CustomerEntity customerEntity = customerService.getCustomer(accessToken);

        CouponEntity couponEntity = orderService.getCouponByCouponName(couponName);

        CouponDetailsResponse couponDetailsResponse = new CouponDetailsResponse()
                .couponName(couponEntity.getCouponName())
                .id(UUID.fromString(couponEntity.getUuid()))
                .percent(couponEntity.getPercent());

        return new ResponseEntity<CouponDetailsResponse>(couponDetailsResponse, HttpStatus.OK);
    }

    @RequestMapping(
            method = RequestMethod.GET, path = "/order",
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<CustomerOrderResponse> getPastOrdersOfUser(@RequestHeader("authorization") final String authorization)
            throws AuthenticationFailedException, AuthorizationFailedException {
        String accessToken = authorization.split("Bearer ")[1];

        CustomerEntity customerEntity = customerService.getCustomer(accessToken);

        List<OrdersEntity> ordersEntities = orderService.getOrdersByCustomers(customerEntity.getUuid());

        List<OrderList> orderLists = new LinkedList<>();

        if (ordersEntities != null) {
            for (OrdersEntity ordersEntity : ordersEntities) {
                List<OrderItemEntity> orderItemEntities = orderService.getOrderItemsByOrder(ordersEntity);

                List<ItemQuantityResponse> itemQuantityResponseList = new LinkedList<>();
                orderItemEntities.forEach(orderItemEntity -> {          //Looping for every item in the order to get details of the item ordered
                    ItemQuantityResponseItem itemQuantityResponseItem = new ItemQuantityResponseItem()
                            .itemName(orderItemEntity.getItem().getItemName())
                            .itemPrice(orderItemEntity.getItem().getPrice())
                            .id(UUID.fromString(orderItemEntity.getItem().getUuid()))
                            .type(ItemQuantityResponseItem.TypeEnum.valueOf(orderItemEntity.getItem().getType().getValue()));
                    ItemQuantityResponse itemQuantityResponse = new ItemQuantityResponse()
                            .item(itemQuantityResponseItem)
                            .quantity(orderItemEntity.getQuantity())
                            .price(orderItemEntity.getPrice());
                    itemQuantityResponseList.add(itemQuantityResponse);
                });
                //Creating OrderListAddressState to add in the address
                OrderListAddressState orderListAddressState = new OrderListAddressState()
                        .id(UUID.fromString(ordersEntity.getAddress().getState().getUuid()))
                        .stateName(ordersEntity.getAddress().getState().getStateName());

                //Creating OrderListAddress to add address to the orderList
                OrderListAddress orderListAddress = new OrderListAddress()
                        .id(UUID.fromString(ordersEntity.getAddress().getUuid()))
                        .flatBuildingName(ordersEntity.getAddress().getFlatBuilNo())
                        .locality(ordersEntity.getAddress().getLocality())
                        .city(ordersEntity.getAddress().getCity())
                        .pincode(ordersEntity.getAddress().getPincode())
                        .state(orderListAddressState);
                //Creating OrderListCoupon to add Coupon to the orderList
                OrderListCoupon orderListCoupon = new OrderListCoupon()
                        .couponName(ordersEntity.getCoupon().getCouponName())
                        .id(UUID.fromString(ordersEntity.getCoupon().getUuid()))
                        .percent(ordersEntity.getCoupon().getPercent());

                //Creating OrderListCustomer to add Customer to the orderList
                OrderListCustomer orderListCustomer = new OrderListCustomer()
                        .id(UUID.fromString(ordersEntity.getCustomer().getUuid()))
                        .firstName(ordersEntity.getCustomer().getFirstName())
                        .lastName(ordersEntity.getCustomer().getLastName())
                        .emailAddress(ordersEntity.getCustomer().getEmail())
                        .contactNumber(ordersEntity.getCustomer().getContactNumber());

                //Creating OrderListPayment to add Payment to the orderList
                OrderListPayment orderListPayment = new OrderListPayment()
                        .id(UUID.fromString(ordersEntity.getPayment().getUuid()))
                        .paymentName(ordersEntity.getPayment().getPaymentName());

                OrderList orderList = new OrderList()
                        .id(UUID.fromString(ordersEntity.getUuid()))
                        .itemQuantities(itemQuantityResponseList)
                        .address(orderListAddress)
                        .bill(BigDecimal.valueOf(ordersEntity.getBill()))
                        .date(String.valueOf(ordersEntity.getDate()))
                        .discount(BigDecimal.valueOf(ordersEntity.getDiscount()))
                        .coupon(orderListCoupon)
                        .customer(orderListCustomer)
                        .payment(orderListPayment);
                orderLists.add(orderList);
            }

            //Creating CustomerOrderResponse by adding OrderLists to it
            CustomerOrderResponse customerOrderResponse = new CustomerOrderResponse()
                    .orders(orderLists);
            return new ResponseEntity<CustomerOrderResponse>(customerOrderResponse, HttpStatus.OK);
        } else {
            return new ResponseEntity<CustomerOrderResponse>(new CustomerOrderResponse(), HttpStatus.OK);//If no order created by customer empty array is returned.
        }
    }


    @RequestMapping(method = RequestMethod.POST, path = "/order", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<SaveOrderResponse> saveOrder(@RequestHeader("authorization") final String authorization,
                                                       @RequestBody(required = false) final SaveOrderRequest saveOrderRequest) throws Exception {
        String[] authParts = authorization.split("Bearer ");
        if(saveOrderRequest.getPaymentId() == null ){
            throw new PaymentMethodNotFoundException("PNF-002", "No payment method found by this id");
        }
        final String accessToken = authParts[1];

        OrdersEntity orderEntity = new OrdersEntity();

        CustomerEntity customerEntity = customerService.getCustomer(accessToken);

        CouponEntity couponEntity = orderService.getCouponByCouponId(saveOrderRequest.getCouponId().toString());

        PaymentEntity paymentEntity = paymentService.getPaymentByUUID(saveOrderRequest.getPaymentId().toString());

        AddressEntity addressEntity = addressService.getAddressByUUID(saveOrderRequest.getAddressId(), customerEntity);

        RestaurantEntity restaurantEntity = restaurantService.restaurantByUUID(saveOrderRequest.getRestaurantId().toString());

        orderEntity.setBill(saveOrderRequest.getBill().doubleValue());
        orderEntity.setUuid(UUID.randomUUID().toString());
        orderEntity.setDiscount(saveOrderRequest.getDiscount().doubleValue());
        orderEntity.setCoupon(couponEntity);
        orderEntity.setCustomer(customerEntity);
        orderEntity.setDate(new Timestamp(System.currentTimeMillis()));
        orderEntity.setRestaurant(restaurantEntity);
        orderEntity.setPayment(paymentEntity);
        orderEntity.setAddress(addressEntity);

        OrdersEntity updatedOrderEntity = orderService.saveOrder(orderEntity);

        List<ItemQuantity> itemQuantities = saveOrderRequest.getItemQuantities();
        for (ItemQuantity i : itemQuantities) {

            OrderItemEntity orderItemEntity = new OrderItemEntity();

            ItemEntity itemEntity = itemService.getItemByUUID(i.getItemId().toString());

            orderItemEntity.setItem(itemEntity);
            orderItemEntity.setOrder(orderEntity);
            orderItemEntity.setPrice(i.getPrice());
            orderItemEntity.setQuantity(i.getQuantity());

            OrderItemEntity savedOrderItem = orderService.saveOrderItem(orderItemEntity);
        }
        SaveOrderResponse response = new SaveOrderResponse().id(updatedOrderEntity.getUuid()).status("ORDER SUCCESSFULLY PLACED");
        return new ResponseEntity<SaveOrderResponse>(response, HttpStatus.CREATED);
    }
}
