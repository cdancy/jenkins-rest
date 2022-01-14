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

import com.cdancy.jenkins.rest.auth.AuthenticationType;
import com.cdancy.jenkins.rest.config.JenkinsAuthenticationModule;
import com.google.common.collect.Lists;
import com.google.inject.Module;

import java.io.Closeable;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import org.jclouds.ContextBuilder;
import org.jclouds.javax.annotation.Nullable;

public final class JenkinsClient implements Closeable {

    private final String endPoint;
    private final JenkinsAuthentication credentials;
    private final JenkinsApi jenkinsApi;
    private final Properties overrides;

    /**
     * Create a JenkinsClient inferring endpoint and authentication from
     * environment and system properties.
     */
    public JenkinsClient() {
        this(null, null, null, null);
    }

    /**
     * Create an JenkinsClient. If any of the passed in variables are null we
     * will query System Properties and Environment Variables, in order, to
     * search for values that may be set in a devops/CI fashion. The only
     * difference is the `overrides` which gets merged, but takes precedence,
     * with those System Properties and Environment Variables found.
     *
     * @param endPoint URL of Jenkins instance.
     * @param authentication authentication used to connect to Jenkins instance.
     * @param overrides jclouds Properties to override defaults when creating a new JenkinsApi.
     * @param modules a list of modules to be passed to the Contextbuilder, e.g. for logging.
     */
    public JenkinsClient(@Nullable final String endPoint,
            @Nullable final JenkinsAuthentication authentication,
            @Nullable final Properties overrides,
            @Nullable final List<Module> modules) {
        this.endPoint = endPoint != null
                ? endPoint
                : JenkinsUtils.inferEndpoint();
        this.credentials = authentication != null
                ? authentication
                : JenkinsUtils.inferAuthentication();
        this.overrides = mergeOverrides(overrides);
        this.jenkinsApi = createApi(this.endPoint, this.credentials, this.overrides, modules);
    }

    private JenkinsApi createApi(final String endPoint, final JenkinsAuthentication authentication, final Properties overrides, final List<Module> modules) {
        final List<Module> allModules = Lists.newArrayList(new JenkinsAuthenticationModule(authentication));
        if (modules != null) {
            allModules.addAll(modules);
        }
        return ContextBuilder
                .newBuilder(new JenkinsApiMetadata.Builder().build())
                .endpoint(endPoint)
                .modules(allModules)
                .overrides(overrides)
                .buildApi(JenkinsApi.class);
    }

    /**
     * Query System Properties and Environment Variables for overrides and merge
     * the potentially passed in overrides with those.
     *
     * @param possibleOverrides Optional passed in overrides.
     * @return Properties object.
     */
    private Properties mergeOverrides(final Properties possibleOverrides) {
        final Properties inferOverrides = JenkinsUtils.inferOverrides();
        if (possibleOverrides != null) {
            inferOverrides.putAll(possibleOverrides);
        }
        return inferOverrides;
    }

    public String endPoint() {
        return this.endPoint;
    }

    @Deprecated
    public String credentials() {
        return this.authValue();
    }

    public Properties overrides() {
        return this.overrides;
    }

    public String authValue() {
        return this.credentials.authValue();
    }

    public AuthenticationType authType() {
        return this.credentials.authType();
    }

    public JenkinsApi api() {
        return this.jenkinsApi;
    }

    public static Builder builder() {
        return new Builder();
    }

    @Override
    public void close() throws IOException {
        if (this.api() != null) {
            this.api().close();
        }
    }

    public static class Builder {

        private String endPoint;
        private JenkinsAuthentication.Builder authBuilder;
        private Properties overrides;
        private List<Module> modules = Lists.newArrayList();

        /**
         * Define the base endpoint to connect to.
         *
         * @param endPoint Jenkins base endpoint.
         * @return this Builder.
         */
        public Builder endPoint(final String endPoint) {
            this.endPoint = endPoint;
            return this;
        }

        /**
         * Optional credentials to use for authentication. Must take the form of
         * `username:password` or its base64 encoded version.
         *
         * @param optionallyBase64EncodedCredentials authentication credentials.
         * @return this Builder.
         */
        public Builder credentials(final String optionallyBase64EncodedCredentials) {
            authBuilder = JenkinsAuthentication.builder()
                    .credentials(optionallyBase64EncodedCredentials);
            return this;
        }

        /**
         * Optional Api token to use for authentication.
         * This is not a Bearer token, hence the name apiToken.
         *
         * @param apiToken authentication token.
         * @return this Builder.
         */
        public Builder apiToken(final String apiToken) {
            authBuilder = JenkinsAuthentication.builder()
                    .apiToken(apiToken);
            return this;
        }

        /**
         * Optional jclouds Properties to override. What can be overridden can
         * be found here:
         *
         * <p>https://github.com/jclouds/jclouds/blob/master/core/src/main/java/org/jclouds/Constants.java
         *
         * @param overrides optional jclouds Properties to override.
         * @return this Builder.
         */
        public Builder overrides(final Properties overrides) {
            this.overrides = overrides;
            return this;
        }

        /**
         * Optional List of Module to add. Modules can be added, for logging
         * for example.
         *
         * @param modules optional List of Module to add.
         * @return this Builder.
         */
        public Builder modules(final Module... modules) {
            this.modules.addAll(Arrays.asList(modules));
            return this;
        }

        /**
         * Build an instance of JenkinsClient.
         *
         * @return JenkinsClient
         */
        public JenkinsClient build() {

            // 1.) If user passed in some auth use/build that.
            final JenkinsAuthentication authentication = authBuilder != null
                    ? authBuilder.build()
                    : null;

            return new JenkinsClient(endPoint, authentication, overrides, modules);
        }
    }
}
