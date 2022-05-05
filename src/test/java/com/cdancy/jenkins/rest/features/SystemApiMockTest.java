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
import static org.testng.Assert.assertFalse;

import com.cdancy.jenkins.rest.domain.common.RequestStatus;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.testng.annotations.Test;

import com.cdancy.jenkins.rest.JenkinsApi;
import com.cdancy.jenkins.rest.JenkinsApiMetadata;
import com.cdancy.jenkins.rest.domain.system.SystemInfo;
import com.cdancy.jenkins.rest.BaseJenkinsMockTest;

import javax.ws.rs.core.MediaType;

/**
 * Mock tests for the {@link com.cdancy.jenkins.rest.features.SystemApi} class.
 */
@Test(groups = "unit", testName = "SystemApiMockTest")
public class SystemApiMockTest extends BaseJenkinsMockTest {

    public void testGetSystemInfo() throws Exception {
        MockWebServer server = mockWebServer();

        server.enqueue(
            new MockResponse().setHeader("X-Hudson", "1.395").setHeader("X-Jenkins", JenkinsApiMetadata.BUILD_VERSION)
                .setHeader("X-Jenkins-Session", "cc323b8d").setHeader("X-Hudson-CLI-Port", "50000")
                .setHeader("X-Jenkins-CLI-Port", "50000").setHeader("X-Jenkins-CLI2-Port", "50000")
                .setHeader("X-Instance-Identity", "fdsa").setHeader("X-SSH-Endpoint", "127.0.1.1:46126")
                .setHeader("Server", "Jetty(winstone-2.9)").setResponseCode(200));
        JenkinsApi jenkinsApi = api(server.url("/").url());
        SystemApi api = jenkinsApi.systemApi();
        try {
            final SystemInfo version = api.systemInfo();
            assertNotNull(version);
            assertTrue(version.jenkinsVersion().equalsIgnoreCase(JenkinsApiMetadata.BUILD_VERSION));
            assertSent(server, "HEAD", "/");
        } finally {
            jenkinsApi.close();
            server.shutdown();
        }
    }

    public void testGetSystemInfoOnError() throws Exception {
        MockWebServer server = mockWebServer();

        server.enqueue(
            new MockResponse().setBody("Not Authorized").setResponseCode(401));
        JenkinsApi jenkinsApi = api(server.url("/").url());
        SystemApi api = jenkinsApi.systemApi();
        try {
            final SystemInfo version = api.systemInfo();
            assertNotNull(version);
            assertFalse(version.errors().isEmpty());
            assertSent(server, "HEAD", "/");
        } finally {
            jenkinsApi.close();
            server.shutdown();
        }
    }

    public void testQuietDown() throws Exception {
        MockWebServer server = mockWebServer();

        server.enqueue(new MockResponse().setResponseCode(200));
        JenkinsApi jenkinsApi = api(server.url("/").url());
        SystemApi api = jenkinsApi.systemApi();
        try {
            RequestStatus success = api.quietDown();
            assertNotNull(success);
            assertTrue(success.value());
            assertSentAccept(server, "POST", "/quietDown", MediaType.TEXT_HTML);
        } finally {
            jenkinsApi.close();
            server.shutdown();
        }
    }

    public void testQuietDownOnAuthException() throws Exception {
        MockWebServer server = mockWebServer();

        server.enqueue(new MockResponse().setResponseCode(401));
        JenkinsApi jenkinsApi = api(server.url("/").url());
        SystemApi api = jenkinsApi.systemApi();
        try {
            RequestStatus status = api.quietDown();
            assertFalse(status.value());
            assertFalse(status.errors().isEmpty());
            assertTrue(status.errors().get(0).exceptionName().endsWith("AuthorizationException"));
            assertSentAccept(server, "POST", "/quietDown", MediaType.TEXT_HTML);
        } finally {
            jenkinsApi.close();
            server.shutdown();
        }
    }

    public void testCancelQuietDown() throws Exception {
        MockWebServer server = mockWebServer();

        server.enqueue(new MockResponse().setResponseCode(200));
        JenkinsApi jenkinsApi = api(server.url("/").url());
        SystemApi api = jenkinsApi.systemApi();
        try {
            RequestStatus success = api.cancelQuietDown();
            assertNotNull(success);
            assertTrue(success.value());
            assertSentAccept(server, "POST", "/cancelQuietDown", MediaType.TEXT_HTML);
        } finally {
            jenkinsApi.close();
            server.shutdown();
        }
    }

    public void testCancelQuietDownOnAuthException() throws Exception {
        MockWebServer server = mockWebServer();

        server.enqueue(new MockResponse().setResponseCode(401));
        JenkinsApi jenkinsApi = api(server.url("/").url());
        SystemApi api = jenkinsApi.systemApi();
        try {
            RequestStatus status = api.cancelQuietDown();
            assertFalse(status.value());
            assertFalse(status.errors().isEmpty());
            assertTrue(status.errors().get(0).exceptionName().endsWith("AuthorizationException"));
            assertSentAccept(server, "POST", "/cancelQuietDown", MediaType.TEXT_HTML);
        } finally {
            jenkinsApi.close();
            server.shutdown();
        }
    }
}
