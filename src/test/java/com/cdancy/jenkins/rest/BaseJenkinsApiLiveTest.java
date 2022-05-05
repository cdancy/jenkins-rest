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

import com.cdancy.jenkins.rest.config.JenkinsAuthenticationModule;
import static org.jclouds.util.Strings2.toStringAndClose;

import java.io.IOException;
import java.util.Objects;
import java.util.Properties;
import java.util.UUID;

import com.cdancy.jenkins.rest.domain.job.BuildInfo;
import com.cdancy.jenkins.rest.domain.queue.QueueItem;
import org.jclouds.Constants;
import org.jclouds.apis.BaseApiLiveTest;
import org.testng.annotations.Test;

import com.google.common.base.Charsets;
import com.google.common.base.Throwables;
import com.google.common.collect.ImmutableSet;
import com.google.inject.Module;

@Test(groups = "live")
public class BaseJenkinsApiLiveTest extends BaseApiLiveTest<JenkinsApi> {

    protected final JenkinsAuthentication jenkinsAuthentication;

    public BaseJenkinsApiLiveTest() {
        provider = "jenkins";
        this.jenkinsAuthentication = TestUtilities.inferTestAuthentication();
    }

    @Override
    protected Properties setupProperties() {
        Properties overrides = super.setupProperties();
        overrides.setProperty(Constants.PROPERTY_MAX_RETRIES, "0");
        return overrides;
    }

    protected String randomString() {
        return UUID.randomUUID().toString().replaceAll("-", "");
    }

    public String payloadFromResource(String resource) {
        try {
            return new String(toStringAndClose(Objects.requireNonNull(getClass().getResourceAsStream(resource))).getBytes(Charsets.UTF_8));
        } catch (IOException e) {
            throw Throwables.propagate(e);
        }
    }

    @Override
    protected Iterable<Module> setupModules() {
        final JenkinsAuthenticationModule credsModule = new JenkinsAuthenticationModule(this.jenkinsAuthentication);
        return ImmutableSet.of(getLoggingModule(), credsModule);
    }

    /**
     * Return a queue item that is being built.
     * If the queue item is canceled before the build is launched, null is returned.
     * To prevent the test from hanging, this method times out after 10 attempts and the queue item is returned the way it is.
     * @param queueId  The queue id returned when asking Jenkins to run a build.
     * @return Null if the queue item has been canceled before it has had a chance to run,
     *         otherwise the QueueItem element is returned, but this does not guarantee that the build runs.
     *         The caller has to check the value of queueItem.executable, and if it is null, the queue item is still pending.
     *
     */
    protected QueueItem getRunningQueueItem(int queueId) throws InterruptedException {
        int max = 10;
        QueueItem queueItem = api.queueApi().queueItem(queueId);
        while (max > 0) {
            if (queueItem.cancelled()) return null;
            if (queueItem.executable() != null) {
                return queueItem;
            }
            Thread.sleep(2000);
            queueItem = api.queueApi().queueItem(queueId);
            max = max - 1;
        }
        return queueItem;
    }

    protected BuildInfo getCompletedBuild(String jobName, QueueItem queueItem) throws InterruptedException {
        int max = 10;
        BuildInfo buildInfo = api.jobsApi().buildInfo(null, jobName, queueItem.executable().number());
        while (buildInfo.result() == null) {
            Thread.sleep(2000);
            buildInfo = api.jobsApi().buildInfo(null, jobName, queueItem.executable().number());
        }
        return buildInfo;
    }

}
