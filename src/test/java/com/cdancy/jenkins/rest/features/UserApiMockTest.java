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

import com.cdancy.jenkins.rest.BaseJenkinsMockTest;
import com.cdancy.jenkins.rest.JenkinsApi;
import com.cdancy.jenkins.rest.domain.user.ApiToken;
import com.cdancy.jenkins.rest.domain.user.User;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.testng.annotations.Test;

import static org.testng.Assert.*;

/**
 * Mock tests for the {@link com.cdancy.jenkins.rest.features.JobsApi} class.
 */
@Test(groups = "unit", testName = "UserApiMockTest")
public class UserApiMockTest extends BaseJenkinsMockTest {

    @Test
    public void testGetUser() throws Exception {
        MockWebServer server = mockWebServer();

        String body = payloadFromResource("/user.json");
        server.enqueue(new MockResponse().setBody(body).setResponseCode(200));
        //JenkinsApi jenkinsApi = api(server.url("/").url());
        try (JenkinsApi jenkinsApi = api(server.url("/").url())) {
            UserApi api = jenkinsApi.userApi();
            User output = api.get();
            assertNotNull(output);
            assertNotNull(output.absoluteUrl());
            assertEquals(output.absoluteUrl(), "http://localhost:8080/user/admin");
            assertNull(output.description());
            assertNotNull(output.fullName());
            assertEquals(output.fullName(), "Administrator");
            assertNotNull(output.id());
            assertEquals(output.id(), "admin");
            assertSent(server, "GET", "/user/user/api/json");
        } finally {
            server.shutdown();
        }
    }

    @Test
    public void testGenerateNewApiToken() throws Exception {
        MockWebServer server = mockWebServer();

        String body = payloadFromResource("/api-token.json");
        server.enqueue(new MockResponse().setBody(body).setResponseCode(200));
        try (JenkinsApi jenkinsApi = api(server.url("/").url())) {
            UserApi api = jenkinsApi.userApi();
            ApiToken output = api.generateNewToken("random");
            assertNotNull(output);
            assertNotNull(output.status());
            assertEquals(output.status(), "ok");
            assertNotNull(output.data());
            assertNotNull(output.data().tokenName());
            assertEquals(output.data().tokenName(), "kb-token");
            assertNotNull(output.data().tokenUuid());
            assertEquals(output.data().tokenUuid(), "8c42630b-4be5-4f51-b4e9-f17a8ac07521");
            assertNotNull(output.data().tokenValue());
            assertEquals(output.data().tokenValue(), "112fe6e9b1b94eb1ee58f0ea4f5a1ac7bf");
            assertSentWithFormData(server, "POST", "/user/user/descriptorByName/jenkins.security.ApiTokenProperty/generateNewToken", "newTokenName=random");
        } finally {
            server.shutdown();
        }
    }

    // TODO: testRevokeApiToken
    // TODO: testRevokeApiTokenWithEmptyUuid
}
