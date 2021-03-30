package com.upgrad.FoodOrderingApp.service.businness;

import com.upgrad.FoodOrderingApp.service.dao.OrderDAO;
import com.upgrad.FoodOrderingApp.service.dao.OrderItemDAO;
import com.upgrad.FoodOrderingApp.service.entity.*;
import com.upgrad.FoodOrderingApp.service.exception.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class OrderService {

    @Autowired
    private OrderDAO orderDao;

    @Autowired
    private AuthService authService;

    @Autowired
    private Validator validator;

    @Autowired
    private OrderItemDAO orderItemDao;

    @Transactional
    public CouponEntity getCouponByName(String couponName, final String authorizationToken) throws AuthorizationFailedException {

        // Validates the access token retrieved from database
        CustomerAuthEntity customerAuthEntity = authService.validateToken(authorizationToken);
        validator.validateAccessToken(customerAuthEntity);
        return orderDao.getCouponByName(couponName);
    }

    public CouponEntity getCouponByCouponId(String uuid) throws CouponNotFoundException {
        CouponEntity couponEntity = orderDao.getCouponByCouponUUID(uuid);

        if (couponEntity == null) {
            throw new CouponNotFoundException("CPF-001", "No coupon by this id");
        }
        return couponEntity;
    }

    @Transactional
    public List<OrdersEntity> getCustomerOrders(final CustomerEntity customerEntity) {

        return orderDao.getCustomerOrders(customerEntity);
    }

    @Transactional
    public OrdersEntity saveOrder(OrdersEntity ordersEntity, List<OrderItemEntity> orderItems) {

        // Saves the order by calling saveOrder
        OrdersEntity savedOrderEntity = orderDao.saveOrder(ordersEntity);

        // Loops thru getItemQuantities and loads the OrderItemEntity
        for (OrderItemEntity orderItemEntity : orderItems) {
            orderItemEntity.setOrders(savedOrderEntity);

            // Saves the order item by calling createOrderItemEntity of orderItemDao
            orderItemDao.createOrderItemEntity(orderItemEntity);
        }

        // Returns the savedOrderEntity from the orderDao
        return orderDao.saveOrder(savedOrderEntity);
    }

    //Save items included in an order
    @Transactional(propagation = Propagation.REQUIRED)
    public OrderItemEntity saveOrderItem(OrderItemEntity orderItemEntity) {
        return orderItemDao.createOrderItemEntity(orderItemEntity);
    }

}
