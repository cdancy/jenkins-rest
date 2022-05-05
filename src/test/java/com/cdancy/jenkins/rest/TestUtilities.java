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

/**
 * Static methods for generating test data.
 */
public class TestUtilities extends JenkinsUtils {

    public static final String TEST_CREDENTIALS_SYSTEM_PROPERTY = "test.jenkins.usernamePassword";
    public static final String TEST_CREDENTIALS_ENVIRONMENT_VARIABLE = TEST_CREDENTIALS_SYSTEM_PROPERTY.replaceAll("\\.", "_").toUpperCase();

    public static final String TEST_API_TOKEN_SYSTEM_PROPERTY = "test.jenkins.usernameApiToken";
    public static final String TEST_API_TOKEN_ENVIRONMENT_VARIABLE = TEST_API_TOKEN_SYSTEM_PROPERTY.replaceAll("\\.", "_").toUpperCase();

    /**
     * Find credentials (ApiToken, UsernamePassword, or Anonymous) from system/environment.
     *
     * @return JenkinsCredentials
     */
    public static JenkinsAuthentication inferTestAuthentication() {

        final JenkinsAuthentication.Builder inferAuth = JenkinsAuthentication.builder();

        // 1.) Check for API Token as this requires no crumb hence is faster
        String authValue = JenkinsUtils
                .retriveExternalValue(TEST_API_TOKEN_SYSTEM_PROPERTY,
                        TEST_API_TOKEN_ENVIRONMENT_VARIABLE);
        if (authValue != null) {
            inferAuth.apiToken(authValue);
            return inferAuth.build();
        }

        // 2.) Check for UsernamePassword auth credentials.
        authValue = JenkinsUtils
                .retriveExternalValue(TEST_CREDENTIALS_SYSTEM_PROPERTY,
                        TEST_CREDENTIALS_ENVIRONMENT_VARIABLE);
        if (authValue != null) {
            inferAuth.credentials(authValue);
            return inferAuth.build();
        }

        // 3.) If neither #1 or #2 find anything "Anonymous" access is assumed.
        return inferAuth.build();
    }

    private TestUtilities() {
        throw new UnsupportedOperationException("Purposefully not implemented");
    }
}
