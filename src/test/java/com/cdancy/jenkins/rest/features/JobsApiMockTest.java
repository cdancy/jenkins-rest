/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.cdancy.jenkins.rest.features;

import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertTrue;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.core.MediaType;

import org.testng.annotations.Test;

import com.cdancy.jenkins.rest.JenkinsApi;
import com.cdancy.jenkins.rest.domain.job.BuildInfo;
import com.cdancy.jenkins.rest.domain.job.JobInfo;
import com.cdancy.jenkins.rest.domain.job.ProgressiveText;
import com.cdancy.jenkins.rest.internal.BaseJenkinsMockTest;
import com.google.common.collect.Lists;
import com.squareup.okhttp.mockwebserver.MockResponse;
import com.squareup.okhttp.mockwebserver.MockWebServer;

/**
 * Mock tests for the {@link com.cdancy.jenkins.rest.features.JobsApi} class.
 */
@Test(groups = "unit", testName = "JobsApiMockTest")
public class JobsApiMockTest extends BaseJenkinsMockTest {

   public void testGetJobInfo() throws Exception {
      MockWebServer server = mockEtcdJavaWebServer();

      String body = payloadFromResource("/job-info.json");
      server.enqueue(new MockResponse().setBody(body).setResponseCode(200));
      JenkinsApi etcdJavaApi = api(server.getUrl("/"));
      JobsApi api = etcdJavaApi.jobsApi();
      try {
         JobInfo output = api.jobInfo("fish");
         assertNotNull(output);
         assertNotNull(output.name().equals("fish"));
         assertTrue(output.builds().size() == 7);
         assertSent(server, "GET", "/job/fish/api/json");
      } finally {
         etcdJavaApi.close();
         server.shutdown();
      }
   }

   public void testGetJobInfoNotFound() throws Exception {
      MockWebServer server = mockEtcdJavaWebServer();

      server.enqueue(new MockResponse().setResponseCode(404));
      JenkinsApi etcdJavaApi = api(server.getUrl("/"));
      JobsApi api = etcdJavaApi.jobsApi();
      try {
         JobInfo output = api.jobInfo("fish");
         assertNull(output);
         assertSent(server, "GET", "/job/fish/api/json");
      } finally {
         etcdJavaApi.close();
         server.shutdown();
      }
   }

   public void testGetBuildInfo() throws Exception {
      MockWebServer server = mockEtcdJavaWebServer();

      String body = payloadFromResource("/build-info.json");
      server.enqueue(new MockResponse().setBody(body).setResponseCode(200));
      JenkinsApi etcdJavaApi = api(server.getUrl("/"));
      JobsApi api = etcdJavaApi.jobsApi();
      try {
         BuildInfo output = api.buildInfo("fish", 10);
         assertNotNull(output);
         assertNotNull(output.fullDisplayName().equals("fish #10"));
         assertTrue(output.artifacts().size() == 1);
         assertSent(server, "GET", "/job/fish/10/api/json");
      } finally {
         etcdJavaApi.close();
         server.shutdown();
      }
   }

   public void testGetBuildInfoNotFound() throws Exception {
      MockWebServer server = mockEtcdJavaWebServer();

      server.enqueue(new MockResponse().setResponseCode(404));
      JenkinsApi etcdJavaApi = api(server.getUrl("/"));
      JobsApi api = etcdJavaApi.jobsApi();
      try {
         BuildInfo output = api.buildInfo("fish", 10);
         assertNull(output);
         assertSent(server, "GET", "/job/fish/10/api/json");
      } finally {
         etcdJavaApi.close();
         server.shutdown();
      }
   }

   public void testCreateJob() throws Exception {
      MockWebServer server = mockEtcdJavaWebServer();

      String configXML = payloadFromResource("/freestyle-project.xml");
      server.enqueue(new MockResponse().setResponseCode(200));
      JenkinsApi etcdJavaApi = api(server.getUrl("/"));
      JobsApi api = etcdJavaApi.jobsApi();
      try {
         boolean success = api.create("DevTest", configXML);
         assertTrue(success);
         assertSentWithXMLFormDataAccept(server, "POST", "/createItem?name=DevTest", configXML, MediaType.WILDCARD);
      } finally {
         etcdJavaApi.close();
         server.shutdown();
      }
   }

