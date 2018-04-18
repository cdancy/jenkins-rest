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

package com.cdancy.jenkins.rest.domain.system;

import com.cdancy.jenkins.rest.JenkinsUtils;
import com.cdancy.jenkins.rest.domain.common.ErrorsHolder;
import com.cdancy.jenkins.rest.domain.common.Error;

import org.jclouds.json.SerializedNames;
import org.jclouds.javax.annotation.Nullable;

import com.google.auto.value.AutoValue;

import java.util.List;

@AutoValue
public abstract class SystemInfo implements ErrorsHolder {

    public abstract String hudsonVersion();

    public abstract String jenkinsVersion();

    public abstract String jenkinsSession();

    public abstract String instanceIdentity();

    @Nullable
    public abstract String sshEndpoint();

    public abstract String server();

    SystemInfo() {
    }

    @SerializedNames({ "hudsonVersion", "jenkinsVersion", "jenkinsSession",
        "instanceIdentity", "sshEndpoint", "server", "errors" })
    public static SystemInfo create(String hudsonVersion, String jenkinsVersion, String jenkinsSession,
            String instanceIdentity,
            String sshEndpoint, String server, final List<Error> errors) {
        return new AutoValue_SystemInfo(JenkinsUtils.nullToEmpty(errors),
                hudsonVersion, jenkinsVersion, jenkinsSession, 
                instanceIdentity, sshEndpoint, server);
    }
}
