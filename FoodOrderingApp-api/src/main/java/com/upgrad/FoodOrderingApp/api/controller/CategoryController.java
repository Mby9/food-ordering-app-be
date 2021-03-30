package com.upgrad.FoodOrderingApp.api.controller;

import com.upgrad.FoodOrderingApp.api.config.Assembler;
import com.upgrad.FoodOrderingApp.api.model.CategoryDetailsResponse;
import com.upgrad.FoodOrderingApp.api.model.CategoryListResponse;
import com.upgrad.FoodOrderingApp.service.businness.CategoryService;
import com.upgrad.FoodOrderingApp.service.entity.CategoryEntity;
import com.upgrad.FoodOrderingApp.service.exception.CategoryNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.ArrayList;
import java.util.List;

@RestController
@CrossOrigin
@RequestMapping("/")
public class CategoryController {

    @Autowired
    private CategoryService categoryBusinessService;

    @Autowired
    private Assembler assembler;

    /**
     *
     * @return All categories stored in database
     */
    @RequestMapping(method = RequestMethod.GET, path = "/category", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<List<CategoryListResponse>> getAllCategories() {

        final List<CategoryEntity> allCategories = categoryBusinessService.getAllCategories();

        // Adding the list of categories to categoriesList
        List<CategoryListResponse> categoriesList = new ArrayList<>();
        for (CategoryEntity n: allCategories) {
            CategoryListResponse categoryDetail = new CategoryListResponse();
            categoryDetail.setCategoryName(n.getCategoryName());
            categoryDetail.setId(UUID.fromString(n.getUuid()));
            categoriesList.add(categoryDetail);
        }

        // return response entity with CategoriesList(details) and Http status
        return new ResponseEntity<>(categoriesList, HttpStatus.OK);
    }

    /**
     *
     * @param categoryId
     * @return Category with full details like items based on given category id
     * @throws CategoryNotFoundException - when category id field is empty
     */
    @RequestMapping(method = RequestMethod.GET, path = "/category/{category_id}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<CategoryDetailsResponse> getCategoryById(@PathVariable("category_id") String categoryId)
            throws CategoryNotFoundException {

        // Throw exception if path variable(category_id) is empty
        if(categoryId == null || categoryId.isEmpty() || categoryId.equalsIgnoreCase("\"\"")){
            throw new CategoryNotFoundException("CNF-001", "Category id field should not be empty");
        }

        // Getting category which matched with given category_id with help of category business service
        final CategoryEntity category = categoryBusinessService.getCategoryEntityByUuid(categoryId);

        // Throw exception if given category_id not matched with any category in DB
        if(category == null){
            throw new CategoryNotFoundException("CNF-002", "No category by this id");
        }

        // Adding the values into categoryDetails
        CategoryDetailsResponse categoryDetails = new CategoryDetailsResponse();
        categoryDetails.setCategoryName(category.getCategoryName());
        categoryDetails.setId(UUID.fromString(category.getUuid()));
        categoryDetails.setItemList(assembler.getItemList(category));

        // return response entity with categoryDetails(details) and Http status
        return new ResponseEntity<>(categoryDetails, HttpStatus.OK);
    }

}
