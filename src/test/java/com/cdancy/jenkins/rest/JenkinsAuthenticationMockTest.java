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

package com.cdancy.jenkins.rest;

import com.cdancy.jenkins.rest.auth.AuthenticationType;
import com.cdancy.jenkins.rest.exception.UndetectableIdentityException;
import org.testng.annotations.Test;

import static com.google.common.io.BaseEncoding.base64;
import static org.testng.Assert.assertEquals;

public class JenkinsAuthenticationMockTest {

    @Test
    public void testAnonymousAuthentication() {
        JenkinsAuthentication ja = JenkinsAuthentication.builder().build();
        assertEquals(ja.identity, "anonymous");
        assertEquals(ja.authType(), AuthenticationType.Anonymous);
        assertEquals(ja.authValue(), base64().encode("anonymous:".getBytes()));
        assertEquals(ja.credential, ja.authValue());
    }

    @Test
    public void testUsernamePasswordAuthentication() {
        JenkinsAuthentication ja = JenkinsAuthentication.builder()
            .credentials("user:password")
            .build();
        assertEquals(ja.identity, "user");
        assertEquals(ja.authType(), AuthenticationType.UsernamePassword);
        assertEquals(ja.authValue(), base64().encode("user:password".getBytes()));
        assertEquals(ja.credential, ja.authValue());
    }

    @Test
    public void testUsernameApiTokenAuthentication() {
        JenkinsAuthentication ja = JenkinsAuthentication.builder()
            .apiToken("user:token")
            .build();
        assertEquals(ja.identity, "user");
        assertEquals(ja.authType(), AuthenticationType.UsernameApiToken);
        assertEquals(ja.authValue(), base64().encode("user:token".getBytes()));
        assertEquals(ja.credential, ja.authValue());
    }

    @Test
    public void testEncodedUsernamePasswordAuthentication() {
        String encoded = base64().encode("user:password".getBytes());
        JenkinsAuthentication ja = JenkinsAuthentication.builder()
            .credentials(encoded)
            .build();
        assertEquals(ja.identity, "user");
        assertEquals(ja.authType(), AuthenticationType.UsernamePassword);
        assertEquals(ja.authValue(), encoded);
        assertEquals(ja.credential, ja.authValue());
    }

    @Test
    public void testEncodedUsernameApiTokenAuthentication() {
        String encoded = base64().encode("user:token".getBytes());
        JenkinsAuthentication ja = JenkinsAuthentication.builder()
            .apiToken(encoded)
            .build();
        assertEquals(ja.identity, "user");
        assertEquals(ja.authType(), AuthenticationType.UsernameApiToken);
        assertEquals(ja.authValue(), encoded);
        assertEquals(ja.credential, ja.authValue());
    }

    @Test
    public void testEmptyUsernamePassword() {
        JenkinsAuthentication ja = JenkinsAuthentication.builder()
            .credentials(":")
            .build();
        assertEquals(ja.identity, "");
        assertEquals(ja.authType(), AuthenticationType.UsernamePassword);
        assertEquals(ja.authValue(), base64().encode(":".getBytes()));
        assertEquals(ja.credential, ja.authValue());
    }

    @Test
    public void testEmptyUsernameApiToken() {
        JenkinsAuthentication ja = JenkinsAuthentication.builder()
            .apiToken(":")
            .build();
        assertEquals(ja.identity, "");
        assertEquals(ja.authType(), AuthenticationType.UsernameApiToken);
        assertEquals(ja.authValue(), base64().encode(":".getBytes()));
        assertEquals(ja.credential, ja.authValue());
    }

    @Test
    public void testUndetectableCredential() {
        String invalid = base64().encode("no_colon_here".getBytes());
        try {
            JenkinsAuthentication.builder()
                .apiToken(invalid)
                .build();
        } catch (UndetectableIdentityException ex) {
          assertEquals(ex.getMessage(),
                "Unable to detect the identity being used in '" + invalid + "'. Supported types are a user:password, or a user:apiToken, or their base64 encoded value.");
        }
    }
}
