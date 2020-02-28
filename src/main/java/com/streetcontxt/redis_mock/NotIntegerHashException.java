package com.streetcontxt.redis_mock;

public final class NotIntegerHashException extends Exception {
    
    public NotIntegerHashException() {
        super("ERR hash value is not an integer");
    }
}
