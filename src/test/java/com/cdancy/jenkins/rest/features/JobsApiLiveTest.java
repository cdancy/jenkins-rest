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

import com.cdancy.jenkins.rest.domain.job.Cause;
import com.cdancy.jenkins.rest.domain.job.Parameter;
import com.cdancy.jenkins.rest.domain.plugins.Plugin;
import com.cdancy.jenkins.rest.domain.plugins.Plugins;
import org.testng.annotations.Test;

import com.cdancy.jenkins.rest.BaseJenkinsApiLiveTest;
import com.cdancy.jenkins.rest.domain.common.IntegerResponse;
import com.cdancy.jenkins.rest.domain.common.RequestStatus;
import com.cdancy.jenkins.rest.domain.job.BuildInfo;
import com.cdancy.jenkins.rest.domain.job.JobInfo;
import com.cdancy.jenkins.rest.domain.job.ProgressiveText;

import com.google.common.collect.Lists;

@Test(groups = "live", testName = "SystemApiLiveTest", singleThreaded = true)
public class JobsApiLiveTest extends BaseJenkinsApiLiveTest {

    private IntegerResponse queueId;
    private IntegerResponse queueIdForAnotherJob;
    private Integer buildNumber;
    private static final String FOLDER_PLUGIN_NAME = "cloudbees-folder";
    private static final String FOLDER_PLUGIN_VERSION = "latest";

    @Test
    public void testCreateJob() {
        String config = payloadFromResource("/freestyle-project-no-params.xml");
        RequestStatus success = api().create(null, "DevTest", config);
        assertTrue(success.value());
    }

    @Test(dependsOnMethods = "testCreateJob")
    public void testGetJobInfo() {
        JobInfo output = api().jobInfo(null, "DevTest");
        assertNotNull(output);
        assertTrue(output.name().equals("DevTest"));
        assertNull(output.lastBuild());
        assertNull(output.firstBuild());
        assertTrue(output.builds().isEmpty());
    }

    @Test(dependsOnMethods = "testGetJobInfo")
    public void testLastBuildNumberOnJobWithNoBuilds() {
        Integer output = api().lastBuildNumber(null, "DevTest");
        assertNull(output);
    }

    @Test(dependsOnMethods = "testLastBuildNumberOnJobWithNoBuilds")
    public void testLastBuildTimestampOnJobWithNoBuilds() {
        String output = api().lastBuildTimestamp(null, "DevTest");
        assertNull(output);
    }

    @Test(dependsOnMethods = "testLastBuildTimestampOnJobWithNoBuilds")
    public void testBuildJob() {
        queueId = api().build(null, "DevTest");
        assertNotNull(queueId);
        assertTrue(queueId.value() > 0);
        assertTrue(queueId.errors().size() == 0);
    }

    @Test(dependsOnMethods = "testBuildJob")
    public void testLastBuildNumberOnJob() {
        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        buildNumber = api().lastBuildNumber(null, "DevTest");
        assertNotNull(buildNumber);
        assertTrue(buildNumber == 1);
    }

    @Test(dependsOnMethods = "testLastBuildNumberOnJob")
    public void testLastBuildTimestamp() {
        String output = api().lastBuildTimestamp(null, "DevTest");
        assertNotNull(output);
    }

    @Test(dependsOnMethods = "testLastBuildTimestamp")
    public void testLastBuildGetProgressiveText() {
        ProgressiveText output = api().progressiveText(null, "DevTest", 0);
        assertNotNull(output);
        assertTrue(output.size() > 0);
        assertFalse(output.hasMoreData());
    }

    @Test(dependsOnMethods = "testLastBuildGetProgressiveText")
    public void testGetBuildInfo() {
        BuildInfo output = api().buildInfo(null, "DevTest", buildNumber);
        assertNotNull(output);
        assertTrue(output.fullDisplayName().equals("DevTest #" + buildNumber));
        assertTrue(output.queueId() == queueId.value());
    }

    @Test(dependsOnMethods = "testGetBuildInfo")
    public void testGetBuildParametersOfLastJob() {
        List<Parameter> parameters = api().buildInfo(null, "DevTest", 1).actions().get(0).parameters();
        assertTrue(parameters.size() == 0);
    }

    @Test(dependsOnMethods = "testGetBuildParametersOfLastJob")
    public void testCreateJobThatAlreadyExists() {
        String config = payloadFromResource("/freestyle-project.xml");
        RequestStatus success = api().create(null, "DevTest", config);
        assertFalse(success.value());
    }

