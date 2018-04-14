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

import org.jclouds.json.SerializedNames;

import com.google.auto.value.AutoValue;
import org.jclouds.javax.annotation.Nullable;

@AutoValue
public abstract class SystemInfo {

    public abstract String hudsonVersion();

    public abstract String jenkinsVersion();

    public abstract String jenkinsSession();

    public abstract String hudsonCLIPort();

    public abstract String jenkinsCLIPort();

    public abstract String jenkinsCLI2Port();

    public abstract String instanceIdentity();

    @Nullable
    public abstract String sshEndpoint();

    public abstract String server();

    SystemInfo() {
    }

    @SerializedNames({ "hudsonVersion", "jenkinsVersion", "jenkinsSession",
        "hudsonCLIPort", "jenkinsCLIPort", "jenkinsCLI2Port",
        "instanceIdentity", "sshEndpoint", "server" })
    public static SystemInfo create(String hudsonVersion, String jenkinsVersion, String jenkinsSession,
            String hudsonCLIPort, String jenkinsCLIPort, String jenkinsCLI2Port, String instanceIdentity,
            String sshEndpoint, String server) {
        return new AutoValue_SystemInfo(hudsonVersion, jenkinsVersion, jenkinsSession, 
                hudsonCLIPort, jenkinsCLIPort, jenkinsCLI2Port,
                instanceIdentity, sshEndpoint, server);
    }
}
