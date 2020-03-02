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

## Using published artifact

To use this (or any artifact from Bintray) in another project, the SCX maven Bintray repo must be 
added as a repository in that project's build file. Then it can be added as a dependency as usual.

E.g. in an sbt build, add the bintray repo: `resolvers += Resolver.bintrayRepo("streetcontxt", "maven")`
...then add the library dependency: `"com.streetcontxt" % "redis-mock-java" % "1.0"`
