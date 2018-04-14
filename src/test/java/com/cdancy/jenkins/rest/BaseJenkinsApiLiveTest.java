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
import java.util.Properties;
import java.util.UUID;

import org.jclouds.Constants;
import org.jclouds.apis.BaseApiLiveTest;
import org.testng.annotations.Test;

import com.google.common.base.Charsets;
import com.google.common.base.Throwables;
import com.google.common.collect.ImmutableSet;
import com.google.inject.Module;

@Test(groups = "live")
public class BaseJenkinsApiLiveTest extends BaseApiLiveTest<JenkinsApi> {

    protected final String defaultBitbucketGroup = "stash-users";
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
            return new String(toStringAndClose(getClass().getResourceAsStream(resource)).getBytes(Charsets.UTF_8));
        } catch (IOException e) {
            throw Throwables.propagate(e);
        }
    }

    @Override
    protected Iterable<Module> setupModules() {
        final JenkinsAuthenticationModule credsModule = new JenkinsAuthenticationModule(this.jenkinsAuthentication);
        return ImmutableSet.<Module> of(getLoggingModule(), credsModule);
    }
}
