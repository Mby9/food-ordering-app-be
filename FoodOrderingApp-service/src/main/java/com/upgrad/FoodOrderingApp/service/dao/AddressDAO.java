package com.upgrad.FoodOrderingApp.service.dao;

import com.upgrad.FoodOrderingApp.service.entity.AddressEntity;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import java.util.UUID;

@Repository
public class AddressDAO {

    @PersistenceContext
    EntityManager entityManager;

    public AddressEntity createAddress(AddressEntity address) {
        entityManager.persist(address);
        return address;
    }

    public AddressEntity getAddressByUUID(String id) {
        try {
            return entityManager
                    .createNamedQuery("addressByUUID", AddressEntity.class)
                    .setParameter("uuid", id)
                    .getSingleResult();
        } catch (NoResultException nre) {
            return null;
        }
    }

    public AddressEntity getAddressById(final Integer addressId) {
        try {
            return entityManager.createNamedQuery("addressById", AddressEntity.class).setParameter("id", addressId)
                    .getSingleResult();
        } catch(NoResultException nre) {
            return null;
        }

    }

    public AddressEntity getAddressByUuid(final String addressUuid) {
        try {
            return entityManager.createNamedQuery("addressByUUID", AddressEntity.class).setParameter("uuid", addressUuid)
                    .getSingleResult();
        } catch(NoResultException nre) {
            return null;
        }

    }

    public AddressEntity deleteAddress(AddressEntity addressEntity){
        addressEntity.setActive(0);
        this.entityManager.merge(addressEntity);
        return addressEntity;
    }


}
