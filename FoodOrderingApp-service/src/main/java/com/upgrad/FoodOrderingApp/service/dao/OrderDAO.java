package com.upgrad.FoodOrderingApp.service.dao;

import com.upgrad.FoodOrderingApp.service.entity.CouponEntity;
import com.upgrad.FoodOrderingApp.service.entity.CustomerEntity;
import com.upgrad.FoodOrderingApp.service.entity.OrdersEntity;
import com.upgrad.FoodOrderingApp.service.entity.RestaurantEntity;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import java.util.List;

@Repository
public class OrderDAO {

    @PersistenceContext
    private EntityManager entityManager;

    public CouponEntity getCouponByName(String couponName) {
        try {
            return entityManager.createNamedQuery("couponByName", CouponEntity.class).setParameter("couponName", couponName)
                    .getSingleResult();
        } catch(NoResultException nre) {
            return null;
        }
    }

    public List<OrdersEntity> getCustomerOrders(CustomerEntity customerEntity) {
        try {
            return entityManager.createNamedQuery("ordersByCustomer", OrdersEntity.class).setParameter("customer", customerEntity)
                    .getResultList();
        } catch(NoResultException nre) {
            return null;
        }
    }

    public OrdersEntity saveOrder(OrdersEntity ordersEntity) {
        entityManager.persist(ordersEntity);
        return ordersEntity;
    }

    public List<OrdersEntity> getOrdersByRestaurant(final RestaurantEntity restaurantEntity) {
        try {
            return entityManager.createNamedQuery("ordersByRestaurant", OrdersEntity.class)
                    .setParameter("restaurant", restaurantEntity).getResultList();
        } catch (NoResultException nre) {
            return null;
        }
    }

    //Get coupon by coupon UUID
    public CouponEntity getCouponByCouponUUID(String uuid) {
        try {
            return entityManager.createNamedQuery("couponByUUID", CouponEntity.class).setParameter("uuid", uuid).getSingleResult();
        } catch (NoResultException nre) {
            return null;
        }
    }
}
