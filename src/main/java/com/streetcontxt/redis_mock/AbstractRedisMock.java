package com.streetcontxt.redis_mock;

import java.util.List;

import com.streetcontxt.redis_mock.IRedisSortedSet.ZsetPair;

public abstract class AbstractRedisMock extends AbstractRedisClient {

    public abstract boolean modified(Integer hashCode, String command, List<Object> args);

}
