package com.upgrad.FoodOrderingApp.api.controller;

import com.upgrad.FoodOrderingApp.api.config.Assembler;
import com.upgrad.FoodOrderingApp.api.model.*;
import com.upgrad.FoodOrderingApp.service.businness.CategoryService;
import com.upgrad.FoodOrderingApp.service.businness.RestaurantService;
import com.upgrad.FoodOrderingApp.service.entity.*;
import com.upgrad.FoodOrderingApp.service.exception.AuthorizationFailedException;
import com.upgrad.FoodOrderingApp.service.exception.CategoryNotFoundException;
import com.upgrad.FoodOrderingApp.service.exception.InvalidRatingException;
import com.upgrad.FoodOrderingApp.service.exception.RestaurantNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

@RestController
public class RestaurantController {

    @Autowired
    private RestaurantService restaurantService;

    @Autowired
    private Assembler assembler;

    @Autowired
    private CategoryService categoryService;

    @RequestMapping(method = RequestMethod.GET, path = "/restaurant", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<RestaurantListResponse> getAllRestaurants() {

        final List<RestaurantEntity> allRestaurants = restaurantService.getAllRestaurants();

        // return response entity with RestaurantLists(details) and Http status
        return new ResponseEntity<>(getRestaurantsResponse(allRestaurants), HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.GET, path = "/restaurant/name/{restaurant_name}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<RestaurantListResponse> getRestaurantsByName(@PathVariable("restaurant_name") String restaurantName)
            throws RestaurantNotFoundException {

        // Throw exception if path variable(restaurant_name) is empty
        if(restaurantName == null || restaurantName.isEmpty() || restaurantName.equalsIgnoreCase("\"\"")){
            throw new RestaurantNotFoundException("RNF-003", "Restaurant name field should not be empty");
        }

        // Getting the list of all restaurants with help of restaurant business service based on input restaurant name
        final List<RestaurantEntity> allRestaurants = restaurantService.getRestaurantsByName(restaurantName);

        // return response entity with RestaurantLists(details) and Http status
        return new ResponseEntity<>(getRestaurantsResponse(allRestaurants), HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.GET, path = "/restaurant/category/{category_id}",
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<RestaurantListResponse> getRestaurantByCategoryId(
            @PathVariable("category_id") String categoryId) throws CategoryNotFoundException {

        // Throw exception if path variable(category_id) is empty
        if(categoryId == null || categoryId.isEmpty() || categoryId.equalsIgnoreCase("\"\"")){
            throw new CategoryNotFoundException("CNF-001", "Category id field should not be empty");
        }

        // Getting category which matched with given category_id with help of category business service
        CategoryEntity matchedCategory = categoryService.getCategoryEntityByUuid(categoryId);

        // Throw exception if given category_id not matched with any category in DB
        if(matchedCategory == null){
            throw new CategoryNotFoundException("CNF-002", "No category by this id");
        }

        // If given category_id match with any category in DataBase, then get all restaurants having matched category
        final List<RestaurantCategoryEntity> allRestaurantCategories = restaurantService.getRestaurantByCategoryId(matchedCategory.getId());

        // Adding the list of restaurants to RestaurantList
        List<RestaurantList> details = new ArrayList<>();
        for (RestaurantCategoryEntity restaurantCategoryEntity:allRestaurantCategories) {
            RestaurantEntity restaurantEntity = restaurantCategoryEntity.getRestaurant();
            RestaurantList detail = getRestaurantList(restaurantEntity);

            // Add category detail to details(RestaurantList)
            details.add(detail);
        }
        RestaurantListResponse response = new RestaurantListResponse();
        response.setRestaurants(details);

        // return response entity with RestaurantLists(details) and Http status
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.GET, path = "/api/restaurant/{restaurant_id}",
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<RestaurantDetailsResponse> getRestaurantByUUId(@PathVariable("restaurant_id") String restaurantId)
            throws RestaurantNotFoundException {

        // Throw exception if path variable(restaurant_id) is empty
        if(restaurantId == null || restaurantId.isEmpty() || restaurantId.equalsIgnoreCase("\"\"")){
            throw new RestaurantNotFoundException("RNF-002", "Restaurant id field should not be empty");
        }

        // Getting restaurant which matched with given restaurant_id with help of restaurant business service
        final RestaurantEntity restaurant = restaurantService.getRestaurantByUUId(restaurantId);

        // Throw exception if given restaurant_id not matched with any restaurant in DB
        if(restaurant == null){
            throw new RestaurantNotFoundException("RNF-001", "No restaurant by this id");
        }

        // Adding the list of restaurant details to RestaurantDetailsResponse
        RestaurantDetailsResponse details = assembler.getRestaurantDetailsResponse(restaurant);

        // Looping categories and setting  values
        List<CategoryList> categoryLists = new ArrayList<>();
        for (CategoryEntity categoryEntity :restaurant.getCategoryEntities()) {
            CategoryList categoryListDetail = new CategoryList();
            categoryListDetail.setId(UUID.fromString(categoryEntity.getUuid()));
            categoryListDetail.setCategoryName(categoryEntity.getCategoryName());

            // Looping items and setting to category
            categoryListDetail.setItemList(assembler.getItemList(categoryEntity));

            // Adding category to category list
            categoryLists.add(categoryListDetail);
        }

        // Add category detail to details(Restaurant)
        details.setCategories(categoryLists);

        // return response entity with RestaurantDetails(details) and Http status
        return new ResponseEntity<>(details, HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.PUT, path = "/api/restaurant/{restaurant_id}",
            consumes = MediaType.APPLICATION_JSON_UTF8_VALUE,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<RestaurantUpdatedResponse> updateCustomerRating(
            @RequestHeader("authorization") final String authorization,
            @RequestParam Double customerRating, @PathVariable("restaurant_id") String restaurantId )
            throws AuthorizationFailedException, InvalidRatingException, RestaurantNotFoundException {

        // Get the bearerToken
        String[] bearerToken = authorization.split("Bearer ");

        // Call the RestaurantBusinessService to update the customer
        RestaurantEntity restaurantEntity = restaurantService.updateCustomerRating(customerRating, restaurantId,
                bearerToken[1]);

        // Attach the details to the updateResponse
        RestaurantUpdatedResponse restaurantUpdatedResponse = new RestaurantUpdatedResponse()
                .id(UUID.fromString(restaurantEntity.getUuid())).status("RESTAURANT RATING UPDATED SUCCESSFULLY");

        // Returns the RestaurantUpdatedResponse with OK http status
        return new ResponseEntity<>(restaurantUpdatedResponse, HttpStatus.OK);
    }

    private RestaurantListResponse getRestaurantsResponse(List<RestaurantEntity> allRestaurants) {

        RestaurantListResponse response = new RestaurantListResponse();

        for (RestaurantEntity restaurantEntity: allRestaurants) {
            RestaurantList detail = getRestaurantList(restaurantEntity);

            // Add category detail to details(RestaurantList)
            response.addRestaurantsItem(detail);
        }

        return response;
    }

    private RestaurantList getRestaurantList(RestaurantEntity restaurantEntity) {
        RestaurantList detail = assembler.getRestaurantList(restaurantEntity);

        List<String> categoryLists = new ArrayList<>();
        for (CategoryEntity categoryEntity : restaurantEntity.getCategoryEntities()) {
            categoryLists.add(categoryEntity.getCategoryName());
        }

        // Sorting category list on name
        Collections.sort(categoryLists);

        // Joining List items as string with comma(,)
        detail.setCategories(String.join(",", categoryLists));
        return detail;
    }

}

