package com.streetcontxt.redis_mock.adapter.jedis;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.exceptions.JedisException;

/**
 * CheckedJedis is verified more thoroughly in JedisAdapterTest... this test just verifies
 * functionality that isn't part of JedisAdapter.
 */
public class CheckedJedisMockTest {

    @Test
    public void createJedisMock() {
        CheckedJedisMock creator = new CheckedJedisMock();
        Jedis mockJedis = creator.getJedisMock();
        mockJedis.set("key1", "value1");
        assertEquals("value1", mockJedis.get("key1"));
    }

    /**
     * This test only works if there are still unimplemented methods...
     * For now, it tests methods that seem unlikely to be implemented any time soon. If they're implemented
     * and this test breaks, just change it to call some other unimplemented method.
     */
    @Test
    public void detectUnimplementedMethods() {
        CheckedJedisMock creator = new CheckedJedisMock();
        Jedis mockJedis = creator.getJedisMock();

        try {
            // from Jedis
            mockJedis.clusterSlaves("ip");
        } catch (UnsupportedOperationException e) {
        }

        try {
            // from BinaryJedis (Jedis's superclass)
            mockJedis.decr("test".getBytes());
        } catch (UnsupportedOperationException e) {
        }
    }

    /**
     * Ensures that if the mocked method throws an exception (e.g. an actual Redis exception),
     * the original exception is thrown to the caller rather than an InvocationTargetException.
     */
    @Test(expected = JedisException.class)
    public void throwOriginalExceptions() {
        CheckedJedisMock creator = new CheckedJedisMock();
        Jedis mockJedis = creator.getJedisMock();
        mockJedis.zadd("set1", 1, "key1");
        mockJedis.strlen("set1"); // throws "wrong type" exception
    }

    /**
     * Ensures the logic that detects unmocked methods doesn't prevent methods inherited from Object from being called.
     */
    @Test
    public void allowObjectMethodInvocation() {
        CheckedJedisMock creator = new CheckedJedisMock();
        Jedis mockJedis = creator.getJedisMock();
        assertTrue(mockJedis.getClass().getSimpleName().contains("Jedis"));
    }

}
