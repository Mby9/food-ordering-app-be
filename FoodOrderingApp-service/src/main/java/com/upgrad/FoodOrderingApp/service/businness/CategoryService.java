package com.upgrad.FoodOrderingApp.service.businness;

import com.upgrad.FoodOrderingApp.service.dao.CategoryDAO;
import com.upgrad.FoodOrderingApp.service.dao.CategoryItemDAO;
import com.upgrad.FoodOrderingApp.service.dao.RestaurantCategoryDAO;
import com.upgrad.FoodOrderingApp.service.dao.RestaurantDAO;
import com.upgrad.FoodOrderingApp.service.entity.CategoryEntity;
import com.upgrad.FoodOrderingApp.service.entity.RestaurantCategoryEntity;
import com.upgrad.FoodOrderingApp.service.entity.RestaurantEntity;
import com.upgrad.FoodOrderingApp.service.exception.CategoryNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.LinkedList;
import java.util.List;

@Service
public class CategoryService {

    @Autowired
    private RestaurantCategoryDAO restaurantCategoryDao;

    @Autowired
    private RestaurantDAO restaurantDao;

    @Autowired
    private CategoryDAO categoryDao;

    @Autowired
    private CategoryItemDAO categoryItemDao;

    public List<CategoryEntity> getCategoriesByRestaurant(String restaurantUuid) {

        //Getting restaurant entity by using restaurant id
        RestaurantEntity restaurantEntity = restaurantDao.restaurantByUUID(restaurantUuid);

        //Getting all the category in a restaurant
        List<RestaurantCategoryEntity> restaurantCategoryEntities = restaurantCategoryDao.getCategoriesByRestaurant(restaurantEntity);

        //Creating the list of the Category entity which will be returned.
        List<CategoryEntity> categoryEntities = new LinkedList<>();
        restaurantCategoryEntities.forEach(restaurantCategoryEntity -> {
            categoryEntities.add(restaurantCategoryEntity.getCategoryId());
        });
        return categoryEntities;
    }

    public List<CategoryEntity> getAllCategoriesOrderedByName() {
        //Calls getAllCategoriesOrderedByName of categoryDao to get list of CategoryEntity
        List<CategoryEntity> categoryEntities = categoryDao.getAllCategoriesOrderedByName();
        return categoryEntities;
    }

    public List<CategoryEntity> getAllCategories() {

        //Calls all getCategories
        List<CategoryEntity> categoryEntities = restaurantCategoryDao.getAllCategories();
        return categoryEntities;
    }

    //This method will get the category by category ID
    public CategoryEntity getCategoryById(String categoryUuid) throws CategoryNotFoundException {
        if (categoryUuid == null || categoryUuid == "") {
            throw new CategoryNotFoundException("CNF-001", "Category id field should not be empty");
        }
        //Calls getCategoryByUuid of categoryDao to get CategoryEntity
        CategoryEntity categoryEntity = categoryDao.getCategoryByUuid(categoryUuid);
        //Checking for categoryEntity to be null or empty to throw exception.
        if (categoryEntity == null) {
            throw new CategoryNotFoundException("CNF-002", "No category by this id");
        }
        return categoryEntity;
    }
}
