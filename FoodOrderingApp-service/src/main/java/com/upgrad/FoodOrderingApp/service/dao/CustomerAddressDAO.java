package com.upgrad.FoodOrderingApp.service.dao;

import com.upgrad.FoodOrderingApp.service.entity.AddressEntity;
import com.upgrad.FoodOrderingApp.service.entity.CustomerAddressEntity;
import com.upgrad.FoodOrderingApp.service.entity.CustomerEntity;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import java.util.ArrayList;
import java.util.List;

@Repository
public class CustomerAddressDAO {

    @PersistenceContext
    private EntityManager entityManager;

    public void createCustomerAddress(CustomerAddressEntity customerAddressEntity) {
        entityManager.persist(customerAddressEntity);
    }

    public List<AddressEntity> getAddressForCustomerByUuid(final String uuid) {
        try {
            CustomerEntity customerEntity = entityManager.createNamedQuery("customerByUuid", CustomerEntity.class)
                    .setParameter("uuid", uuid).getSingleResult();

            List<CustomerAddressEntity> customerAddressEntities = entityManager
                    .createNamedQuery("customerAddressesListByCustomerId", CustomerAddressEntity.class)
                    .setParameter("customer", customerEntity).getResultList();

            if (customerAddressEntities.size() == 0) {
                return null;
            }

            List<Integer> ids = new ArrayList<>();

            for (CustomerAddressEntity cae : customerAddressEntities) {
                ids.add(cae.getAddress().getId());
            }

            return entityManager
                    .createQuery("SELECT a FROM AddressEntity a WHERE a.id in :addressIds AND a.active = 1 order by a.id desc",
                    AddressEntity.class).setParameter("addressIds", ids).getResultList();

        } catch (NoResultException nre) {
            return null;
        }
    }

    public CustomerAddressEntity getCustAddressByCustIdAddressId(final CustomerEntity customerEntity, final AddressEntity addressEntity) {
        try {
            return entityManager.createNamedQuery("custAddressByCustIdAddressId", CustomerAddressEntity.class)
                    .setParameter("customer", customerEntity).setParameter( "address", addressEntity)
                    .getSingleResult();
        } catch(NoResultException nre) {
            return null;
        }
    }

    //Retrieve the list of addresses of a particular customer
    public List<AddressEntity> getCustomerAddressListByCustomer(CustomerEntity customerEntity){
        try{
            List<AddressEntity> addressEntityList = new ArrayList<>();
            List<CustomerAddressEntity> customerAddressEntityList = this.entityManager
                    .createNamedQuery("customerAddressesByCustomerId", CustomerAddressEntity.class)
                    .setParameter("customer", customerEntity).getResultList();
            for(CustomerAddressEntity customerAddressEntity:customerAddressEntityList){
                addressEntityList.add(customerAddressEntity.getAddress());
            }
            return addressEntityList;
        }catch (NoResultException nre){
            return null;
        }
    }
}