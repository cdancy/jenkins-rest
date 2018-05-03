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

import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertTrue;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.testng.annotations.Test;

import com.cdancy.jenkins.rest.BaseJenkinsApiLiveTest;
import com.cdancy.jenkins.rest.domain.common.RequestStatus;
import com.cdancy.jenkins.rest.domain.job.BuildInfo;
import com.cdancy.jenkins.rest.domain.job.JobInfo;
import com.cdancy.jenkins.rest.domain.job.ProgressiveText;

import com.google.common.collect.Lists;

@Test(groups = "live", testName = "SystemApiLiveTest", singleThreaded = true)
public class JobsApiLiveTest extends BaseJenkinsApiLiveTest {

    private Integer queueId;
    private Integer buildNumber;

    @Test
    public void testCreateJob() {
        String config = payloadFromResource("/freestyle-project-no-params.xml");
        RequestStatus success = api().create(null, "DevTest", config);
        assertTrue(success.value());
    }

    @Test(dependsOnMethods = "testCreateJob")
    public void testGetJobInfo() {
        JobInfo output = api().jobInfo("DevTest");
        assertNotNull(output);
        assertTrue(output.name().equals("DevTest"));
        assertNull(output.lastBuild());
        assertNull(output.firstBuild());
        assertTrue(output.builds().isEmpty());
    }

    @Test(dependsOnMethods = "testGetJobInfo")
    public void testLastBuildNumberOnJobWithNoBuilds() {
        Integer output = api().lastBuildNumber("DevTest");
        assertNull(output);
    }

    @Test(dependsOnMethods = "testLastBuildNumberOnJobWithNoBuilds")
    public void testLastBuildTimestampOnJobWithNoBuilds() {
        String output = api().lastBuildTimestamp("DevTest");
        assertNull(output);
    }

    @Test(dependsOnMethods = "testLastBuildTimestampOnJobWithNoBuilds")
    public void testBuildJob() {
        queueId = api().build("DevTest");
        assertNotNull(queueId);
        assertTrue(queueId > 0);
    }

    @Test(dependsOnMethods = "testBuildJob")
    public void testLastBuildNumberOnJob() {
        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        buildNumber = api().lastBuildNumber("DevTest");
        assertNotNull(buildNumber);
        assertTrue(buildNumber == 1);
    }

    @Test(dependsOnMethods = "testLastBuildNumberOnJob")
    public void testLastBuildTimestamp() {
        String output = api().lastBuildTimestamp("DevTest");
        assertNotNull(output);
    }

    @Test(dependsOnMethods = "testLastBuildTimestamp")
    public void testLastBuildGetProgressiveText() {
        ProgressiveText output = api().progressiveText("DevTest", 0);
        assertNotNull(output);
        assertTrue(output.size() > 0);
        assertFalse(output.hasMoreData());
    }

    @Test(dependsOnMethods = "testLastBuildGetProgressiveText")
    public void testGetBuildInfo() {
        BuildInfo output = api().buildInfo("DevTest", buildNumber);
        assertNotNull(output);
        assertTrue(output.fullDisplayName().equals("DevTest #" + buildNumber));
        assertTrue(output.queueId() == queueId);
    }

    @Test(dependsOnMethods = "testGetBuildInfo")
    public void testCreateJobThatAlreadyExists() {
        String config = payloadFromResource("/freestyle-project.xml");
        RequestStatus success = api().create(null, "DevTest", config);
        assertFalse(success.value());
    }

    @Test(dependsOnMethods = "testCreateJobThatAlreadyExists")
    public void testSetDescription() {
        boolean success = api().description("DevTest", "RandomDescription");
        assertTrue(success);
    }

    @Test(dependsOnMethods = "testSetDescription")
    public void testGetDescription() {
        String output = api().description("DevTest");
        assertTrue(output.equals("RandomDescription"));
    }

    @Test(dependsOnMethods = "testGetDescription")
    public void testGetConfig() {
        String output = api().config("DevTest");
        assertNotNull(output);
    }

    @Test(dependsOnMethods = "testGetConfig")
    public void testUpdateConfig() {
        String config = payloadFromResource("/freestyle-project.xml");
        boolean success = api().config("DevTest", config);
        assertTrue(success);
    }

    @Test(dependsOnMethods = "testUpdateConfig")
    public void testBuildJobWithParameters() {
        Map<String, List<String>> params = new HashMap<>();
        params.put("SomeKey", Lists.newArrayList("SomeVeryNewValue"));
        Integer output = api().buildWithParameters("DevTest", params);
        assertNotNull(output);
        assertTrue(output > 0);
    }

    @Test(dependsOnMethods = "testBuildJobWithParameters")
    public void testDisableJob() {
        boolean success = api().disable("DevTest");
        assertTrue(success);
    }

    @Test(dependsOnMethods = "testDisableJob")
    public void testDisableJobAlreadyDisabled() {
        boolean success = api().disable("DevTest");
        assertTrue(success);
    }

    @Test(dependsOnMethods = "testDisableJobAlreadyDisabled")
    public void testEnableJob() {
        boolean success = api().enable("DevTest");
        assertTrue(success);
    }

    @Test(dependsOnMethods = "testEnableJob")
    public void testEnableJobAlreadyEnabled() {
        boolean success = api().enable("DevTest");
        assertTrue(success);
    }

    @Test(dependsOnMethods = "testEnableJobAlreadyEnabled")
    public void testDeleteJob() {
        RequestStatus success = api().delete("DevTest");
        assertNotNull(success);
        assertTrue(success.value());
    }

    @Test
    public void testGetJobInfoNonExistentJob() {
        JobInfo output = api().jobInfo(randomString());
        assertNull(output);
    }

    @Test
    public void testDeleteJobNonExistent() {
        RequestStatus success = api().delete(randomString());
        assertNotNull(success);
        assertFalse(success.value());
    }

    @Test
    public void testGetConfigNonExistentJob() {
        String output = api().config(randomString());
        assertNull(output);
    }

    @Test
    public void testSetDescriptionNonExistentJob() {
        boolean success = api().description(randomString(), "RandomDescription");
        assertFalse(success);
    }

    @Test
    public void testGetDescriptionNonExistentJob() {
        String output = api().description(randomString());
        assertNull(output);
    }

    @Test
    public void testBuildNonExistentJob() {
        Integer output = api().build(randomString());
        assertNull(output);
    }

    @Test
    public void testGetBuildInfoNonExistentJob() {
        BuildInfo output = api().buildInfo(randomString(), 123);
        assertNull(output);
    }

    @Test
    public void testBuildNonExistentJobWithParams() {
        Map<String, List<String>> params = new HashMap<>();
        params.put("SomeKey", Lists.newArrayList("SomeVeryNewValue"));
        Integer output = api().buildWithParameters(randomString(), params);
        assertNull(output);
    }

    private JobsApi api() {
        return api.jobsApi();
    }
}
