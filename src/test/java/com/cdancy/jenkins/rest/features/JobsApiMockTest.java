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
import static org.testng.Assert.assertTrue;

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
         assertSentWithXMLFormData(server, "POST", "/createItem?name=DevTest", configXML);
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
         assertSentWithXMLFormData(server, "POST", "/createItem?name=DevTest", configXML);
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
         assertSent(server, "POST", "/job/DevTest/doDelete");
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
         assertSent(server, "POST", "/job/DevTest/doDelete");
      } finally {
         etcdJavaApi.close();
         server.shutdown();
      }
   }
}