    @Test(dependsOnMethods = "testCreateJobThatAlreadyExists")
    public void testSetDescription() {
        boolean success = api().description(null, "DevTest", "RandomDescription");
        assertTrue(success);
    }

    @Test(dependsOnMethods = "testSetDescription")
    public void testGetDescription() {
        String output = api().description(null, "DevTest");
        assertTrue(output.equals("RandomDescription"));
    }

    @Test(dependsOnMethods = "testGetDescription")
    public void testGetConfig() {
        String output = api().config(null, "DevTest");
        assertNotNull(output);
    }

    @Test(dependsOnMethods = "testGetConfig")
    public void testUpdateConfig() {
        String config = payloadFromResource("/freestyle-project.xml");
        boolean success = api().config(null, "DevTest", config);
        assertTrue(success);
    }

    @Test(dependsOnMethods = "testUpdateConfig")
    public void testBuildJobWithParameters() {
        Map<String, List<String>> params = new HashMap<>();
        params.put("SomeKey", Lists.newArrayList("SomeVeryNewValue"));
        IntegerResponse output = api().buildWithParameters(null, "DevTest", params);
        assertNotNull(output);
        assertTrue(output.value() > 0);
        assertTrue(output.errors().size() == 0);
    }

    @Test(dependsOnMethods = "testBuildJobWithParameters")
    public void testDisableJob() {
        boolean success = api().disable(null, "DevTest");
        assertTrue(success);
    }

    @Test(dependsOnMethods = "testDisableJob")
    public void testDisableJobAlreadyDisabled() {
        boolean success = api().disable(null, "DevTest");
        assertTrue(success);
    }

    @Test(dependsOnMethods = "testDisableJobAlreadyDisabled")
    public void testEnableJob() {
        boolean success = api().enable(null, "DevTest");
        assertTrue(success);
    }

    @Test(dependsOnMethods = "testEnableJob")
    public void testEnableJobAlreadyEnabled() {
        boolean success = api().enable(null, "DevTest");
        assertTrue(success);
    }

    @Test(dependsOnMethods = "testEnableJobAlreadyEnabled")
    public void testDeleteJob() {
        RequestStatus success = api().delete(null, "DevTest");
        assertNotNull(success);
        assertTrue(success.value());
    }

    /*
     * check for the presence of folder-plugin
     * If not present, attempt to install it.
     */
    @Test
    public void testInstallFolderPlugin() throws Exception{
        long endTime = 0;
        long maxWaitTime = 5 * 60 * 1000;
        if(!isFolderPluginInstalled()) {
            RequestStatus status = api.pluginManagerApi().installNecessaryPlugins(FOLDER_PLUGIN_NAME + "@" + FOLDER_PLUGIN_VERSION);
            assertTrue(status.value());
            while(endTime <= maxWaitTime) {
                if(!isFolderPluginInstalled()) {
                    Thread.sleep(10000);
                    endTime += 10000;
                } else {
                    break;
                }
            }
        }
        assertTrue(isFolderPluginInstalled());
    }

    @Test(dependsOnMethods = "testInstallFolderPlugin")
    public void testCreateFoldersInJenkins() {
        String config = payloadFromResource("/folder-config.xml");
        RequestStatus success1 = api().create(null, "test-folder", config);
        assertTrue(success1.value());
        RequestStatus success2 = api().create("test-folder", "test-folder-1", config);
        assertTrue(success2.value());
    }

    @Test(dependsOnMethods = "testCreateFoldersInJenkins")
    public void testCreateJobInFolder() {
        String config = payloadFromResource("/freestyle-project-no-params.xml");
        RequestStatus success = api().create("test-folder/test-folder-1", "JobInFolder", config);
        assertTrue(success.value());
    }

    @Test(dependsOnMethods = "testCreateFoldersInJenkins")
    public void testCreateJobWithIncorrectFolderPath() {
        String config = payloadFromResource("/folder-config.xml");
        RequestStatus success = api().create("/test-folder//test-folder-1/", "Job",config);
        assertFalse(success.value());
    }

    @Test(dependsOnMethods = "testCreateJobInFolder")
    public void testUpdateJobConfigInFolder() {
        String config = payloadFromResource("/freestyle-project.xml");
        boolean success = api().config("test-folder/test-folder-1", "JobInFolder", config);
        assertTrue(success);
    }

    @Test(dependsOnMethods = "testUpdateJobConfigInFolder")
    public void testDisableJobInFolder() {
        boolean success = api().disable("test-folder/test-folder-1", "JobInFolder");
        assertTrue(success);
    }