   public void testCreateJobAlreadyExists() throws Exception {
      MockWebServer server = mockEtcdJavaWebServer();

      String configXML = payloadFromResource("/freestyle-project.xml");
      server.enqueue(new MockResponse().setHeader("X-Error", "A job already exists with the name ?DevTest?")
            .setResponseCode(400));
      JenkinsApi etcdJavaApi = api(server.getUrl("/"));
      JobsApi api = etcdJavaApi.jobsApi();
      try {
         boolean success = api.create("DevTest", configXML);
         assertFalse(success);
         assertSentWithXMLFormDataAccept(server, "POST", "/createItem?name=DevTest", configXML, MediaType.WILDCARD);
      } finally {
         etcdJavaApi.close();
         server.shutdown();
      }
   }

   public void testGetDescription() throws Exception {
      MockWebServer server = mockEtcdJavaWebServer();

      server.enqueue(new MockResponse().setBody("whatever").setResponseCode(200));
      JenkinsApi etcdJavaApi = api(server.getUrl("/"));
      JobsApi api = etcdJavaApi.jobsApi();
      try {
         String output = api.description("DevTest");
         assertNotNull(output);
         assertTrue(output.equals("whatever"));
         assertSentAcceptText(server, "GET", "/job/DevTest/description");
      } finally {
         etcdJavaApi.close();
         server.shutdown();
      }
   }

   public void testGetDescriptionNonExistentJob() throws Exception {
      MockWebServer server = mockEtcdJavaWebServer();

      server.enqueue(new MockResponse().setResponseCode(404));
      JenkinsApi etcdJavaApi = api(server.getUrl("/"));
      JobsApi api = etcdJavaApi.jobsApi();
      try {
         String output = api.description("DevTest");
         assertNull(output);
         assertSentAcceptText(server, "GET", "/job/DevTest/description");
      } finally {
         etcdJavaApi.close();
         server.shutdown();
      }
   }

   public void testUpdateDescription() throws Exception {
      MockWebServer server = mockEtcdJavaWebServer();

      server.enqueue(new MockResponse().setResponseCode(200));
      JenkinsApi etcdJavaApi = api(server.getUrl("/"));
      JobsApi api = etcdJavaApi.jobsApi();
      try {
         boolean success = api.description("DevTest", "whatever");
         assertTrue(success);
         assertSentWithFormData(server, "POST", "/job/DevTest/description", "description=whatever",
               MediaType.TEXT_HTML);
      } finally {
         etcdJavaApi.close();
         server.shutdown();
      }
   }

   public void testUpdateDescriptionNonExistentJob() throws Exception {
      MockWebServer server = mockEtcdJavaWebServer();

      server.enqueue(new MockResponse().setResponseCode(404));
      JenkinsApi etcdJavaApi = api(server.getUrl("/"));
      JobsApi api = etcdJavaApi.jobsApi();
      try {
         boolean success = api.description("DevTest", "whatever");
         assertFalse(success);
         assertSentWithFormData(server, "POST", "/job/DevTest/description", "description=whatever",
               MediaType.TEXT_HTML);
      } finally {
         etcdJavaApi.close();
         server.shutdown();
      }
   }

   public void testGetConfig() throws Exception {
      MockWebServer server = mockEtcdJavaWebServer();

      String configXML = payloadFromResource("/freestyle-project.xml");
      server.enqueue(new MockResponse().setBody(configXML).setResponseCode(200));
      JenkinsApi etcdJavaApi = api(server.getUrl("/"));
      JobsApi api = etcdJavaApi.jobsApi();
      try {
         String output = api.config("DevTest");
         assertNotNull(output);
         assertTrue(configXML.equals(output));
         assertSentAcceptText(server, "GET", "/job/DevTest/config.xml");
      } finally {
         etcdJavaApi.close();
         server.shutdown();
      }
   }

   public void testGetConfigNonExistentJob() throws Exception {
      MockWebServer server = mockEtcdJavaWebServer();

      server.enqueue(new MockResponse().setResponseCode(404));
      JenkinsApi etcdJavaApi = api(server.getUrl("/"));
      JobsApi api = etcdJavaApi.jobsApi();
      try {
         String output = api.config("DevTest");
         assertNull(output);
         assertSentAcceptText(server, "GET", "/job/DevTest/config.xml");
      } finally {
         etcdJavaApi.close();
         server.shutdown();
      }
   }

