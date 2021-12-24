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

import com.cdancy.jenkins.rest.BaseJenkinsApiLiveTest;
import com.cdancy.jenkins.rest.domain.common.RequestStatus;
import com.cdancy.jenkins.rest.domain.user.*;
import org.testng.annotations.Test;

import static org.testng.Assert.*;

@Test(groups = "live", testName = "UserApiLiveTest", singleThreaded = true)
public class UserApiLiveTest extends BaseJenkinsApiLiveTest {

    ApiToken token;

    @Test
    public void testGetUser() {
        User user = api().get();
        assertNotNull(user);
        assertNotNull(user.absoluteUrl());
        assertEquals(user.absoluteUrl(), System.getProperty("test.jenkins.endpoint") + "/user/admin");
        assertTrue(user.description() == null || user.description().equals(""));
        assertNotNull(user.fullName());
        assertEquals(user.fullName(), "admin");
        assertNotNull(user.id());
        assertEquals(user.id(), "admin");
    }

    @Test
    public void testGenerateNewToken() {
        token = api().generateNewToken("user-api-test-token");
        assertNotNull(token);
        assertEquals(token.status(), "ok");
        assertNotNull(token.data());
        assertNotNull(token.data().tokenName());
        assertEquals(token.data().tokenName(), "user-api-test-token");
        assertNotNull(token.data().tokenUuid());
        assertNotNull(token.data().tokenValue());
    }

    @Test(dependsOnMethods = "testGenerateNewToken")
    public void testRevokeApiToken() {
        RequestStatus status = api().revoke(token.data().tokenUuid());
        // Jenkins returns 200 whether the tokenUuid is correct or not.
        assertTrue(status.value());
    }

    @Test
    public void testRevokeApiTokenWithEmptyUuid() {
        RequestStatus status = api().revoke("");
        assertFalse(status.value());
        // TODO: Deal with the HTML response from Jenkins Stapler
    }

    private UserApi api() {
        return api.userApi();
    }
}
