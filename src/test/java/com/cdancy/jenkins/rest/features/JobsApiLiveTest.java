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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.cdancy.jenkins.rest.BaseJenkinsApiLiveTest;
import com.cdancy.jenkins.rest.domain.common.LongResponse;
import com.cdancy.jenkins.rest.domain.common.RequestStatus;
import com.cdancy.jenkins.rest.domain.job.*;
import com.cdancy.jenkins.rest.domain.plugins.Plugin;
import com.cdancy.jenkins.rest.domain.plugins.Plugins;
import com.cdancy.jenkins.rest.domain.queue.QueueItem;
import com.google.common.collect.Lists;
import org.testng.annotations.Test;

import static org.testng.Assert.*;

@Test(groups = "live", testName = "JobsApiLiveTest", singleThreaded = true)
public class JobsApiLiveTest extends BaseJenkinsApiLiveTest {

    private LongResponse queueId;
    private LongResponse queueIdForAnotherJob;
    private Integer buildNumber;
    private static final String FOLDER_PLUGIN_NAME = "cloudbees-folder";
    private static final String FOLDER_PLUGIN_VERSION = "latest";

    private static final String FREESTYLE_JOB_NAME = "FreeStyleSleep";
    private static final String PIPELINE_JOB_NAME = "PipelineSleep";
    private static final String PIPELINE_WITH_ACTION_JOB_NAME = "PipelineAction";

    @Test
    public void testCreateJob() {
        String config = payloadFromResource("/freestyle-project-no-params.xml");
        RequestStatus success = api().create(null, "DevTest", config);
        assertTrue(success.value());
    }

    // The next 3 tests must run one after the other as they use the same Job
    @Test
    public void testStopFreeStyleBuild() throws InterruptedException {
        String config = payloadFromResource("/freestyle-project-sleep-10-task.xml");
        RequestStatus createStatus = api().create(null, FREESTYLE_JOB_NAME, config);
        assertTrue(createStatus.value());
        LongResponse qId = api().build(null, FREESTYLE_JOB_NAME);
        assertNotNull(qId);
        assertTrue(qId.value() > 0);
        QueueItem queueItem = getRunningQueueItem(qId.value());
        assertNotNull(queueItem);
        assertNotNull(queueItem.executable());
        assertNotNull(queueItem.executable().number());
        RequestStatus stopStatus = api().stop(null, FREESTYLE_JOB_NAME, queueItem.executable().number());
        assertTrue(stopStatus.value());
        BuildInfo buildInfo = getCompletedBuild(FREESTYLE_JOB_NAME, queueItem);
        assertEquals(buildInfo.result(), "ABORTED");
    }

    @Test(dependsOnMethods = "testStopFreeStyleBuild")
    public void testTermFreeStyleBuild() throws InterruptedException {
        LongResponse qId = api().build(null, FREESTYLE_JOB_NAME);
        assertNotNull(qId);
        assertTrue(qId.value() > 0);
        QueueItem queueItem = getRunningQueueItem(qId.value());
        assertNotNull(queueItem);
        assertNotNull(queueItem.executable());
        assertNotNull(queueItem.executable().number());
        RequestStatus termStatus = api().term(null, FREESTYLE_JOB_NAME, queueItem.executable().number());
        // Strangely, term does not work on FreeStyleBuild
        assertFalse(termStatus.value());
        assertEquals(termStatus.errors().size(), 1);
        assertEquals(termStatus.errors().get(0).message(), "The term operation does not exist for " +
            System.getProperty("test.jenkins.endpoint") +
            "/job/"+FREESTYLE_JOB_NAME+"/"+queueItem.executable().number()+"/term/, try stop instead.");
        assertEquals(termStatus.errors().get(0).exceptionName(), "com.cdancy.jenkins.rest.exception.RedirectTo404Exception");
        api().stop(null, FREESTYLE_JOB_NAME, queueItem.executable().number());
        BuildInfo buildInfoStop = getCompletedBuild(FREESTYLE_JOB_NAME, queueItem);
        assertEquals(buildInfoStop.result(), "ABORTED");
    }

