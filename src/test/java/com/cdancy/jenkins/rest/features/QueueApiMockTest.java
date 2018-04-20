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

import org.testng.annotations.Test;

import com.cdancy.jenkins.rest.JenkinsApi;
import com.cdancy.jenkins.rest.domain.queue.QueueItem;
import com.cdancy.jenkins.rest.BaseJenkinsMockTest;

import com.squareup.okhttp.mockwebserver.MockResponse;
import com.squareup.okhttp.mockwebserver.MockWebServer;

/**
 * Mock tests for the {@link com.cdancy.jenkins.rest.features.QueueApi} class.
 */
@Test(groups = "unit", testName = "QueueApiMockTest")
public class QueueApiMockTest extends BaseJenkinsMockTest {

    public void testGetQueue() throws Exception {
        MockWebServer server = mockWebServer();
        String body = payloadFromResource("/queue.json");
        server.enqueue(new MockResponse().setBody(body).setResponseCode(200));
        JenkinsApi jenkinsApi = api(server.getUrl("/"));
        QueueApi api = jenkinsApi.queueApi();
        try {
            List<QueueItem> output = api.queue();
            assertTrue(output.size() == 2);
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
        JenkinsApi jenkinsApi = api(server.getUrl("/"));
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
        JenkinsApi jenkinsApi = api(server.getUrl("/"));
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
        JenkinsApi jenkinsApi = api(server.getUrl("/"));
        int queueItemId = 143;
        int buildNumber = 14;
        QueueItem queueItem = jenkinsApi.queueApi().queueItem(queueItemId);
        try {
            assertFalse(queueItem.cancelled());
            assertNull(queueItem.why());
            assertNotNull(queueItem.executable());
            assertEquals((int) queueItem.executable().number(), (int) buildNumber);
            assertEquals(queueItem.executable().url(), "http://localhost:8082/job/test/" + buildNumber + "/");
            assertSent(server, "GET", "/queue/item/" + queueItemId + "/api/json");
        } finally {
            jenkinsApi.close();
            server.shutdown();
        }
    }

    public void testCancelQueueItem() throws Exception {
        MockWebServer server = mockWebServer();
        server.enqueue(new MockResponse().setResponseCode(404));
        JenkinsApi jenkinsApi = api(server.getUrl("/"));
        int queueItemId = 143;
        boolean result = jenkinsApi.queueApi().cancel(queueItemId);
        try {
            assertTrue(result);
            assertSentWithFormData(server, "POST", "/queue/cancelItem", "id=" + queueItemId);
        } finally {
            jenkinsApi.close();
            server.shutdown();
        }

    }
}
