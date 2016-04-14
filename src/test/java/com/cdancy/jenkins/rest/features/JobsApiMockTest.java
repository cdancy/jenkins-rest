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

import javax.ws.rs.core.MediaType;

import org.testng.annotations.Test;

import com.cdancy.jenkins.rest.JenkinsApi;
import com.cdancy.jenkins.rest.internal.BaseJenkinsMockTest;
import com.squareup.okhttp.mockwebserver.MockResponse;
import com.squareup.okhttp.mockwebserver.MockWebServer;

/**
 * Mock tests for the {@link com.cdancy.jenkins.rest.features.JobsApi} class.
 */
@Test(groups = "unit", testName = "JobsApiMockTest")
public class JobsApiMockTest extends BaseJenkinsMockTest {

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
}
