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

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertTrue;

import java.util.List;
import java.util.Map;

import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.testng.annotations.Test;

import com.cdancy.jenkins.rest.JenkinsApi;
import com.cdancy.jenkins.rest.domain.queue.QueueItem;
import com.cdancy.jenkins.rest.BaseJenkinsMockTest;
import com.cdancy.jenkins.rest.domain.common.RequestStatus;

import com.google.common.collect.Maps;

/**
 * Mock tests for the {@link com.cdancy.jenkins.rest.features.QueueApi} class.
 */
@Test(groups = "unit", testName = "QueueApiMockTest")
public class QueueApiMockTest extends BaseJenkinsMockTest {

    public void testGetQueue() throws Exception {
        MockWebServer server = mockWebServer();
        String body = payloadFromResource("/queue.json");
        server.enqueue(new MockResponse().setBody(body).setResponseCode(200));
        JenkinsApi jenkinsApi = api(server.url("/").url());
        QueueApi api = jenkinsApi.queueApi();
        try {
            List<QueueItem> output = api.queue();
            assertEquals(output.size(), 2);
            assertSent(server, "GET", "/queue/api/json");
        } finally {
            jenkinsApi.close();
            server.shutdown();
        }
    }

    public void testGetPendingQueueItem() throws Exception {
        MockWebServer server = mockWebServer();
        String body = payloadFromResource("/queueItemPending.json");
        server.enqueue(new MockResponse().setBody(body).setResponseCode(200));
        JenkinsApi jenkinsApi = api(server.url("/").url());
        int queueItemId = 143;
        QueueItem queueItem = jenkinsApi.queueApi().queueItem(queueItemId);
        try {
            assertFalse(queueItem.cancelled());
            assertEquals(queueItem.why(), "Build #9 is already in progress (ETA:15 sec)");
            assertNull(queueItem.executable());
            assertSent(server, "GET", "/queue/item/" + queueItemId + "/api/json");
        } finally {
            jenkinsApi.close();
            server.shutdown();
        }
    }

    public void testGetCancelledQueueItem() throws Exception {
        MockWebServer server = mockWebServer();
        String body = payloadFromResource("/queueItemCancelled.json");
        server.enqueue(new MockResponse().setBody(body).setResponseCode(200));
        JenkinsApi jenkinsApi = api(server.url("/").url());
        int queueItemId = 143;
        QueueItem queueItem = jenkinsApi.queueApi().queueItem(queueItemId);
        try {
            assertTrue(queueItem.cancelled());
            assertNull(queueItem.why());
            assertNull(queueItem.executable());
            assertSent(server, "GET", "/queue/item/" + queueItemId + "/api/json");
        } finally {
            jenkinsApi.close();
            server.shutdown();
        }
    }

    public void testGetRunningQueueItem() throws Exception {
        MockWebServer server = mockWebServer();
        String body = payloadFromResource("/queueItemRunning.json");
        server.enqueue(new MockResponse().setBody(body).setResponseCode(200));
        JenkinsApi jenkinsApi = api(server.url("/").url());
        int queueItemId = 143;
        int buildNumber = 14;
        QueueItem queueItem = jenkinsApi.queueApi().queueItem(queueItemId);
        Map <String, String> map = Maps.newHashMap();
        map.put("a", "4");
        try {
            assertEquals(queueItem.params(), map);
            assertFalse(queueItem.cancelled());
            assertNull(queueItem.why());
            assertNotNull(queueItem.executable());
            assertEquals((int) queueItem.executable().number(), buildNumber);
            assertEquals(queueItem.executable().url(), "http://localhost:8082/job/test/" + buildNumber + "/");
            assertSent(server, "GET", "/queue/item/" + queueItemId + "/api/json");
        } finally {
            jenkinsApi.close();
            server.shutdown();
        }
    }

