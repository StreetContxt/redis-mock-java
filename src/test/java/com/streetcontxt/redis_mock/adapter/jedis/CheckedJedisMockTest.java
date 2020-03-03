package com.streetcontxt.redis_mock.adapter.jedis;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import redis.clients.jedis.Jedis;

public class CheckedJedisMockTest {

  // CheckedJedis is verified more thoroughly in JedisAdapterTest... this test just verifies
  // functionality that doesn't come from JedisAdapter.

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
      // from Jedis proper
      mockJedis.clusterSlaves("ip");
    } catch(UnsupportedOperationException e) {
    }

    try {
      // from BinaryJedis superclass
      mockJedis.decr("test".getBytes());
    } catch(UnsupportedOperationException e) {
    }
  }

  @Test
  public void allowObjectMethodInvokation() {
    CheckedJedisMock creator = new CheckedJedisMock();
    Jedis mockJedis = creator.getJedisMock();

    // Ensure the logic that detects unmocked methods allows methods from Object to be called.
    assertTrue(mockJedis.getClass().getSimpleName().contains("Jedis"));
  }

}