    @Test(dependsOnMethods = "testTermFreeStyleBuild")
    public void testKillFreeStyleBuild() throws InterruptedException {
        LongResponse qId = api().build(null, FREESTYLE_JOB_NAME);
        assertNotNull(qId);
        assertTrue(qId.value() > 0);
        QueueItem queueItem = getRunningQueueItem(qId.value());
        assertNotNull(queueItem);
        assertNotNull(queueItem.executable());
        assertNotNull(queueItem.executable().number());
        RequestStatus killStatus = api().kill(null, FREESTYLE_JOB_NAME, queueItem.executable().number());
        // Strangely, kill does not work on FreeStyleBuild
        assertFalse(killStatus.value());
        assertEquals(killStatus.errors().size(), 1);
        assertEquals(killStatus.errors().get(0).message(), "The kill operation does not exist for " +
            System.getProperty("test.jenkins.endpoint") +
            "/job/"+FREESTYLE_JOB_NAME+"/"+queueItem.executable().number()+"/kill/, try stop instead.");
        assertEquals(killStatus.errors().get(0).exceptionName(), "com.cdancy.jenkins.rest.exception.RedirectTo404Exception");
        api().stop(null, FREESTYLE_JOB_NAME, queueItem.executable().number());
        BuildInfo buildInfoStop = getCompletedBuild(FREESTYLE_JOB_NAME, queueItem);
        assertEquals(buildInfoStop.result(), "ABORTED");

        // Delete the job, it's no longer needed
        RequestStatus success = api().delete(null, FREESTYLE_JOB_NAME);
        assertNotNull(success);
        assertTrue(success.value());
    }

    // The next 3 tests must run one after the other as they use the same Job
    @Test
    public void testStopPipelineBuild() throws InterruptedException {
        String config = payloadFromResource("/pipeline.xml");
        RequestStatus createStatus = api().create(null, PIPELINE_JOB_NAME, config);
        assertTrue(createStatus.value());
        LongResponse qId = api().build(null, PIPELINE_JOB_NAME);
        assertNotNull(qId);
        assertTrue(qId.value() > 0);
        QueueItem queueItem = getRunningQueueItem(qId.value());
        assertNotNull(queueItem);
        assertNotNull(queueItem.executable());
        assertNotNull(queueItem.executable().number());
        RequestStatus stopStatus = api().stop(null, PIPELINE_JOB_NAME, queueItem.executable().number());
        assertTrue(stopStatus.value());
        BuildInfo buildInfo = getCompletedBuild(PIPELINE_JOB_NAME, queueItem);
        assertEquals(buildInfo.result(), "ABORTED");
    }

    @Test(dependsOnMethods = "testStopPipelineBuild")
    public void testTermPipelineBuild() throws InterruptedException {
        LongResponse qId = api().build(null, PIPELINE_JOB_NAME);
        assertNotNull(qId);
        assertTrue(qId.value() > 0);
        QueueItem queueItem = getRunningQueueItem(qId.value());
        assertNotNull(queueItem);
        assertNotNull(queueItem.executable());
        assertNotNull(queueItem.executable().number());
        RequestStatus termStatus = api().term(null, PIPELINE_JOB_NAME, queueItem.executable().number());
        assertTrue(termStatus.value());
        BuildInfo buildInfo = getCompletedBuild(PIPELINE_JOB_NAME, queueItem);
        assertEquals(buildInfo.result(), "ABORTED");
    }

    @Test(dependsOnMethods = "testTermPipelineBuild")
    public void testKillPipelineBuild() throws InterruptedException {
        LongResponse qId = api().build(null, PIPELINE_JOB_NAME);
        assertNotNull(qId);
        assertTrue(qId.value() > 0);
        QueueItem queueItem = getRunningQueueItem(qId.value());
        assertNotNull(queueItem);
        assertNotNull(queueItem.executable());
        assertNotNull(queueItem.executable().number());
        RequestStatus killStatus = api().kill(null, PIPELINE_JOB_NAME, queueItem.executable().number());
        assertTrue(killStatus.value());
        BuildInfo buildInfo = getCompletedBuild(PIPELINE_JOB_NAME, queueItem);
        assertEquals(buildInfo.result(), "ABORTED");

        // The Job is no longer needed, delete it.
        RequestStatus success = api().delete(null, PIPELINE_JOB_NAME);
        assertNotNull(success);
        assertTrue(success.value());
    }

    @Test(dependsOnMethods = {"testCreateJob", "testCreateJobForEmptyAndNullParams", "testKillPipelineBuild", "testKillFreeStyleBuild", "testDeleteFolders"})
    public void testGetJobListFromRoot() {
        JobList output = api().jobList("");
        assertNotNull(output);
        assertFalse(output.jobs().isEmpty());
        assertEquals(output.jobs().size(), 2);
    }

