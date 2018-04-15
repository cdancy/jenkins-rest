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

import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

import org.testng.annotations.Test;

import com.cdancy.jenkins.rest.JenkinsApi;
import com.cdancy.jenkins.rest.BaseJenkinsMockTest;

import com.squareup.okhttp.mockwebserver.MockResponse;
import com.squareup.okhttp.mockwebserver.MockWebServer;

import javax.ws.rs.core.MediaType;

/**
 * Mock tests for the {@link com.cdancy.jenkins.rest.features.CrumbIssuerApi} class.
 */
@Test(groups = "unit", testName = "CrumbIssuerApiMockTest")
public class CrumbIssuerApiMockTest extends BaseJenkinsMockTest {

    public void testGetSystemInfo() throws Exception {
        MockWebServer server = mockWebServer();

        final String body = "";
        server.enqueue(new MockResponse().setBody("Jenkins-Crumb:04a1109fc2db171362c966ebe9fc87f0").setResponseCode(200));
        JenkinsApi jenkinsApi = api(server.getUrl("/"));
        CrumbIssuerApi api = jenkinsApi.crumbIssuerApi();
        try {
            final String instance = api.crumb();
            assertNotNull(instance);
            assertTrue(instance.contains(":"));
            assertSentAccept(server, "GET", "/crumbIssuer/api/xml?xpath=concat%28//crumbRequestField,%22%3A%22,//crumb%29", MediaType.TEXT_PLAIN);
        } finally {
            jenkinsApi.close();
            server.shutdown();
        }
    }
}
