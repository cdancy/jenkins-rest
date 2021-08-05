
[![Build Status](https://travis-ci.org/cdancy/jenkins-rest.svg?branch=master)](https://travis-ci.org/cdancy/jenkins-rest)
[![codecov](https://codecov.io/gh/cdancy/jenkins-rest/branch/master/graph/badge.svg)](https://codecov.io/gh/cdancy/jenkins-rest)
[![Download](https://api.bintray.com/packages/cdancy/java-libraries/jenkins-rest/images/download.svg) ](https://bintray.com/cdancy/java-libraries/jenkins-rest/_latestVersion)
[![Stack Overflow](https://img.shields.io/badge/stack%20overflow-jenkins&#8211;rest-4183C4.svg)](https://stackoverflow.com/questions/tagged/jenkins+rest)

# jenkins-rest

Java client is built on the top of jclouds for working with Jenkins REST API.

## Setup

Client's can be built like so:
```
JenkinsClient client = JenkinsClient.builder()
.endPoint("http://127.0.0.1:8080") // Optional. Defaults to http://127.0.0.1:8080
.credentials("admin:password") // Optional.
.build();

SystemInfo systemInfo = client.api().systemApi().systemInfo();
assertTrue(systemInfo.jenkinsVersion().equals("1.642.4"));
```
      
## Latest release

Can be found in jcenter like so:
```
<dependency>
  <groupId>com.cdancy</groupId>
  <artifactId>jenkins-rest</artifactId>
  <version>X.Y.Z</version>
  <classifier>sources|tests|javadoc|all</classifier> (Optional)
</dependency>
```

## Documentation

* javadocs can be found via [javadoc.io](https://javadoc.io/doc/com.cdancy/jenkins-rest)
* the [jenkins-rest wiki](https://github.com/cdancy/jenkins-rest/wiki)

## Property based setup

Client instances do NOT need to supply the endPoint or credentials as a part of instantiating the JenkinsClient object. 
Instead one can supply them through system properties, environment variables, or a combination 
of the two. System properties will be searched first and if not found, will attempt to 
query the environment.

Setting the `endpoint` can be done with any of the following (searched in order):

- `jenkins.rest.endpoint`
- `jenkinsRestEndpoint`
- `JENKINS_REST_ENDPOINT`

Setting the `credentials` can be done with any of the following (searched in order):

- `jenkins.rest.credentials`
- `jenkinsRestCredentials`
- `JENKINS_REST_CREDENTIALS`

## Credentials

jenkins-rest credentials can take 1 of 2 forms:

- Colon delimited username and password: __admin:password__ 
- Base64 encoded username and password: __YWRtaW46cGFzc3dvcmQ=__ 

## Examples

The [mock](https://github.com/cdancy/jenkins-rest/tree/master/src/test/java/com/cdancy/jenkins/rest/features) and [live](https://github.com/cdancy/jenkins-rest/tree/master/src/test/java/com/cdancy/jenkins/rest/features) tests provide many examples
that you can use in your own code.

## Components

- jclouds \- used as the backend for communicating with Jenkins REST API
- AutoValue \- used to create immutable value types both to and from the jenkins program
    
## Testing

Running mock tests can be done like so:

	./gradlew clean build mockTest
	
Running integration tests can be done like so (requires existing jenkins instance):

	./gradlew clean build integTest 

### Integration tests settings

#### Jenkins instance requirements

- a running instance accessible on http://127.0.0.1:8080 (can be changed in the gradle.properties file)
- no pre-existing jobs
- Jenkins security
  - an `admin` user (credentials used by the tests can be changed in the gradle.properties file) with `ADMIN` role (required as the tests install plugins)
  - [CSRF protection enabled](https://wiki.jenkins.io/display/JENKINS/CSRF+Protection). Not mandatory but [recommended by the Jenkins documentation](https://jenkins.io/doc/book/system-administration/security/#protect-users-of-jenkins-from-other-threats). The lib supports Jenkins instances with our without this protection (see #14)
- Plugins
  - [CloudBees Credentials](https://plugins.jenkins.io/cloudbees-credentials): otherwise an http 500 error occurs when accessing
to http://127.0.0.1:8080/job/test-folder/job/test-folder-1/ `java.lang.NoClassDefFoundError: com/cloudbees/hudson/plugins/folder/properties/FolderCredentialsProvider`
  - [OWASP Markup Formatter](https://plugins.jenkins.io/antisamy-markup-formatter) configured to use `Safe HTML`
  - [Configuration As Code](https://plugins.jenkins.io/configuration-as-code) plugin installed

This project provides instructions to setup a [pre-configured Docker container](src/main/docker/README.md)

#### Integration tests configuration

- jenkins url and authentication method used by the tests are defined in the `gradle.properties` file
- by default, tests use the `credentials` authentication but this can be changed to use the `token` authentication


#### Running integration tests from within your IDE

- the `integTest` gradle tasks set various System Properties
- if you don't want to use gradle as tests runner in your IDE, configure the tests with the same kind of System Properties



# Additional Resources

* [Jenkins REST API](http://wiki.jenkins-ci.org/display/JENKINS/Remote+access+API)
* [Apache jclouds](https://jclouds.apache.org/start/)

