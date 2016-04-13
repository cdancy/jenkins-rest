
# jenkins-rest

java-based client to interact with Jenkins REST API. 

## Setup

Client's can be built like so:

      JenkinsClient client = new JenkinsClient.Builder()
      .endPoint("http://127.0.0.1:8080") // Optional. Defaults to http://127.0.0.1:8080
      .credentials("admin:password") // Optional.
      .build();

      Version version = client.api().systemApi().version();
      
## Latest release

Can be found in jcenter:

	<dependency>
	  <groupId>com.cdancy</groupId>
	  <artifactId>jenkins-rest</artifactId>
	  <version>0.0.1</version>
	</dependency>
	
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

## Components

- jclouds \- used as the backend for communicating with Jenkins REST API
- AutoValue \- used to create immutable value types both to and from the jenkins program
    
## Testing

Running mock tests can be done like so:

	./gradlew clean build mockTest
	
Running integration tests can be done like so (requires docker):

	./gradlew clean build integTest
	
Running integration tests without invoking docker can be done like so:

	./gradlew clean build integTest -PbootstrapDocker=false -PtestJenkinsEndpoint=http://127.0.0.1:8080 
	
# Additional Resources

* [Jenkins REST API](http://wiki.jenkins-ci.org/display/JENKINS/Remote+access+API)
* [Apache jclouds](https://jclouds.apache.org/start/)

