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

import java.net.URI;
import java.util.Properties;

import org.jclouds.apis.ApiMetadata;
import org.jclouds.rest.internal.BaseHttpApiMetadata;

import com.cdancy.jenkins.rest.config.JenkinsHttpApiModule;
import com.google.auto.service.AutoService;
import com.google.common.collect.ImmutableSet;
import com.google.inject.Module;

@AutoService(ApiMetadata.class)
public class JenkinsApiMetadata extends BaseHttpApiMetadata<JenkinsApi> {

    public static final String API_VERSION = "1.0";
    public static final String BUILD_VERSION = "2.0";

    @Override
    public Builder toBuilder() {
        return new Builder().fromApiMetadata(this);
    }

    public JenkinsApiMetadata() {
        this(new Builder());
    }

    protected JenkinsApiMetadata(Builder builder) {
        super(builder);
    }

    public static Properties defaultProperties() {
        return BaseHttpApiMetadata.defaultProperties();
    }

    public static class Builder extends BaseHttpApiMetadata.Builder<JenkinsApi, Builder> {

        protected Builder() {
           super(JenkinsApi.class);
           id("jenkins").name("Jenkins API").identityName("Optional Username").credentialName("Optional Password")
                .defaultIdentity("").defaultCredential("")
                .documentation(URI.create("http://wiki.jenkins-ci.org/display/JENKINS/Remote+access+API"))
                .version(API_VERSION).buildVersion(BUILD_VERSION).defaultEndpoint("http://127.0.0.1:8080")
                .defaultProperties(JenkinsApiMetadata.defaultProperties())
                .defaultModules(ImmutableSet.of(JenkinsHttpApiModule.class));
        }

        @Override
        public JenkinsApiMetadata build() {
            return new JenkinsApiMetadata(this);
        }

        @Override
        protected Builder self() {
            return this;
        }

        @Override
        public Builder fromApiMetadata(ApiMetadata in) {
            return this;
        }
    }
}
