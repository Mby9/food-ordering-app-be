package com.upgrad.FoodOrderingApp.service.businness;

import com.upgrad.FoodOrderingApp.service.dao.CategoryDAO;
import com.upgrad.FoodOrderingApp.service.dao.RestaurantDAO;
import com.upgrad.FoodOrderingApp.service.entity.CategoryEntity;
import com.upgrad.FoodOrderingApp.service.entity.RestaurantEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class CategoryService {

    @Autowired
    private CategoryDAO categoryDao;

    @Autowired
    private RestaurantDAO restaurantDAO;

    // A Method which takes the categoryId as parameter for  getCategoryEntityById endpoint
    public CategoryEntity getCategoryEntityById(final Integer categoryId){
        return  categoryDao.getCategoryById(categoryId);
    }

    // A Method which takes the categoryUUId as parameter for  getCategoryEntityByUUId endpoint
    public CategoryEntity getCategoryEntityByUuid(final String categoryUUId){
        return  categoryDao.getCategoryByUUId(categoryUUId);
    }

    // A Method which is for  getAllCategories endpoint
    public List<CategoryEntity> getAllCategories(){
        return  categoryDao.getAllCategories();
    }

    //List all categories mapped to a restaurant - list by restaurant UUID
    public List<CategoryEntity> getCategoriesByRestaurant(String restaurantUUID) {
        RestaurantEntity restaurantEntity = restaurantDAO.getRestaurantByUUId(restaurantUUID);
        return restaurantEntity.getCategoryEntities().stream()
                .sorted(Comparator.comparing(CategoryEntity::getCategoryName))
                .collect(Collectors.toList());
    }
}