    @Test(dependsOnMethods = "testDisableJobInFolder")
    public void testEnableJobInFolder() {
        boolean success = api().enable("test-folder/test-folder-1", "JobInFolder");
        assertTrue(success);
    }

    @Test(dependsOnMethods = "testEnableJobInFolder")
    public void testSetDescriptionOfJobInFolder() {
        boolean success = api().description("test-folder/test-folder-1", "JobInFolder", "RandomDescription");
        assertTrue(success);
    }

    @Test(dependsOnMethods = "testSetDescriptionOfJobInFolder")
    public void testGetDescriptionOfJobInFolder() {
        String output = api().description("test-folder/test-folder-1", "JobInFolder");
        assertTrue(output.equals("RandomDescription"));
    }

    @Test(dependsOnMethods = "testGetDescriptionOfJobInFolder")
    public void testGetJobInfoInFolder() {
        JobInfo output = api().jobInfo("test-folder/test-folder-1", "JobInFolder");
        assertNotNull(output);
        assertTrue(output.name().equals("JobInFolder"));
        assertTrue(output.builds().isEmpty());
    }

    @Test(dependsOnMethods = "testGetJobInfoInFolder")
    public void testBuildWithParameters() {
        Map<String, List<String>> params = new HashMap<>();
        params.put("SomeKey", Lists.newArrayList("SomeVeryNewValue"));
        queueIdForAnotherJob = api().buildWithParameters("test-folder/test-folder-1", "JobInFolder", params);
        assertNotNull(queueIdForAnotherJob);
        assertTrue(queueIdForAnotherJob.value() > 0);
    }

    @Test(dependsOnMethods = "testBuildWithParameters")
    public void testLastBuildTimestampOfJobInFolder() {
        String output = api().lastBuildTimestamp("test-folder/test-folder-1", "JobInFolder");
        assertNotNull(output);
    }

    @Test(dependsOnMethods = "testLastBuildTimestampOfJobInFolder")
    public void testGetProgressiveText() {
        ProgressiveText output = api().progressiveText("test-folder/test-folder-1", "JobInFolder", 0);
        assertNotNull(output);
        assertTrue(output.size() > 0);
        assertFalse(output.hasMoreData());
    }

    @Test(dependsOnMethods = "testGetProgressiveText")
    public void testGetBuildInfoOfJobInFolder() {
        BuildInfo output = api().buildInfo("test-folder/test-folder-1", "JobInFolder", 1);
        assertNotNull(output);
        assertTrue(output.fullDisplayName().contains("JobInFolder #1"));
        assertTrue(output.queueId() == queueIdForAnotherJob.value());
    }

    @Test(dependsOnMethods = "testGetProgressiveText")
    public void testGetBuildParametersofJob() {
        List<Parameter> parameters = api().buildInfo("test-folder/test-folder-1", "JobInFolder",1).actions().get(0).parameters();
        assertNotNull(parameters);
        assertTrue(parameters.get(0).name().equals("SomeKey"));
        assertTrue(parameters.get(0).value().equals("SomeVeryNewValue"));
    }

    @Test(dependsOnMethods = "testGetProgressiveText")
    public void testGetBuildCausesOfJob() {
        List<Cause> causes = api().buildInfo("test-folder/test-folder-1", "JobInFolder",1).actions().get(1).causes();
        assertNotNull(causes);
        assertTrue(causes.get(0).shortDescription().equals("Started by user admin"));
        assertTrue(causes.get(0).userId().equals("admin"));
        assertTrue(causes.get(0).userName().equals("admin"));
    }

    public void testCreateJobForEmptyAndNullParams() {
        String config = payloadFromResource("/freestyle-project-empty-and-null-params.xml");
        RequestStatus success = api().create(null, "JobForEmptyAndNullParams", config);
        assertTrue(success.value());
    }

    @Test(dependsOnMethods = "testCreateJobForEmptyAndNullParams")
    public void testBuildWithParametersOfJobForEmptyAndNullParams() {
        Map<String, List<String>> params = new HashMap<>();
        params.put("SomeKey1", Lists.newArrayList(""));
        params.put("SomeKey2", null);
        IntegerResponse job1 = api.jobsApi().buildWithParameters(null, "JobForEmptyAndNullParams", params);
        assertNotNull(job1);
        assertTrue(job1.value() > 0);
        assertTrue(job1.errors().size() == 0);
    }

