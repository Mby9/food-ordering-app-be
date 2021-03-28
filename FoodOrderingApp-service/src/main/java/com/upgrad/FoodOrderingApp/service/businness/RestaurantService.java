package com.upgrad.FoodOrderingApp.service.businness;
import com.upgrad.FoodOrderingApp.service.dao.RestaurantDAO;
import com.upgrad.FoodOrderingApp.service.entity.*;
import com.upgrad.FoodOrderingApp.service.exception.AuthorizationFailedException;
import com.upgrad.FoodOrderingApp.service.exception.CategoryNotFoundException;
import com.upgrad.FoodOrderingApp.service.exception.InvalidRatingException;
import com.upgrad.FoodOrderingApp.service.exception.RestaurantNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import com.upgrad.FoodOrderingApp.service.entity.CustomerAuthEntity;
import org.springframework.stereotype.Service;

import javax.persistence.NoResultException;
import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class RestaurantService {
    @Autowired
    private RestaurantDAO restaurantDao;

    @Autowired
    private AuthService authService;

    @Autowired
    private Validator validator;

    public List<RestaurantEntity> getAllRestaurants() {
        return restaurantDao.getAllRestaurants();
    }

    public List<RestaurantEntity> getRestaurantsByName(String restaurantName) {
        return restaurantDao.getRestaurantsByName(restaurantName);
    }

    public List<RestaurantCategoryEntity> getRestaurantByCategoryId(final Long categoryID) {
        return restaurantDao.getRestaurantByCategoryId(categoryID);
    }
    public RestaurantEntity getRestaurantByUUId(String restaurantUUID) {
        return restaurantDao.getRestaurantByUUId(restaurantUUID);
    }

    @Transactional
    public RestaurantEntity updateCustomerRating (final Double customerRating, final String restaurant_id, final String authorizationToken)
            throws AuthorizationFailedException, RestaurantNotFoundException, InvalidRatingException {

        final ZonedDateTime now = ZonedDateTime.now();

        // Validating the customer using the authorizationToken
        CustomerAuthEntity customerAuthEntity = authService.validateToken(authorizationToken);
        validator.validateAccessToken(customerAuthEntity);

        // Throw exception if path variable(restaurant_id) is empty
        if(restaurant_id == null || restaurant_id.isEmpty() || restaurant_id.equalsIgnoreCase("\"\"")){
            throw new RestaurantNotFoundException("RNF-002", "Restaurant id field should not be empty");
        }

        //get the restaurant Details using the restaurantUuid
        RestaurantEntity restaurantEntity =  restaurantDao.getRestaurantByUUId(restaurant_id);

        if (restaurantEntity == null) {
            throw new RestaurantNotFoundException("RNF-001", "No restaurant by this id");
        }

        // Throw exception if path variable(restaurant_id) is empty
        if(customerRating == null || customerRating.isNaN() || customerRating < 1 || customerRating > 5 ){
            throw new InvalidRatingException("IRE-001", "Restaurant should be in the range of 1 to 5");
        }

        // Now calculate new customer rating  and set the updated rating and attach it to the restaurantEntity
        BigDecimal oldRatingCalculation = (restaurantEntity.getCustomerRating().multiply(new BigDecimal(restaurantEntity.getNumCustomersRated())));
        BigDecimal calculatedRating = (oldRatingCalculation.add(new BigDecimal(customerRating))).divide(new BigDecimal(restaurantEntity.getNumCustomersRated() + 1));
        restaurantEntity.setCustomerRating(calculatedRating);
        restaurantEntity.setNumCustomersRated(restaurantEntity.getNumCustomersRated() + 1);

        //called restaurantDao to merge the content and update in the database
        restaurantDao.updateRestaurant(restaurantEntity);
        return restaurantEntity;
    }

}