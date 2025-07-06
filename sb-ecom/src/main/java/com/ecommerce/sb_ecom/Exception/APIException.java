package com.ecommerce.sb_ecom.Exception;

public class APIException extends RuntimeException{
     private static final long SerialversionId=1L;


    public APIException() {
    }

    public APIException(String message) {
        super(message);
    }
}
