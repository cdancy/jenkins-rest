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

import java.io.Closeable;

import org.jclouds.rest.annotations.Delegate;

import com.cdancy.jenkins.rest.features.ConfigurationAsCodeApi;
import com.cdancy.jenkins.rest.features.CrumbIssuerApi;
import com.cdancy.jenkins.rest.features.JobsApi;
import com.cdancy.jenkins.rest.features.PluginManagerApi;
import com.cdancy.jenkins.rest.features.QueueApi;
import com.cdancy.jenkins.rest.features.StatisticsApi;
import com.cdancy.jenkins.rest.features.SystemApi;
import com.cdancy.jenkins.rest.features.UserApi;

public interface JenkinsApi extends Closeable {

    @Delegate
    CrumbIssuerApi crumbIssuerApi();

    @Delegate
    JobsApi jobsApi();

    @Delegate
    PluginManagerApi pluginManagerApi();

    @Delegate
    QueueApi queueApi();

    @Delegate
    StatisticsApi statisticsApi();

    @Delegate
    SystemApi systemApi();

    @Delegate
    ConfigurationAsCodeApi configurationAsCodeApi();

    @Delegate
    UserApi userApi();
}
