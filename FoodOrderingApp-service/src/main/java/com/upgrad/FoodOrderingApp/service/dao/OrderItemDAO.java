package com.upgrad.FoodOrderingApp.service.dao;

import com.upgrad.FoodOrderingApp.service.entity.OrdersEntity;
import com.upgrad.FoodOrderingApp.service.entity.OrderItemEntity;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import java.util.List;

@Repository
public class OrderItemDAO {

    @PersistenceContext
    private EntityManager entityManager;

    //Get Items by Order
    public List<OrderItemEntity> getItemsByOrders(OrdersEntity orderEntity) {
        try {
            List<OrderItemEntity> orderItemEntities = entityManager.createNamedQuery("getItemsByOrders", OrderItemEntity.class).setParameter("order", orderEntity).getResultList();
            return orderItemEntities;
        } catch (NoResultException nre) {
            return null;
        }
    }

    //get order items by order
    public List<OrderItemEntity> getOrderItemsByOrder(OrdersEntity orderEntity) {
        try {
            List<OrderItemEntity> orderItemEntities = entityManager.createNamedQuery("getOrderItemByOrder", OrderItemEntity.class).setParameter("order", orderEntity).getResultList();
            return orderItemEntities;
        } catch (NoResultException nre) {
            return null;
        }
    }
}