   public void testUpdateConfig() throws Exception {
      MockWebServer server = mockEtcdJavaWebServer();

      String configXML = payloadFromResource("/freestyle-project.xml");
      server.enqueue(new MockResponse().setResponseCode(200));
      JenkinsApi etcdJavaApi = api(server.getUrl("/"));
      JobsApi api = etcdJavaApi.jobsApi();
      try {
         boolean success = api.config("DevTest", configXML);
         assertTrue(success);
         assertSentAccept(server, "POST", "/job/DevTest/config.xml", MediaType.TEXT_HTML);
      } finally {
         etcdJavaApi.close();
         server.shutdown();
      }
   }

   public void testUpdateConfigNonExistentJob() throws Exception {
      MockWebServer server = mockEtcdJavaWebServer();

      String configXML = payloadFromResource("/freestyle-project.xml");
      server.enqueue(new MockResponse().setResponseCode(404));
      JenkinsApi etcdJavaApi = api(server.getUrl("/"));
      JobsApi api = etcdJavaApi.jobsApi();
      try {
         boolean success = api.config("DevTest", configXML);
         assertFalse(success);
         assertSentAccept(server, "POST", "/job/DevTest/config.xml", MediaType.TEXT_HTML);
      } finally {
         etcdJavaApi.close();
         server.shutdown();
      }
   }

   public void testDeleteJob() throws Exception {
      MockWebServer server = mockEtcdJavaWebServer();

      server.enqueue(new MockResponse().setResponseCode(200));
      JenkinsApi etcdJavaApi = api(server.getUrl("/"));
      JobsApi api = etcdJavaApi.jobsApi();
      try {
         boolean success = api.delete("DevTest");
         assertTrue(success);
         assertSentAccept(server, "POST", "/job/DevTest/doDelete", MediaType.TEXT_HTML);
      } finally {
         etcdJavaApi.close();
         server.shutdown();
      }
   }

   public void testDeleteJobNonExistent() throws Exception {
      MockWebServer server = mockEtcdJavaWebServer();

      server.enqueue(new MockResponse().setResponseCode(400));
      JenkinsApi etcdJavaApi = api(server.getUrl("/"));
      JobsApi api = etcdJavaApi.jobsApi();
      try {
         boolean success = api.delete("DevTest");
         assertFalse(success);
         assertSentAccept(server, "POST", "/job/DevTest/doDelete", MediaType.TEXT_HTML);
      } finally {
         etcdJavaApi.close();
         server.shutdown();
      }
   }

   public void testEnableJob() throws Exception {
      MockWebServer server = mockEtcdJavaWebServer();

      server.enqueue(new MockResponse().setResponseCode(200));
      JenkinsApi etcdJavaApi = api(server.getUrl("/"));
      JobsApi api = etcdJavaApi.jobsApi();
      try {
         boolean success = api.enable("DevTest");
         assertTrue(success);
         assertSentAccept(server, "POST", "/job/DevTest/enable", MediaType.TEXT_HTML);
      } finally {
         etcdJavaApi.close();
         server.shutdown();
      }
   }

   public void testEnableJobAlreadyEnabled() throws Exception {
      MockWebServer server = mockEtcdJavaWebServer();

      server.enqueue(new MockResponse().setResponseCode(200));
      JenkinsApi etcdJavaApi = api(server.getUrl("/"));
      JobsApi api = etcdJavaApi.jobsApi();
      try {
         boolean success = api.enable("DevTest");
         assertTrue(success);
         assertSentAccept(server, "POST", "/job/DevTest/enable", MediaType.TEXT_HTML);
      } finally {
         etcdJavaApi.close();
         server.shutdown();
      }
   }

   public void testDisableJob() throws Exception {
      MockWebServer server = mockEtcdJavaWebServer();

      server.enqueue(new MockResponse().setResponseCode(200));
      JenkinsApi etcdJavaApi = api(server.getUrl("/"));
      JobsApi api = etcdJavaApi.jobsApi();
      try {
         boolean success = api.disable("DevTest");
         assertTrue(success);
         assertSentAccept(server, "POST", "/job/DevTest/disable", MediaType.TEXT_HTML);
      } finally {
         etcdJavaApi.close();
         server.shutdown();
      }
   }

