package com.coisee.covoiturage.exception;

public class BusinessRuleException extends RuntimeException {
    public BusinessRuleException(String mssg){
        super(mssg);
    }
}
