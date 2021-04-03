package com.upgrad.FoodOrderingApp.api.exception;



import com.upgrad.FoodOrderingApp.service.exception.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import java.util.Map;
import java.util.HashMap;



@ControllerAdvice
public class RestExceptionHandler {
    /* signup */
    @ExceptionHandler(SignUpRestrictedException.class)
    public ResponseEntity<Map<String, Object>> signup(SignUpRestrictedException exception, WebRequest request) {
        Map errorMap = new HashMap();
        errorMap.put("code", exception.getCode());
        errorMap.put("message", exception.getErrorMessage());
        return new ResponseEntity(errorMap, HttpStatus.BAD_REQUEST);
    }

    /* authentication failed */
    @ExceptionHandler(AuthenticationFailedException.class)
    public ResponseEntity<Map<String, Object>> authFailExc(AuthenticationFailedException exception, WebRequest request) {
        Map errorMap = new HashMap();
        errorMap.put("code", exception.getCode());
        errorMap.put("message", exception.getErrorMessage());
        return new ResponseEntity(errorMap, HttpStatus.UNAUTHORIZED);
    }

    /* authorization failed */
    @ExceptionHandler(AuthorizationFailedException.class)
    public ResponseEntity<Map<String, Object>> authFail(AuthorizationFailedException exception, WebRequest request) {
        Map errorMap = new HashMap();
        errorMap.put("code", exception.getCode());
        errorMap.put("message", exception.getErrorMessage());
        return new ResponseEntity(errorMap, HttpStatus.FORBIDDEN);
    }

    /* update failed */
    @ExceptionHandler(UpdateCustomerException.class)
    public ResponseEntity<Map<String, Object>> customerUpdateFail(UpdateCustomerException exception, WebRequest request) {
        Map errorMap = new HashMap();
        errorMap.put("code", exception.getCode());
        errorMap.put("message", exception.getErrorMessage());
        return new ResponseEntity(errorMap, HttpStatus.BAD_REQUEST);
    }

    /* saved address failed */
    @ExceptionHandler(SaveAddressException.class)
    public ResponseEntity<Map<String, Object>> saveAddressFail(SaveAddressException exception, WebRequest request) {
        Map errorMap = new HashMap();
        errorMap.put("code", exception.getCode());
        errorMap.put("message", exception.getErrorMessage());
        return new ResponseEntity(errorMap, HttpStatus.BAD_REQUEST);
    }

    /* address not found */
    @ExceptionHandler(AddressNotFoundException.class)
    public ResponseEntity<Map<String, Object>> addressNotFound(AddressNotFoundException exception, WebRequest request) {
        Map errorMap = new HashMap();
        errorMap.put("code", exception.getCode());
        errorMap.put("message", exception.getErrorMessage());
        return new ResponseEntity(errorMap, HttpStatus.NOT_FOUND);
    }

    /* restaurant not found */
    @ExceptionHandler(RestaurantNotFoundException.class)
    public ResponseEntity<Map<String, Object>> restaurantNotFound(RestaurantNotFoundException exception, WebRequest request) {
        Map errorMap = new HashMap();
        errorMap.put("code", exception.getCode());
        errorMap.put("message", exception.getErrorMessage());
        return new ResponseEntity(errorMap, HttpStatus.NOT_FOUND);
    }

    /* category not found */
    @ExceptionHandler(CategoryNotFoundException.class)
    public ResponseEntity<Map<String, Object>> categoryNotFound(CategoryNotFoundException exception, WebRequest request) {
        Map errorMap = new HashMap();
        errorMap.put("code", exception.getCode());
        errorMap.put("message", exception.getErrorMessage());
        return new ResponseEntity(errorMap, HttpStatus.NOT_FOUND);
    }
    /* Coupon not found */
    @ExceptionHandler(CouponNotFoundException.class)
    public ResponseEntity<Map<String, Object>> couponNotFound(CouponNotFoundException exception, WebRequest request) {
        Map errorMap = new HashMap();
        errorMap.put("code", exception.getCode());
        errorMap.put("message", exception.getErrorMessage());
        return new ResponseEntity(errorMap, HttpStatus.NOT_FOUND);
    }
    /* payemnt method not found */
    @ExceptionHandler(PaymentMethodNotFoundException.class)
    public ResponseEntity<Map<String, Object>> paymentMethodNotFound(PaymentMethodNotFoundException exception, WebRequest request) {
        Map errorMap = new HashMap();
        errorMap.put("code", exception.getCode());
        errorMap.put("message", exception.getErrorMessage());
        return new ResponseEntity(errorMap, HttpStatus.NOT_FOUND);
    }
    /* Item method not found */
    @ExceptionHandler(ItemNotFoundException.class)
    public ResponseEntity<Map<String, Object>> itemNotFound(ItemNotFoundException exception, WebRequest request) {
        Map errorMap = new HashMap();
        errorMap.put("code", exception.getCode());
        errorMap.put("message", exception.getErrorMessage());
        return new ResponseEntity(errorMap, HttpStatus.NOT_FOUND);
    }
}