   public void testDisableJobAlreadyEnabled() throws Exception {
      MockWebServer server = mockEtcdJavaWebServer();

      server.enqueue(new MockResponse().setResponseCode(200));
      JenkinsApi etcdJavaApi = api(server.getUrl("/"));
      JobsApi api = etcdJavaApi.jobsApi();
      try {
         boolean success = api.disable("DevTest");
         assertTrue(success);
         assertSentAccept(server, "POST", "/job/DevTest/disable", MediaType.TEXT_HTML);
      } finally {
         etcdJavaApi.close();
         server.shutdown();
      }
   }

   public void testBuildJob() throws Exception {
      MockWebServer server = mockEtcdJavaWebServer();

      server.enqueue(
            new MockResponse().setHeader("Location", "http://127.0.1.1:8080/queue/item/1/").setResponseCode(201));
      JenkinsApi etcdJavaApi = api(server.getUrl("/"));
      JobsApi api = etcdJavaApi.jobsApi();
      try {
         Integer output = api.build("DevTest");
         assertNotNull(output);
         assertTrue(output == 1);
         assertSentAccept(server, "POST", "/job/DevTest/build", "application/unknown");
      } finally {
         etcdJavaApi.close();
         server.shutdown();
      }
   }

   public void testBuildJobWithNoLocationReturned() throws Exception {
      MockWebServer server = mockEtcdJavaWebServer();

      server.enqueue(
              new MockResponse().setResponseCode(201));
      JenkinsApi etcdJavaApi = api(server.getUrl("/"));
      JobsApi api = etcdJavaApi.jobsApi();
      try {
         Integer output = api.build("DevTest");
         assertNotNull(output);
         assertTrue(output == 0);
         assertSentAccept(server, "POST", "/job/DevTest/build", "application/unknown");
      } finally {
         etcdJavaApi.close();
         server.shutdown();
      }
   }

   public void testBuildJobNonExistentJob() throws Exception {
      MockWebServer server = mockEtcdJavaWebServer();

      server.enqueue(new MockResponse().setResponseCode(404));
      JenkinsApi etcdJavaApi = api(server.getUrl("/"));
      JobsApi api = etcdJavaApi.jobsApi();
      try {
         Integer output = api.build("DevTest");
         assertNull(output);
         assertSentAccept(server, "POST", "/job/DevTest/build", "application/unknown");
      } finally {
         etcdJavaApi.close();
         server.shutdown();
      }
   }

   public void testBuildJobWithParams() throws Exception {
      MockWebServer server = mockEtcdJavaWebServer();

      server.enqueue(
            new MockResponse().setHeader("Location", "http://127.0.1.1:8080/queue/item/1/").setResponseCode(201));
      JenkinsApi etcdJavaApi = api(server.getUrl("/"));
      JobsApi api = etcdJavaApi.jobsApi();
      try {
         Map<String, List<String>> params = new HashMap<>();
         params.put("SomeKey", Lists.newArrayList("SomeVeryNewValue"));
         Integer output = api.buildWithParameters("DevTest", params);
         assertNotNull(output);
         assertTrue(output == 1);
         assertSentAccept(server, "POST", "/job/DevTest/buildWithParameters", "application/unknown");
      } finally {
         etcdJavaApi.close();
         server.shutdown();
      }
   }

   public void testBuildJobWithParamsNonExistentJob() throws Exception {
      MockWebServer server = mockEtcdJavaWebServer();

      server.enqueue(new MockResponse().setResponseCode(404));
      JenkinsApi etcdJavaApi = api(server.getUrl("/"));
      JobsApi api = etcdJavaApi.jobsApi();
      try {
         Map<String, List<String>> params = new HashMap<>();
         params.put("SomeKey", Lists.newArrayList("SomeVeryNewValue"));
         Integer output = api.buildWithParameters("DevTest", params);
         assertNull(output);
         assertSentAccept(server, "POST", "/job/DevTest/buildWithParameters", "application/unknown");
      } finally {
         etcdJavaApi.close();
         server.shutdown();
      }
   }