    @Test(dependsOnMethods = "testCreateJob")
    public void testGetJobInfo() {
        JobInfo output = api().jobInfo(null, "DevTest");
        assertNotNull(output);
        assertEquals(output.name(), "DevTest");
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
    public void testBuildJob() throws InterruptedException {
        queueId = api().build(null, "DevTest");
        assertNotNull(queueId);
        assertTrue(queueId.value() > 0);
        assertEquals(queueId.errors().size(), 0);
        // Before we exit the test, wait until the job runs
        QueueItem queueItem = getRunningQueueItem(queueId.value());
        getCompletedBuild("DevTest", queueItem);
    }

    @Test(dependsOnMethods = "testBuildJob")
    public void testLastBuildNumberOnJob() {
        buildNumber = api().lastBuildNumber(null, "DevTest");
        assertNotNull(buildNumber);
        assertEquals((int) buildNumber, 1);
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
        assertEquals("DevTest #" + buildNumber, output.fullDisplayName());
        assertEquals((long) queueId.value(), output.queueId());
    }

    @Test(dependsOnMethods = "testGetBuildInfo")
    public void testGetBuildParametersOfLastJob() {
        List<Parameter> parameters = api().buildInfo(null, "DevTest", 1).actions().get(0).parameters();
        assertEquals(parameters.size(), 0);
    }

    @Test
    public void testBuildInfoActions() throws InterruptedException {
        String config = payloadFromResource("/pipeline-with-action.xml");
        RequestStatus createStatus = api().create(null, PIPELINE_WITH_ACTION_JOB_NAME, config);
        assertTrue(createStatus.value());
        LongResponse qId = api().build(null, PIPELINE_WITH_ACTION_JOB_NAME);
        assertNotNull(qId);
        assertTrue(qId.value() > 0);
        QueueItem queueItem = getRunningQueueItem(qId.value());
        assertNotNull(queueItem);
        assertNotNull(queueItem.executable());
        assertNotNull(queueItem.executable().number());
        BuildInfo buildInfo = getCompletedBuild(PIPELINE_WITH_ACTION_JOB_NAME, queueItem);
        assertEquals(buildInfo.result(), "SUCCESS");
        System.out.println(buildInfo);
        boolean found = false;
        for (int idx = 0; idx < buildInfo.actions().size(); idx++) {
            if (buildInfo.actions().get(idx).text() != null) {
                if (buildInfo.actions().get(idx).text().equals("Hudson, we have a problem.") &&
                    buildInfo.actions().get(idx).iconPath().equals("error.svg") &&
                    buildInfo.actions().get(idx)._class().equals("com.jenkinsci.plugins.badge.action.BadgeSummaryAction")) {
                    found = true;
                }
            }
        }
        assertTrue(found);

        // The Job is no longer needed, delete it.
        RequestStatus success = api().delete(null, PIPELINE_WITH_ACTION_JOB_NAME);
        assertNotNull(success);
        assertTrue(success.value());
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
        assertEquals(output, "RandomDescription");
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
        LongResponse output = api().buildWithParameters(null, "DevTest", params);
        assertNotNull(output);
        assertTrue(output.value() > 0);
        assertEquals(output.errors().size(), 0);
    }

    @Test(dependsOnMethods = "testBuildJobWithParameters")
    public void testBuildJobWithNullParametersMap() {
        LongResponse output = api().buildWithParameters(null, "DevTest", null);
        assertNotNull(output);
        assertTrue(output.value() > 0);
        assertEquals(output.errors().size(), 0);
    }

    @Test(dependsOnMethods = "testBuildJobWithNullParametersMap")
    public void testBuildJobWithEmptyParametersMap() {
        LongResponse output = api().buildWithParameters(null, "DevTest", new HashMap<>());
        assertNotNull(output);
        assertNull(output.value());
        assertEquals(output.errors().size(), 1);
    }

    @Test(dependsOnMethods = "testBuildJobWithEmptyParametersMap")
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
    public void testRenameJob(){
        boolean success = api().rename(null,"DevTest","NewDevTest");
        assertTrue(success);
    }

    @Test(dependsOnMethods = "testRenameJob")
    public void testRenameJobNotExist(){
        boolean success = api().rename(null,"JobNotExist","NewDevTest");
        assertFalse(success);
    }

    @Test(dependsOnMethods = "testRenameJobNotExist")
    public void testDeleteJob() {
        RequestStatus success = api().delete(null, "NewDevTest");
        assertNotNull(success);
        assertTrue(success.value());
    }

    //
    // check for the presence of folder-plugin
    // If not present, attempt to install it.
    //
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
    public void testGetJobListInFolder() {
        JobList output = api().jobList("test-folder/test-folder-1");
        assertNotNull(output);
        assertFalse(output.jobs().isEmpty());
        assertEquals(output.jobs().size(), 1);
        assertEquals(output.jobs().get(0), Job.create("hudson.model.FreeStyleProject", "JobInFolder", System.getProperty("test.jenkins.endpoint")+"/job/test-folder/job/test-folder-1/job/JobInFolder/", "notbuilt"));
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
        assertEquals(output, "RandomDescription");
    }

    @Test(dependsOnMethods = "testGetDescriptionOfJobInFolder")
    public void testGetJobInfoInFolder() {
        JobInfo output = api().jobInfo("test-folder/test-folder-1", "JobInFolder");
        assertNotNull(output);
        assertEquals(output.name(), "JobInFolder");
        assertTrue(output.builds().isEmpty());
    }

    @Test(dependsOnMethods = "testGetJobInfoInFolder")
    public void testBuildWithParameters() throws InterruptedException {
        Map<String, List<String>> params = new HashMap<>();
        params.put("SomeKey", Lists.newArrayList("SomeVeryNewValue"));
        queueIdForAnotherJob = api().buildWithParameters("test-folder/test-folder-1", "JobInFolder", params);
        assertNotNull(queueIdForAnotherJob);
        assertTrue(queueIdForAnotherJob.value() > 0);
        QueueItem queueItem = getRunningQueueItem(queueIdForAnotherJob.value());
        assertNotNull(queueItem);
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
        assertEquals((long) queueIdForAnotherJob.value(), output.queueId());
    }

    @Test(dependsOnMethods = "testGetProgressiveText")
    public void testGetBuildParametersofJob() {
        List<Parameter> parameters = api().buildInfo("test-folder/test-folder-1", "JobInFolder",1).actions().get(0).parameters();
        assertNotNull(parameters);
        assertEquals(parameters.get(0).name(), "SomeKey");
        assertEquals(parameters.get(0).value(), "SomeVeryNewValue");
    }

    @Test(dependsOnMethods = "testGetProgressiveText")
    public void testGetBuildCausesOfJob() {
        List<Cause> causes = api().buildInfo("test-folder/test-folder-1", "JobInFolder",1).actions().get(1).causes();
        assertNotNull(causes);
        assertTrue(causes.size() > 0);
        assertNotNull(causes.get(0).shortDescription());
        assertNotNull(causes.get(0).userId());
        assertNotNull(causes.get(0).userName());
    }

    @Test(dependsOnMethods = "testGetProgressiveText")
    public void testGetProgressiveTextOfBuildNumber() {
        ProgressiveText output = api().progressiveText("test-folder/test-folder-1", "JobInFolder", 1,0);
        assertNotNull(output);
        assertTrue(output.size() > 0);
        assertFalse(output.hasMoreData());
    }

    @Test
    public void testCreateJobForEmptyAndNullParams() {
        String config = payloadFromResource("/freestyle-project-empty-and-null-params.xml");
        RequestStatus success = api().create(null, "JobForEmptyAndNullParams", config);
        assertTrue(success.value());
    }

    @Test(dependsOnMethods = "testCreateJobForEmptyAndNullParams")
    public void testBuildWithParametersOfJobForEmptyAndNullParams() throws InterruptedException {
        Map<String, List<String>> params = new HashMap<>();
        params.put("SomeKey1", Lists.newArrayList(""));
        params.put("SomeKey2", null);
        LongResponse job1 = api.jobsApi().buildWithParameters(null, "JobForEmptyAndNullParams", params);
        assertNotNull(job1);
        assertTrue(job1.value() > 0);
        assertEquals(job1.errors().size(), 0);
        QueueItem queueItem = getRunningQueueItem(job1.value());
        assertNotNull(queueItem);
    }

    @Test(dependsOnMethods = "testBuildWithParametersOfJobForEmptyAndNullParams")
    public void testGetBuildParametersOfJobForEmptyAndNullParams() {
        List<Parameter> parameters = api().buildInfo(null, "JobForEmptyAndNullParams", 1).actions().get(0).parameters();
        assertNotNull(parameters);
        assertEquals(parameters.get(0).name(), "SomeKey1");
        assertTrue(parameters.get(0).value().isEmpty());
        assertEquals(parameters.get(1).name(), "SomeKey2");
        assertTrue(parameters.get(1).value().isEmpty());
    }

    @Test(dependsOnMethods = { "testGetBuildParametersOfJobForEmptyAndNullParams", "testGetJobListFromRoot"})
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
    public void testRenameJonInFloder(){
        boolean success = api().rename("test-folder/test-folder-1", "JobInFolder", "NewJobInFolder");
        assertTrue(success);
    }

    @Test(dependsOnMethods = "testRenameJonInFloder")
    public void testDeleteJobInFolder() {
        RequestStatus success = api().delete("test-folder/test-folder-1", "NewJobInFolder");
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
        LongResponse output = api().build(null, randomString());
        assertNotNull(output);
        assertNull(output.value());
        assertTrue(output.errors().size() > 0);
        assertNotNull(output.errors().get(0).context());
        assertNotNull(output.errors().get(0).message());
        assertEquals(output.errors().get(0).exceptionName(), "org.jclouds.rest.ResourceNotFoundException");
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
        LongResponse output = api().buildWithParameters(null, randomString(), params);
        assertNotNull(output);
        assertNull(output.value());
        assertTrue(output.errors().size() > 0);
        assertNotNull(output.errors().get(0).context());
        assertNotNull(output.errors().get(0).message());
        assertEquals(output.errors().get(0).exceptionName(), "org.jclouds.rest.ResourceNotFoundException");
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
