
[![Build Status](https://travis-ci.org/cdancy/jenkins-rest.svg?branch=master)](https://travis-ci.org/cdancy/jenkins-rest)
[![codecov](https://codecov.io/gh/cdancy/jenkins-rest/branch/master/graph/badge.svg)](https://codecov.io/gh/cdancy/jenkins-rest)
[![Download](https://api.bintray.com/packages/cdancy/java-libraries/jenkins-rest/images/download.svg) ](https://bintray.com/cdancy/java-libraries/jenkins-rest/_latestVersion)
[![Stack Overflow](https://img.shields.io/badge/stack%20overflow-jenkins&#8211;rest-4183C4.svg)](https://stackoverflow.com/questions/tagged/jenkins+rest)

# jenkins-rest

java-based client to interact with Jenkins REST API. 

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
</dependency>
```

## Documentation

javadocs can be found via [github pages here](http://cdancy.github.io/jenkins-rest/docs/javadoc/)

## Property based setup

Client's do NOT need supply the endPoint or credentials as part of instantiating the JenkinsClient object. 
Instead one can supply them through system properties, environment variables, or a combination 
of the 2. System properties will be searched first and if not found we will attempt to 
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

## Logging

Sometimes it can be useful to activate logging and trace HTTP transactions.
For these cases, you need to pass a logging framework to the builder modules list.
For example using SLF4J, this can be done like so:

```
import org.jclouds.logging.slf4j.config.SLF4JLoggingModule;
...
JenkinsClient client = JenkinsClient.builder()
    .modules(Lists.newArrayList(new SLF4JLoggingModule()))
    .build();
```

In the case of the above example, the logging will require:
- a compile time dependency such as `org.slf4j:slf4j-log4j12:<version>`
- a `log4j.properties` file configuring the logger
- passing the properties file to the application, for example with `JAVA_OPTS=-Dlog4j.configuration=file:log4j.properties`

See a complete example under [examples/logging-slf4j](examples/logging-slf4j).

## Components

- jclouds \- used as the backend for communicating with Jenkins REST API
- AutoValue \- used to create immutable value types both to and from the jenkins program
    
## Testing

Running mock tests can be done like so:

	./gradlew clean build mockTest
	
Running integration tests can be done like so (requires existing jenkins instance):

	./gradlew clean build integTest 
	
# Additional Resources

* [Jenkins REST API](http://wiki.jenkins-ci.org/display/JENKINS/Remote+access+API)
* [Apache jclouds](https://jclouds.apache.org/start/)

