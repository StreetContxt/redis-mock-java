package com.streetcontxt.redis_mock;

public final class NoKeyException extends Exception {
    
    public NoKeyException() {
        super("ERR no such key");
    }
}
