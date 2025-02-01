package com.haleluque.ecommerce.exceptions;

public class ProductException extends RuntimeException{
    String resourceName;
    String field;

    Long fieldId;

    public ProductException() {
    }

    public ProductException(String message, String resourceName, Long fieldId) {
        super(message);
        this.resourceName = resourceName;
        this.fieldId = fieldId;
    }
}
