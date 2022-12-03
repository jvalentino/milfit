package com.blogspot.jvalentino.milfit.service

class ServiceException extends Exception {

    Exception underlying
    
    ServiceException(String message, Exception e) {
        super(message)
        this.underlying = e
    }
    
    ServiceException(String message) {
        super(message)
    }
}