   public void testGetLastBuildNumber() throws Exception {
      MockWebServer server = mockEtcdJavaWebServer();

      String body = payloadFromResource("/build-number.txt");
      server.enqueue(new MockResponse().setBody(body).setResponseCode(200));
      JenkinsApi etcdJavaApi = api(server.getUrl("/"));
      JobsApi api = etcdJavaApi.jobsApi();
      try {
         Integer output = api.lastBuildNumber("DevTest");
         assertNotNull(output);
         assertTrue(output.intValue() == 123);
         assertSentAcceptText(server, "GET", "/job/DevTest/lastBuild/buildNumber");
      } finally {
         etcdJavaApi.close();
         server.shutdown();
      }
   }

   public void testGetLastBuildNumberJobNotExist() throws Exception {
      MockWebServer server = mockEtcdJavaWebServer();

      server.enqueue(new MockResponse().setResponseCode(404));
      JenkinsApi etcdJavaApi = api(server.getUrl("/"));
      JobsApi api = etcdJavaApi.jobsApi();
      try {
         Integer output = api.lastBuildNumber("DevTest");
         assertNull(output);
         assertSentAcceptText(server, "GET", "/job/DevTest/lastBuild/buildNumber");
      } finally {
         etcdJavaApi.close();
         server.shutdown();
      }
   }

   public void testGetLastBuildTimeStamp() throws Exception {
      MockWebServer server = mockEtcdJavaWebServer();

      String body = payloadFromResource("/build-timestamp.txt");
      server.enqueue(new MockResponse().setBody(body).setResponseCode(200));
      JenkinsApi etcdJavaApi = api(server.getUrl("/"));
      JobsApi api = etcdJavaApi.jobsApi();
      try {
         String output = api.lastBuildTimestamp("DevTest");
         assertNotNull(output);
         assertTrue(output.equals(body));
         assertSentAcceptText(server, "GET", "/job/DevTest/lastBuild/buildTimestamp");
      } finally {
         etcdJavaApi.close();
         server.shutdown();
      }
   }

   public void testGetLastBuildTimeStampJobNotExist() throws Exception {
      MockWebServer server = mockEtcdJavaWebServer();

      server.enqueue(new MockResponse().setResponseCode(404));
      JenkinsApi etcdJavaApi = api(server.getUrl("/"));
      JobsApi api = etcdJavaApi.jobsApi();
      try {
         String output = api.lastBuildTimestamp("DevTest");
         assertNull(output);
         assertSentAcceptText(server, "GET", "/job/DevTest/lastBuild/buildTimestamp");
      } finally {
         etcdJavaApi.close();
         server.shutdown();
      }
   }

   public void testGetProgressiveText() throws Exception {
      MockWebServer server = mockEtcdJavaWebServer();

      String body = payloadFromResource("/progressive-text.txt");
      server.enqueue(new MockResponse().setHeader("X-Text-Size", "123").setBody(body).setResponseCode(200));
      JenkinsApi etcdJavaApi = api(server.getUrl("/"));
      JobsApi api = etcdJavaApi.jobsApi();
      try {
         ProgressiveText output = api.progressiveText("DevTest", 0);
         assertNotNull(output);
         assertTrue(output.size() == 123);
         assertFalse(output.hasMoreData());
         assertSentAcceptText(server, "GET", "/job/DevTest/lastBuild/logText/progressiveText?start=0");
      } finally {
         etcdJavaApi.close();
         server.shutdown();
      }
   }

   public void testGetProgressiveTextJobNotExist() throws Exception {
      MockWebServer server = mockEtcdJavaWebServer();

      server.enqueue(new MockResponse().setResponseCode(404));
      JenkinsApi etcdJavaApi = api(server.getUrl("/"));
      JobsApi api = etcdJavaApi.jobsApi();
      try {
         ProgressiveText output = api.progressiveText("DevTest", 0);
         assertNull(output);
         assertSentAcceptText(server, "GET", "/job/DevTest/lastBuild/logText/progressiveText?start=0");
      } finally {
         etcdJavaApi.close();
         server.shutdown();
      }
   }
}
