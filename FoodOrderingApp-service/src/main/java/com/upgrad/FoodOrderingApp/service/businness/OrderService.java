package com.upgrad.FoodOrderingApp.service.businness;

import com.upgrad.FoodOrderingApp.service.dao.*;
import com.upgrad.FoodOrderingApp.service.entity.CouponEntity;
import com.upgrad.FoodOrderingApp.service.entity.CustomerEntity;
import com.upgrad.FoodOrderingApp.service.entity.OrdersEntity;
import com.upgrad.FoodOrderingApp.service.entity.OrderItemEntity;
import com.upgrad.FoodOrderingApp.service.exception.AuthorizationFailedException;
import com.upgrad.FoodOrderingApp.service.exception.CouponNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class OrderService {

    @Autowired
    private CustomerDAO customerDao;

    @Autowired
    CouponDAO couponDao;

    @Autowired
    OrderDAO orderDao;

    @Autowired
    OrderItemDAO orderItemDao;

    @Autowired
    AddressDAO addressDao;

    @Autowired
    PaymentDAO paymentDao;

    @Autowired
    CustomerAddressDAO customerAddressDao;

    @Transactional(propagation = Propagation.REQUIRED)
    public CouponEntity getCouponByCouponName(final String couponName) throws AuthorizationFailedException,
            CouponNotFoundException {
        if (couponName == null || couponName == "" || couponName.isEmpty()) {
            throw new CouponNotFoundException("CPF-002", "Coupon name field should not be empty");
        }
        CouponEntity couponEntity = couponDao.getCouponByName(couponName);
        if (couponEntity == null) {
            throw new CouponNotFoundException("CPF-001", "No coupon by this name");
        }
        return couponEntity;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public CouponEntity getCouponByCouponId(final String couponUuid) throws CouponNotFoundException {
        CouponEntity couponEntity = couponDao.getCouponByUUID(couponUuid);
        if (couponEntity == null) {
            throw new CouponNotFoundException("CPF-002", "No coupon by this id");
        }
        return couponEntity;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public List<OrdersEntity> getOrdersByCustomers(String customerUuid) {

        CustomerEntity customerEntity = customerDao.getCustomerByUuid(customerUuid);

        List<OrdersEntity> ordersEntities = orderDao.getOrdersByCustomers(customerEntity);
        return ordersEntities;
    }

    public List<OrderItemEntity> getOrderItemsByOrder(OrdersEntity orderEntity) {
        List<OrderItemEntity> orderItemEntities = orderItemDao.getOrderItemsByOrder(orderEntity);
        return orderItemEntities;
    }

    //Save Order in Database
    @Transactional(propagation = Propagation.REQUIRED)
    public OrdersEntity saveOrder(OrdersEntity orderEntity) {
        return orderDao.saveOrder(orderEntity);
    }

    //Save Order Item
    @Transactional(propagation = Propagation.REQUIRED)
    public OrderItemEntity saveOrderItem(OrderItemEntity orderedItem) {
        return orderDao.saveOrderItem(orderedItem);
    }


}