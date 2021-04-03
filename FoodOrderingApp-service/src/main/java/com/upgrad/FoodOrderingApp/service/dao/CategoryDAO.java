package com.upgrad.FoodOrderingApp.service.dao;

import com.upgrad.FoodOrderingApp.service.entity.CategoryEntity;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import java.util.List;

@Repository
@SuppressWarnings("all")
public class CategoryDAO {


    @PersistenceContext
    private EntityManager entityManager;

    public CategoryEntity getCategoryByUuid(String uuid) {
        try {
            CategoryEntity categoryEntity = entityManager.createNamedQuery("getCategoryByUuid", CategoryEntity.class).setParameter("uuid", uuid).getSingleResult();
            return categoryEntity;
        } catch (NoResultException nre) {
            return null;
        }
    }

    //To get list categories  from the database if no result it returns null.
    public List<CategoryEntity> getAllCategoriesOrderedByName() {
        try {
            List<CategoryEntity> categoryEntities = entityManager.createNamedQuery("getAllCategoriesOrderedByName", CategoryEntity.class).getResultList();
            return categoryEntities;
        } catch (NoResultException nre) {
            return null;
        }
    }


}
