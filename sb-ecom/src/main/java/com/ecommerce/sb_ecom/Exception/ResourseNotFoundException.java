package com.ecommerce.sb_ecom.Exception;

public class ResourseNotFoundException extends RuntimeException {

    String resourceName;
    String Field;
    String Fieldname;
    Long id;

    public ResourseNotFoundException(String resourceName, String field, String fieldname) {
      super(String.format("%s not found with %s:%s",resourceName,field,fieldname));
        this.resourceName = resourceName;
        Field = field;
        Fieldname = fieldname;
    }

    public ResourseNotFoundException( String resourceName, String field, Long id) {
        super(String.format("%s not found with %s:%d",resourceName,field,id));
        this.resourceName = resourceName;
        Field = field;
        this.id = id;
    }

    public ResourseNotFoundException() {

    }
}
