# redis-mock-java
Redis mock, adapted from Rarefied Redis's [redis-mock-java](https://github.com/wilkenstein/redis-mock-java) project.

## Background

This fork was created to add features and bugfixes to the original Rarefied Redis mock project, 
which is more comprehensive than other Redis mocks but hasn't been updated in some time.

## Building locally

This project uses a standard Maven build.

`mvn compile` - compile
`mvn test` - run tests
`mvn package` - publish local snapshot

## Publishing

CircleCI is set up to publish commits with a `release-<version number>` tag to the SCX Bintray maven repo.

## Adding dependency

To use this in another project, the SCX maven Bintray repo must be added as a repository 
in that project's build file.

E.g. in an sbt build, add the bintray repo: `resolvers += Resolver.bintrayRepo("streetcontxt", "maven")`
...then add the library dependency: `"com.streetcontxt" % "redis-mock-java" % "1.0"`

## Usage

In addition to the functionality of the original project ([redis-mock-java](https://github.com/wilkenstein/redis-mock-java)),
this fork adds a Mockito-based wrapper for the mocked Jedis object which will throw an informative error
if you try to invoke a method that hasn't been mocked, rather than calling the superclass method
and causing a more subtle error. (There are so many Jedis methods and no interface, so it's otherwise
difficult to ensure full implementation of a mock.) It can be used as follows:

```
import com.streetcontxt.redis_mock.adapter.jedis.CheckedJedisMock;
import redis.clients.jedis.Jedis;
[...]
Jedis mockJedis = new CheckedJedisMock().getJedisMock();
```