    public void testQueueItemMultipleParameters() throws Exception {
        MockWebServer server = mockWebServer();
        String body = payloadFromResource("/queueItemMultipleParameters.json");
        server.enqueue(new MockResponse().setBody(body).setResponseCode(200));
        JenkinsApi jenkinsApi = api(server.url("/").url());
        int queueItemId = 143;
        QueueItem queueItem = jenkinsApi.queueApi().queueItem(queueItemId);
        Map <String, String> map = Maps.newHashMap();
        map.put("a", "1");
        map.put("b", "2");
        map.put("c", "3");
        try {
            assertEquals(queueItem.params(), map);
        } finally {
            jenkinsApi.close();
            server.shutdown();
        }
    }

    public void testQueueItemEmptyParameterValue() throws Exception {
        MockWebServer server = mockWebServer();
        String body = payloadFromResource("/queueItemEmptyParameterValue.json");
        server.enqueue(new MockResponse().setBody(body).setResponseCode(200));
        JenkinsApi jenkinsApi = api(server.url("/").url());
        int queueItemId = 143;
        QueueItem queueItem = jenkinsApi.queueApi().queueItem(queueItemId);
        Map <String, String> map = Maps.newHashMap();
        map.put("a", "1");
        map.put("b", "");
        map.put("c", "3");
        try {
            assertEquals(queueItem.params(), map);
        } finally {
            jenkinsApi.close();
            server.shutdown();
        }
    }

    public void testCancelQueueItem() throws Exception {
        MockWebServer server = mockWebServer();
        server.enqueue(new MockResponse().setResponseCode(404));
        JenkinsApi jenkinsApi = api(server.url("/").url());
        int queueItemId = 143;
        RequestStatus result = jenkinsApi.queueApi().cancel(queueItemId);
        try {
            assertNotNull(result);
            assertTrue(result.value());
            assertTrue(result.errors().isEmpty());
            assertSentWithFormData(server, "POST", "/queue/cancelItem", "id=" + queueItemId);
        } finally {
            jenkinsApi.close();
            server.shutdown();
        }
    }

    public void testCancelNonExistentQueueItem() throws Exception {
        MockWebServer server = mockWebServer();
        server.enqueue(new MockResponse().setResponseCode(500));
        JenkinsApi jenkinsApi = api(server.url("/").url());
        int queueItemId = 143;
        RequestStatus result = jenkinsApi.queueApi().cancel(queueItemId);
        try {
            assertNotNull(result);
            assertFalse(result.value());
            assertFalse(result.errors().isEmpty());
            assertSentWithFormData(server, "POST", "/queue/cancelItem", "id=" + queueItemId);
        } finally {
            jenkinsApi.close();
            server.shutdown();
        }
    }

    public void testQueueItemNullTaskName() throws Exception {
        MockWebServer server = mockWebServer();
        String body = payloadFromResource("/queueItemNullTaskName.json");
        server.enqueue(new MockResponse().setBody(body).setResponseCode(200));
        JenkinsApi jenkinsApi = api(server.url("/").url());
        int queueItemId = 143;
        QueueItem queueItem = jenkinsApi.queueApi().queueItem(queueItemId);
        try {
            assertFalse(queueItem.cancelled());
            assertEquals(queueItem.why(), "Build #9 is already in progress (ETA:15 sec)");
            assertNull(queueItem.executable());
            assertSent(server, "GET", "/queue/item/" + queueItemId + "/api/json");
        } finally {
            jenkinsApi.close();
            server.shutdown();
        }
    }

    public void testQueueItemMissingTaskUrl() throws Exception {
        MockWebServer server = mockWebServer();
        String body = payloadFromResource("/queueItemMissingTaskUrl.json");
        server.enqueue(new MockResponse().setBody(body).setResponseCode(200));
        JenkinsApi jenkinsApi = api(server.url("/").url());
        int queueItemId = 143;
        QueueItem queueItem = jenkinsApi.queueApi().queueItem(queueItemId);
        try {
            assertFalse(queueItem.cancelled());
            assertEquals(queueItem.why(), "Just a random message here");
            assertNull(queueItem.executable());
            assertSent(server, "GET", "/queue/item/" + queueItemId + "/api/json");
        } finally {
            jenkinsApi.close();
            server.shutdown();
        }
    }

}
