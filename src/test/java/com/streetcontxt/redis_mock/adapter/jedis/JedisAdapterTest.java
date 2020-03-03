package com.streetcontxt.redis_mock.adapter.jedis;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Collection;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.exceptions.JedisException;

@RunWith(Parameterized.class)
public class JedisAdapterTest {

    /*
    If a test passes locally with the "plain" JedisAdapter but not the checked one, it could be because a call to an
    unmocked method invokes a superclass (Jedis/BinaryJedis) method that happens to work locally.
    (The automatically-created client runs on localhost, and seems to be accessible to local tests.)
    These tests will NOT pass on CircleCI -- they throw ConnectException, etc. The method still needs to be mocked properly!
     */
    public static Jedis plainJedisMock = new JedisAdapter();
    public static Jedis checkedJedisMock = new CheckedJedisMock().getJedisMock();

    @Before
    public void resetMocks() {
        plainJedisMock = new JedisAdapter();
        checkedJedisMock = new CheckedJedisMock().getJedisMock();
    }

    @Parameterized.Parameter
    public static Jedis mockJedis;

    // Run each test with the plain JedisAdapter, and also the checked JedisAdapter: they should support all the same methods.
    @Parameterized.Parameters
    public static Collection<Object> mockJedisImpls() {
        return Arrays.asList(new Object[] {plainJedisMock, checkedJedisMock});
    }

    @Test
    public void testSetGetExists() {
        assertFalse(mockJedis.exists("key1"));

        assertEquals("OK", mockJedis.set("key1", "value1"));
        assertEquals(mockJedis.get("key1"), "value1");

        assertTrue(mockJedis.exists("key1"));
        assertFalse(mockJedis.exists("key2"));

        assertEquals("OK", mockJedis.set("key2", "value2"));
        assertEquals(mockJedis.get("key2"), "value2");

        assertTrue(mockJedis.exists("key1"));
        assertTrue(mockJedis.exists("key2"));
    }

    @Test
    public void testGetSetByteArray() {
        // these methods are from Jedis's superclass, BinaryJedis
        byte[] key1 = "Key1".getBytes();
        byte[] value1 = "Value1".getBytes();

        byte[] key2 = "Key2".getBytes();
        byte[] value2 = "Value2".getBytes();

        assertEquals("OK", mockJedis.set(key1, value1));
        assertEquals("OK", mockJedis.set(key2, value2));

        byte[] value1Resp = mockJedis.get(key1);
        byte[] value2Resp = mockJedis.get(key2);

        assertTrue(java.util.Arrays.equals(value1, value1Resp));
        assertTrue(java.util.Arrays.equals(value2, value2Resp));
    }

    @Test
    public void testDel() {
        mockJedis.set("key1", "value1");
        mockJedis.set("key2", "value2");
        mockJedis.set("key3", "value3");
        assertTrue(mockJedis.exists("key1"));
        assertTrue(mockJedis.exists("key2"));
        assertTrue(mockJedis.exists("key3"));

        mockJedis.del("key1", "key2");
        assertFalse(mockJedis.exists("key1"));
        assertFalse(mockJedis.exists("key2"));
        assertTrue(mockJedis.exists("key3"));

        mockJedis.del("key3");
        assertFalse(mockJedis.exists("key3"));
    }

    @Test
    public void testPersist() {
        mockJedis.set("key1", "value1");
        assertEquals(0L, (long) mockJedis.persist("key1"));

        mockJedis.expire("key1", 3000);
        assertEquals(1L, (long) mockJedis.persist("key1"));
    }

    @Test
    public void testStrlen() {
        mockJedis.set("key1", "1234567");
        mockJedis.set("key2", "");
        mockJedis.zadd("set1", 1, "key1");

        assertEquals(new Long(7), mockJedis.strlen("key1"));
        assertEquals(new Long(0), mockJedis.strlen("key2"));
        assertEquals(new Long(0), mockJedis.strlen("non_existent_key"));

        try {
            mockJedis.strlen("set1");
        } catch (JedisException e) {
            //ok
        }
    }

    @Test
    public void testZaddZscore() {
        assertEquals(1L, (long) mockJedis.zadd("set1", 1, "key1"));
        assertEquals(1L, (long) mockJedis.zadd("set1", 2, "key2"));

        //returns 0 when re-adding existing value
        assertEquals(0L, (long) mockJedis.zadd("set1", 2, "key2"));
        assertEquals(0L, (long) mockJedis.zadd("set1", 4, "key2"));

        assertEquals(new Double(1), mockJedis.zscore("set1", "key1"));
        assertEquals(new Double(4), mockJedis.zscore("set1", "key2"));
    }

}
