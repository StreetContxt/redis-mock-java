package com.streetcontxt.redis_mock;

public final class IndexOutOfRangeException extends Exception {
    
    public IndexOutOfRangeException() {
        super("ERR index out of range");
    }
}
