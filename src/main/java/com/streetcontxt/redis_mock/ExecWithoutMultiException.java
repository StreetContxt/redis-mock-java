package com.streetcontxt.redis_mock;

public final class ExecWithoutMultiException extends Exception {
    
    public ExecWithoutMultiException() {
        super("ERR EXEC without MULTI");
    }
}
