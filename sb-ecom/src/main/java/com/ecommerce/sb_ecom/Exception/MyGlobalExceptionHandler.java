package com.ecommerce.sb_ecom.Exception;


import com.ecommerce.sb_ecom.Payload.APIResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice

public class MyGlobalExceptionHandler {


    @ExceptionHandler(MethodArgumentNotValidException.class)

   public ResponseEntity<Map<String,String>> MethodArgumentNotvalidException(MethodArgumentNotValidException e){
        Map<String,String> response= new HashMap<>();
        e.getBindingResult().getAllErrors().forEach(err->{
            String Fieldname=((FieldError) err).getField();
            String Message=err.getDefaultMessage();
            response.put(Fieldname,Message);
        });
        return new ResponseEntity<Map<String,String>>(response, HttpStatus.BAD_REQUEST);
    }
    @ExceptionHandler(ResourseNotFoundException.class)
    public ResponseEntity<APIResponse>MyResponseNotFoundException(ResourseNotFoundException e){
        String message= e.getMessage();
        APIResponse apiResponse= new APIResponse(message,false);
       return new ResponseEntity<>(apiResponse,HttpStatus.NOT_FOUND);
    }
    @ExceptionHandler(APIException.class)
    public ResponseEntity<APIResponse>MyAPIException(APIException e){
        String message=e.getMessage();
        APIResponse apiResponse= new APIResponse(message,false);
        return new ResponseEntity<>(apiResponse,HttpStatus.BAD_REQUEST);
    }



}
