package com.streetcontxt.redis_mock.adapter.jedis;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import com.streetcontxt.redis_mock.NotImplementedException;
import com.streetcontxt.redis_mock.RedisMock;

import redis.clients.jedis.Jedis;

/**
 * Creates a mock Jedis for use in unit tests, which checks if invoked methods have actually been mocked.
 * <p>
 * Because Jedis (and its superclass BinaryJedis) have a very large number of methods, and there's no interface
 * for them, missing methods in Jedis mocks are a frequent problem. These normally throw difficult-to-interpret
 * errors (e.g. "connection refused") when a superclass method is invoked. To solve this, the mock Jedis object
 * created by this class detects if the Jedis method being invoked is actually implemented in the mock, and
 * throws an informative exception if you try to use a method that hasn't been mocked yet.
 */
public class CheckedJedisMock {

    private RedisMock redisMock = new RedisMock();
    private JedisAdapter jedisAdapter = new JedisAdapter(redisMock);

    // Use Mockito to create a Jedis object that captures method calls, and either passes them to the mock if they're
    // implemented, or throws an exception if they're not implemented.
    private Jedis mockJedis = Mockito.mock(Jedis.class, new JedisMockAnswer());

    public CheckedJedisMock() {
    }

    public Jedis getJedisMock() {
        return mockJedis;
    }

    class JedisMockAnswer implements Answer<Object> {
        @Override
        public Object answer(final InvocationOnMock invocation) throws Throwable {

            try {
                return invokeOnJedisMock(invocation, jedisAdapter);
            } catch (NotImplementedException e) {
            } catch (UnsupportedOperationException e) {
            }

            throw getMockMissingException(invocation);
        }
    }

    private static UnsupportedOperationException getMockMissingException(final InvocationOnMock invocation) {
        String message = "Method not yet implemented in Jedis mock. "
                + "Method name: " + invocation.getMethod().getName()
                + ", required param types: " + invocationParamString(invocation)
                + ". The missing method can be added to " + JedisAdapter.class.getCanonicalName();
        return new UnsupportedOperationException(message);
    }

    /**
     * Tries to invoke a method on a Jedis mock.
     *
     * @param invocation
     * @param jedisMock
     * @return Result of invoking the mock's method
     * @throws Throwable Any exception thrown by the original method
     */
    private static Object invokeOnJedisMock(final InvocationOnMock invocation, final Object jedisMock)
            throws Throwable {

        String invokedMethodName = invocation.getMethod().getName();

        List<Method> candidateMethods = Arrays.asList(jedisMock.getClass().getMethods())
                .stream()
                .filter(method -> method.getName().equals(invokedMethodName)
                        && (method.getDeclaringClass().equals(jedisMock.getClass())
                        || method.getDeclaringClass().equals(Object.class))
                ).collect(Collectors.toList());

        for (Method method : candidateMethods) {

            Object[] arguments = Arrays.copyOf(invocation.getArguments(), invocation.getArguments().length);

            if (method.getParameterCount() > 0 && arguments.length > 0) {
                Class lastParamType = method.getParameterTypes()[method.getParameterCount() - 1];

                // There are only two types of vararg parameters used by Jedis: int... and String...
                // (This would need to be updated if they added other vararg types in the future)
                if (lastParamType.equals(Integer[].class) || lastParamType.equals(String[].class)) {
                    // Convert all the additional parameters from the invocation into an array for the varags method
                    int numMethodParams = method.getParameterCount();
                    List<Object> invocationParams = new ArrayList(
                            Arrays.asList(arguments).subList(0, numMethodParams - 1));
                    invocationParams
                            .add(Arrays.copyOfRange(arguments, numMethodParams - 1, arguments.length, lastParamType));
                    arguments = invocationParams.toArray(new Object[invocationParams.size()]);
                }
            }

            try {
                return method.invoke(jedisMock, arguments);
            } catch (IllegalArgumentException e) {
                // Params don't match, try another method
            } catch (InvocationTargetException e) {
                // Invoked method threw an exception - pass the original exception along
                throw e.getCause();
            }
        }

        throw new NotImplementedException();
    }

    private static String invocationParamString(final InvocationOnMock invocation) {
        List<String> paramTypes = Arrays.asList(invocation.getArguments()).stream()
                .map(obj -> obj.getClass().getSimpleName())
                .collect(Collectors.toList());
        return "<" + String.join(", ", paramTypes) + ">";
    }

}
