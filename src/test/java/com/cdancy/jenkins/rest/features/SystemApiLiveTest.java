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

import org.testng.annotations.Test;

import com.cdancy.jenkins.rest.BaseJenkinsApiLiveTest;
import com.cdancy.jenkins.rest.JenkinsApiMetadata;
import com.cdancy.jenkins.rest.domain.system.Version;

@Test(groups = "live", testName = "SystemApiLiveTest", singleThreaded = true)
public class SystemApiLiveTest extends BaseJenkinsApiLiveTest {

   @Test
   public void testOverallLoad() {
      Version version = api().version();
      assertNotNull(version);
      assertTrue(version.version().equalsIgnoreCase(JenkinsApiMetadata.BUILD_VERSION));
      assertNotNull(version.session());
   }

   private SystemApi api() {
      return api.systemApi();
   }
}
