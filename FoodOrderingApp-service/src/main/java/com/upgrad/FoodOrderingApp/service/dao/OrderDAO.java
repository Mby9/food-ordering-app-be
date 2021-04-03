package com.upgrad.FoodOrderingApp.service.dao;

import com.upgrad.FoodOrderingApp.service.entity.CustomerEntity;
import com.upgrad.FoodOrderingApp.service.entity.OrdersEntity;
import com.upgrad.FoodOrderingApp.service.entity.OrderItemEntity;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import java.util.List;

@Repository
public class OrderDAO {

    @PersistenceContext
    private EntityManager entityManager;

    //Get Orders By Customers
    public List<OrdersEntity> getOrdersByCustomers(CustomerEntity customerEntity) {
        try {
            List<OrdersEntity> ordersEntities = entityManager.createNamedQuery("getOrdersByCustomer", OrdersEntity.class).setParameter("customer", customerEntity).getResultList();
            return ordersEntities;
        } catch (NoResultException nre) {
            return null;
        }
    }

    //Persist Order
    public OrdersEntity saveOrder(OrdersEntity orderEntity) {
        try {
            entityManager.persist(orderEntity);
            return orderEntity;
        } catch (Exception e) {
            return null;
        }
    }

    //Persist Order Item
    public OrderItemEntity saveOrderItem(OrderItemEntity orderedItem) {
        try {
            entityManager.persist(orderedItem);
            return orderedItem;
        } catch (Exception e) {
            return null;
        }
    }
}