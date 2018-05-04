@Grab(group='com.cdancy', module='jenkins-rest', version='0.0.8')
@Grab(group='org.apache.jclouds.driver', module='jclouds-slf4j', version='2.1.0')
@Grab(group='org.slf4j', module='slf4j-log4j12', version='1.7.25')

import com.cdancy.jenkins.rest.JenkinsClient
import org.jclouds.logging.slf4j.config.SLF4JLoggingModule

JenkinsClient client = JenkinsClient.builder()
    .modules([new SLF4JLoggingModule()])
    .build()

println(client.api().systemApi().systemInfo())
