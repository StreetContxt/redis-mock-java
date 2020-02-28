package com.streetcontxt.redis_mock;

public final class DiscardWithoutMultiException extends Exception {
    
    public DiscardWithoutMultiException() {
        super("ERR DISCARD without MULTI");
    }
}
