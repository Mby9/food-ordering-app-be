package com.upgrad.FoodOrderingApp.service.businness;

import com.upgrad.FoodOrderingApp.service.dao.CouponDAO;
import com.upgrad.FoodOrderingApp.service.entity.CouponEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
public class CouponService {

    @Autowired
    private CouponDAO couponDao;

    @Transactional
    public CouponEntity getCouponById(final Long couponId) {
        return couponDao.getCouponById(couponId);
    }

    @Transactional
    public CouponEntity getCouponByUuid(final String couponUuid) {
        return couponDao.getCouponByUuid(couponUuid);
    }

}
