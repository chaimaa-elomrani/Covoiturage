package com.coisee.covoiturage.exception;

public class ResourceNotFoundException extends RuntimeException{

    public ResourceNotFoundException(String mssg){
        super(mssg);
    }
}
