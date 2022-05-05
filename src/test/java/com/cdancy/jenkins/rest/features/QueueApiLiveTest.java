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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.cdancy.jenkins.rest.BaseJenkinsApiLiveTest;
import com.cdancy.jenkins.rest.domain.common.IntegerResponse;
import com.cdancy.jenkins.rest.domain.common.RequestStatus;
import com.cdancy.jenkins.rest.domain.queue.QueueItem;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

@Test(groups = "live", testName = "QueueApiLiveTest", singleThreaded = true)
public class QueueApiLiveTest extends BaseJenkinsApiLiveTest {

    @BeforeClass
    public void init() {
        String config = payloadFromResource("/freestyle-project-sleep-task.xml");
        RequestStatus success = api.jobsApi().create(null,"QueueTest", config);
        assertTrue(success.value());

        config = payloadFromResource("/freestyle-project.xml");
        success = api.jobsApi().create(null,"QueueTestSingleParam", config);
        assertTrue(success.value());

        config = payloadFromResource("/freestyle-project-sleep-task-multiple-params.xml");
        success = api.jobsApi().create(null,"QueueTestMultipleParams", config);
        assertTrue(success.value());
    }

    @Test
    public void testGetQueue() {
        IntegerResponse job1 = api.jobsApi().build(null, "QueueTest");
        assertNotNull(job1);
        assertEquals(job1.errors().size(), 0);
        IntegerResponse job2 = api.jobsApi().build(null, "QueueTest");
        assertNotNull(job2);
        assertEquals(job2.errors().size(), 0);
        List<QueueItem> queueItems = api().queue();
        assertTrue(queueItems.size() > 0);
        boolean foundLastKickedJob = false;
        for (QueueItem item : queueItems) {
            if (item.id() == job2.value()) {
                foundLastKickedJob = true;
                break;
            }
        }
        assertTrue(foundLastKickedJob);
    }

    @Test
    public void testGetPendingQueueItem() {
        IntegerResponse job1 = api.jobsApi().build(null,"QueueTest");
        assertNotNull(job1);
        assertEquals(job1.errors().size(), 0);
        IntegerResponse job2 = api.jobsApi().build(null,"QueueTest");
        assertNotNull(job2);
        assertEquals(job2.errors().size(), 0);

        // job2 is queue after job1, so while job1 runs, job2 is pending in the queue
        QueueItem queueItem = api().queueItem(job2.value());
        assertFalse(queueItem.cancelled());
        assertNotNull(queueItem.why());
        assertNull(queueItem.executable());
    }

    @Test
    public void testGetRunningQueueItem() throws InterruptedException {
        IntegerResponse job1 = api.jobsApi().build(null,"QueueTest");
        assertNotNull(job1);
        assertEquals(job1.errors().size(), 0);
        IntegerResponse job2 = api.jobsApi().build(null,"QueueTest");
        assertNotNull(job2);
        assertEquals(job2.errors().size(), 0);

        // job1 runs first, so we get its queueItem
        QueueItem queueItem = getRunningQueueItem(job1.value());

        // If null, it means the queueItem has been cancelled, which would not be normal in this test
        assertNotNull(queueItem);
        assertFalse(queueItem.cancelled());

        //  We exepect this build to run, consequently:
        //  * the why field should now be null
        //  * the executable field should NOT be null
        //  * the build number should be set to an integer
        //  * the url for the build should be set to a string
        assertNull(queueItem.why());
        assertNotNull(queueItem.executable());
    }

    @Test
    public void testQueueItemSingleParameters() throws InterruptedException {
        Map<String, List<String>> params = new HashMap<>();
        params.put("SomeKey", Lists.newArrayList("SomeVeryNewValue1"));
        IntegerResponse job1 = api.jobsApi().buildWithParameters(null,"QueueTestSingleParam", params);
        assertNotNull(job1);
        assertTrue(job1.value() > 0);
        assertEquals(job1.errors().size(), 0);

        // Jenkins will reject two consecutive build requests when the build parameter values are the same
        // So we must set some different parameter values
        params = new HashMap<>();
        params.put("SomeKey", Lists.newArrayList("SomeVeryNewValue2"));
        IntegerResponse job2 = api.jobsApi().buildWithParameters(null,"QueueTestSingleParam", params);
        assertNotNull(job2);
        assertTrue(job2.value() > 0);
        assertEquals(job2.errors().size(), 0);

        QueueItem queueItem = getRunningQueueItem(job1.value());
        assertNotNull(queueItem);
        assertFalse(queueItem.cancelled());

        Map <String, String> map = Maps.newHashMap();
        map.put("SomeKey", "SomeVeryNewValue1");
        assertEquals(queueItem.params(), map);
    }

