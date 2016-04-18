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

import java.util.List;

import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.cdancy.jenkins.rest.BaseJenkinsApiLiveTest;
import com.cdancy.jenkins.rest.domain.queue.QueueItem;

@Test(groups = "live", testName = "QueueApiLiveTest", singleThreaded = true)
public class QueueApiLiveTest extends BaseJenkinsApiLiveTest {

   @BeforeClass
   public void init() {
      String config = payloadFromResource("/freestyle-project-sleep-task.xml");
      boolean success = api.jobsApi().create("QueueTest", config);
      assertTrue(success);
   }

   @Test
   public void testGetQueue() {
      Integer job1 = api.jobsApi().build("QueueTest");
      assertNotNull(job1);
      Integer job2 = api.jobsApi().build("QueueTest");
      assertNotNull(job2);
      List<QueueItem> queueItems = api().queue();
      assertTrue(queueItems.size() > 0);
      boolean foundLastKickedJob = false;
      for (QueueItem item : queueItems) {
         if (item.id() == job2.intValue()) {
            foundLastKickedJob = true;
            break;
         }
      }
      assertTrue(foundLastKickedJob);
   }

   @AfterClass
   public void finalize() {
      boolean success = api.jobsApi().delete("QueueTest");
      assertTrue(success);
   }

   private QueueApi api() {
      return api.queueApi();
   }
}
