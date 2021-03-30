package com.upgrad.FoodOrderingApp.service.businness;

import com.upgrad.FoodOrderingApp.service.dao.StateDAO;
import com.upgrad.FoodOrderingApp.service.entity.StateEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
public class StateService {

    @Autowired
    private StateDAO stateDao;

    @Transactional
    public StateEntity getStateById(final Integer stateId) {
        return stateDao.getStateByID(stateId);
    }
}
