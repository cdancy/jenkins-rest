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

import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;
import static org.testng.Assert.assertFalse;

import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.testng.annotations.Test;

import com.cdancy.jenkins.rest.JenkinsApi;
import com.cdancy.jenkins.rest.BaseJenkinsMockTest;
import com.cdancy.jenkins.rest.domain.common.RequestStatus;
import com.cdancy.jenkins.rest.domain.plugins.Plugins;
import com.google.common.collect.Maps;

import java.util.Map;

/**
 * Mock tests for the {@link com.cdancy.jenkins.rest.features.PluginManagerApi} class.
 */
@Test(groups = "unit", testName = "PluginManagerApiMockTest")
public class PluginManagerApiMockTest extends BaseJenkinsMockTest {

    public void testGetPlugins() throws Exception {
        final MockWebServer server = mockWebServer();
        server.enqueue(new MockResponse().setBody(payloadFromResource("/plugins.json")).setResponseCode(200));

        final JenkinsApi jenkinsApi = api(server.url("/").url());
        final PluginManagerApi api = jenkinsApi.pluginManagerApi();
        try {
            final Plugins plugins = api.plugins(3, null);
            assertNotNull(plugins);
            assertTrue(plugins.errors().isEmpty());
            assertFalse(plugins.plugins().isEmpty());
            assertNotNull(plugins.plugins().get(0).shortName());
            final Map<String, Object> queryParams = Maps.newHashMap();
            queryParams.put("depth", 3);
            assertSent(server, "GET", "/pluginManager/api/json", queryParams);
        } finally {
            jenkinsApi.close();
            server.shutdown();
        }
    }

    public void testGetPluginsOnAuthException() throws Exception {
        final MockWebServer server = mockWebServer();
        server.enqueue(new MockResponse().setResponseCode(401));

        final JenkinsApi jenkinsApi = api(server.url("/").url());
        final PluginManagerApi api = jenkinsApi.pluginManagerApi();
        try {
            final Plugins plugins = api.plugins(3, null);
            assertNotNull(plugins);
            assertNull(plugins.clazz());
            assertFalse(plugins.errors().isEmpty());
            assertTrue(plugins.errors().get(0).exceptionName().endsWith("AuthorizationException"));
            final Map<String, Object> queryParams = Maps.newHashMap();
            queryParams.put("depth", 3);
            assertSent(server, "GET", "/pluginManager/api/json", queryParams);
        } finally {
            jenkinsApi.close();
            server.shutdown();
        }
    }

    public void testInstallNecessaryPlugins() throws Exception {
        final MockWebServer server = mockWebServer();
        server.enqueue(new MockResponse().setResponseCode(200));

        final JenkinsApi jenkinsApi = api(server.url("/").url());
        final PluginManagerApi api = jenkinsApi.pluginManagerApi();
        try {
            final RequestStatus status = api.installNecessaryPlugins("artifactory@2.2.1");
            assertNotNull(status);
            assertTrue(status.value());
            assertTrue(status.errors().isEmpty());
            assertSent(server, "POST", "/pluginManager/installNecessaryPlugins");
        } finally {
            jenkinsApi.close();
            server.shutdown();
        }
    }

    public void testInstallNecessaryPluginsOnAuthException() throws Exception {
        final MockWebServer server = mockWebServer();
        server.enqueue(new MockResponse().setResponseCode(401));

        final JenkinsApi jenkinsApi = api(server.url("/").url());
        final PluginManagerApi api = jenkinsApi.pluginManagerApi();
        try {
            final RequestStatus status = api.installNecessaryPlugins("artifactory@2.2.1");
            assertNotNull(status);
            assertFalse(status.value());
            assertFalse(status.errors().isEmpty());
            assertTrue(status.errors().get(0).exceptionName().endsWith("AuthorizationException"));
            assertSent(server, "POST", "/pluginManager/installNecessaryPlugins");
        } finally {
            jenkinsApi.close();
            server.shutdown();
        }
    }
}
