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

import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.testng.annotations.Test;

import com.cdancy.jenkins.rest.JenkinsApi;
import com.cdancy.jenkins.rest.domain.statistics.OverallLoad;
import com.cdancy.jenkins.rest.BaseJenkinsMockTest;

/**
 * Mock tests for the {@link com.cdancy.jenkins.rest.features.StatisticsApi}
 * class.
 */
@Test(groups = "unit", testName = "StatisticsApiMockTest")
public class StatisticsApiMockTest extends BaseJenkinsMockTest {

    public void testOverallLoad() throws Exception {
        MockWebServer server = mockWebServer();

        server.enqueue(new MockResponse().setBody(payloadFromResource("/overall-load.json")).setResponseCode(200));
        JenkinsApi jenkinsApi = api(server.url("/").url());
        StatisticsApi api = jenkinsApi.statisticsApi();
        try {
            OverallLoad load = api.overallLoad();
            assertNotNull(load);
            assertSent(server, "GET", "/overallLoad/api/json");
        } finally {
            jenkinsApi.close();
            server.shutdown();
        }
    }
}
