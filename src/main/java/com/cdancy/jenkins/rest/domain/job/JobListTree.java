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

package com.cdancy.jenkins.rest.domain.job;

import java.util.List;

import com.google.auto.value.AutoValue;
import org.jclouds.javax.annotation.Nullable;
import org.jclouds.json.SerializedNames;

@AutoValue
public abstract class JobListTree {

    @Nullable
    public abstract String clazz();

    @Nullable
    public abstract String name();

    @Nullable
    public abstract String fullName();

    @Nullable
    public abstract List<JobListTree> jobs();

    @Nullable
    public abstract String color();

    @Nullable
    public abstract String url();

    @SerializedNames({"_class", "name", "fullName", "jobs", "color", "url"})
    public static JobListTree create(
      @Nullable String clazz,
      @Nullable String name,
      @Nullable String fullName,
      @Nullable List<JobListTree> jobs,
      @Nullable String color,
      @Nullable String url
    ) {
        return new AutoValue_JobListTree(clazz, name, fullName, jobs, color, url);
    }
}
