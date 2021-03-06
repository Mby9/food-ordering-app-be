package com.upgrad.FoodOrderingApp.service.dao;


import com.upgrad.FoodOrderingApp.service.entity.RestaurantEntity;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import java.util.List;

@Repository
@SuppressWarnings("all")
public class RestaurantDAO {

    @PersistenceContext
    private EntityManager entityManager;

    //To get the list of restaurant by ratings from db
    public List<RestaurantEntity> restaurantsByRating() {
        try {
            List<RestaurantEntity> restaurantEntities = entityManager.createNamedQuery("restaurantsByRating", RestaurantEntity.class).getResultList();
            return restaurantEntities;
        } catch (NoResultException nre) {
            return null;
        }
    }

    public RestaurantEntity restaurantByUUID(String uuid) {
        try {
            return entityManager
                    .createNamedQuery("getRestaurantByUuid", RestaurantEntity.class)
                    .setParameter("uuid", uuid)
                    .getSingleResult();
        } catch (NoResultException nre) {

            return null;
        }
    }

    //To get the list of restaurant by name from db
    public List<RestaurantEntity> restaurantsByName(String restaurantName) {
        try {
            String restaurantNameLow = "%" + restaurantName.toLowerCase() + "%"; // to make a check with lower
            List<RestaurantEntity> restaurantEntities = entityManager.createNamedQuery("restaurantsByName", RestaurantEntity.class).setParameter("restaurant_name_low", restaurantNameLow).getResultList();
            return restaurantEntities;
        } catch (NoResultException nre) {
            return null;
        }

    }

    //To update the restaurant in the db and return updated restaurant entity.
    public RestaurantEntity updateRestaurantRating(RestaurantEntity restaurantEntity) {
        entityManager.merge(restaurantEntity);
        return restaurantEntity;
    }


}