    @Test
    public void testQueueItemMultipleParameters() throws InterruptedException {
        Map<String, List<String>> params = new HashMap<>();
        params.put("SomeKey1", Lists.newArrayList("SomeVeryNewValue1"));
        IntegerResponse job1 = api.jobsApi().buildWithParameters(null, "QueueTestMultipleParams",params);
        assertNotNull(job1);
        assertTrue(job1.value() > 0);
        assertEquals(job1.errors().size(), 0);

        // Jenkins will reject two consecutive build requests when the build parameter values are the same
        // So we must set some different parameter values
        params = new HashMap<>();
        params.put("SomeKey1", Lists.newArrayList("SomeVeryNewValue2"));
        IntegerResponse job2 = api.jobsApi().buildWithParameters(null, "QueueTestMultipleParams", params);
        assertNotNull(job2);
        assertTrue(job2.value() > 0);
        assertEquals(job2.errors().size(), 0);

        QueueItem queueItem = getRunningQueueItem(job1.value());
        assertNotNull(queueItem);
        assertFalse(queueItem.cancelled());

        Map <String, String> map = Maps.newHashMap();
        map.put("SomeKey1", "SomeVeryNewValue1");
        map.put("SomeKey2", "SomeValue2");
        map.put("SomeKey3", "SomeValue3");
        assertEquals(queueItem.params(), map);
    }

    @Test
    public void testQueueItemEmptyParameterValue() throws InterruptedException {
        Map<String, List<String>> params = new HashMap<>();
        params.put("SomeKey1", Lists.newArrayList(""));
        IntegerResponse job1 = api.jobsApi().buildWithParameters(null, "QueueTestMultipleParams",params);
        assertNotNull(job1);
        assertTrue(job1.value() > 0);
        assertEquals(job1.errors().size(), 0);

        QueueItem queueItem = getRunningQueueItem(job1.value());
        assertNotNull(queueItem);

        Map <String, String> map = Maps.newHashMap();
        map.put("SomeKey1", "");
        map.put("SomeKey2", "SomeValue2");
        map.put("SomeKey3", "SomeValue3");
        assertEquals(queueItem.params(), map);
    }

    @Test
    public void testGetCancelledQueueItem() {
        IntegerResponse job1 = api.jobsApi().build(null,"QueueTest");
        assertNotNull(job1);
        assertEquals(job1.errors().size(), 0);
        IntegerResponse job2 = api.jobsApi().build(null, "QueueTest");
        assertNotNull(job2);
        assertEquals(job2.errors().size(), 0);

        RequestStatus success = api().cancel(job2.value());
        assertNotNull(success);
        assertTrue(success.value());
        assertTrue(success.errors().isEmpty());

        QueueItem queueItem = api().queueItem(job2.value());
        assertTrue(queueItem.cancelled());
        assertNull(queueItem.why());
        assertNull(queueItem.executable());
    }

    @Test
    public void testCancelNonExistentQueueItem() {
        RequestStatus success = api().cancel(123456789);
        assertNotNull(success);
        assertTrue(success.value());
        assertTrue(success.errors().isEmpty());
    }

    @AfterClass
    public void finish() {
        RequestStatus success = api.jobsApi().delete(null,"QueueTest");
        assertNotNull(success);
        assertTrue(success.value());

        success = api.jobsApi().delete(null,"QueueTestSingleParam");
        assertNotNull(success);
        assertTrue(success.value());

        success = api.jobsApi().delete(null,"QueueTestMultipleParams");
        assertNotNull(success);
        assertTrue(success.value());
    }

    private QueueApi api() {
        return api.queueApi();
    }
}