    @Test(dependsOnMethods = "testBuildWithParametersOfJobForEmptyAndNullParams")
    public void testGetBuildParametersOfJobForEmptyAndNullParams() {
        while (api().jobInfo(null, "JobForEmptyAndNullParams").lastBuild() == null) {
            continue;
        }
        List<Parameter> parameters = api().buildInfo(null, "JobForEmptyAndNullParams", 1).actions().get(0).parameters();
        assertNotNull(parameters);
        assertTrue(parameters.get(0).name().equals("SomeKey1"));
        assertTrue(parameters.get(0).value().isEmpty());
        assertTrue(parameters.get(1).name().equals("SomeKey2"));
        assertTrue(parameters.get(1).value().isEmpty());
    }

    @Test(dependsOnMethods = "testGetBuildParametersOfJobForEmptyAndNullParams")
    public void testDeleteJobForEmptyAndNullParams() {
        RequestStatus success = api().delete(null, "JobForEmptyAndNullParams");
        assertTrue(success.value());
    }

    @Test(dependsOnMethods = "testCreateFoldersInJenkins")
    public void testCreateJobWithLeadingAndTrailingForwardSlashes() {
        String config = payloadFromResource("/freestyle-project-no-params.xml");
        RequestStatus success = api().create("/test-folder/test-folder-1/", "Job", config);
        assertTrue(success.value());
    }

    @Test(dependsOnMethods = "testCreateJobWithLeadingAndTrailingForwardSlashes")
    public void testDeleteJobWithLeadingAndTrailingForwardSlashes() {
        RequestStatus success = api().delete("/test-folder/test-folder-1/", "Job");
        assertTrue(success.value());
    }

    @Test(dependsOnMethods = "testGetBuildInfoOfJobInFolder")
    public void testDeleteJobInFolder() {
        RequestStatus success = api().delete("test-folder/test-folder-1", "JobInFolder");
        assertTrue(success.value());
    }

    @Test(dependsOnMethods = "testDeleteJobInFolder")
    public void testDeleteFolders() {
        RequestStatus success1 = api().delete("test-folder", "test-folder-1");
        assertTrue(success1.value());
        RequestStatus success2 = api().delete(null, "test-folder");
        assertTrue(success2.value());
    }

    @Test
    public void testGetJobInfoNonExistentJob() {
        JobInfo output = api().jobInfo(null, randomString());
        assertNull(output);
    }

    @Test
    public void testDeleteJobNonExistent() {
        RequestStatus success = api().delete(null, randomString());
        assertNotNull(success);
        assertFalse(success.value());
    }

    @Test
    public void testGetConfigNonExistentJob() {
        String output = api().config(null, randomString());
        assertNull(output);
    }

    @Test
    public void testSetDescriptionNonExistentJob() {
        boolean success = api().description(null, randomString(), "RandomDescription");
        assertFalse(success);
    }

    @Test
    public void testGetDescriptionNonExistentJob() {
        String output = api().description(null, randomString());
        assertNull(output);
    }

    @Test
    public void testBuildNonExistentJob() {
        IntegerResponse output = api().build(null, randomString());
        assertNotNull(output);
        assertNull(output.value());
        assertTrue(output.errors().size() > 0);
        assertNotNull(output.errors().get(0).context());
        assertNotNull(output.errors().get(0).message());
        assertTrue(output.errors().get(0).exceptionName().equals("org.jclouds.rest.ResourceNotFoundException"));
    }

    @Test
    public void testGetBuildInfoNonExistentJob() {
        BuildInfo output = api().buildInfo(null, randomString(), 123);
        assertNull(output);
    }

    @Test
    public void testBuildNonExistentJobWithParams() {
        Map<String, List<String>> params = new HashMap<>();
        params.put("SomeKey", Lists.newArrayList("SomeVeryNewValue"));
        IntegerResponse output = api().buildWithParameters(null, randomString(), params);
        assertNotNull(output);
        assertNull(output.value());
        assertTrue(output.errors().size() > 0);
        assertNotNull(output.errors().get(0).context());
        assertNotNull(output.errors().get(0).message());
        assertTrue(output.errors().get(0).exceptionName().equals("org.jclouds.rest.ResourceNotFoundException"));
    }

    private boolean isFolderPluginInstalled() {
        boolean installed = false;
        Plugins plugins = api.pluginManagerApi().plugins(3, null);
        for(Plugin plugin:plugins.plugins()) {
            if(plugin.shortName().equals(FOLDER_PLUGIN_NAME)) {
                installed = true;
                break;
            }
        }
        return installed;
    }

    private JobsApi api() {
        return api.jobsApi();
    }
}
