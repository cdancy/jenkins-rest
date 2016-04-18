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

import org.jclouds.javax.annotation.Nullable;
import org.jclouds.json.SerializedNames;

import com.cdancy.jenkins.rest.domain.queue.QueueItem;
import com.google.auto.value.AutoValue;
import com.google.common.collect.ImmutableList;

@AutoValue
public abstract class JobInfo {

   @Nullable
   public abstract String description();

   @Nullable
   public abstract String displayName();

   @Nullable
   public abstract String displayNameOrNull();

   public abstract String name();

   public abstract String url();

   public abstract boolean buildable();

   public abstract List<Build> builds();

   public abstract String color();

   @Nullable
   public abstract Build firstBuild();

   public abstract boolean inQueue();

   public abstract boolean keepDependencies();

   @Nullable
   public abstract Build lastBuild();

   @Nullable
   public abstract Build lastCompleteBuild();

   @Nullable
   public abstract Build lastFailedBuild();

   @Nullable
   public abstract Build lastStableBuild();

   @Nullable
   public abstract Build lastSuccessfulBuild();

   @Nullable
   public abstract Build lastUnstableBuild();

   @Nullable
   public abstract Build lastUnsuccessfulBuild();

   public abstract int nextBuildNumber();

   @Nullable
   public abstract QueueItem queueItem();

   public abstract boolean concurrentBuild();

   JobInfo() {
   }

   @SerializedNames({ "description", "displayName", "displayNameOrNull", "name", "url", "buildable", "builds", "color",
         "firstBuild", "inQueue", "keepDependencies", "lastBuild", "lastCompleteBuild", "lastFailedBuild",
         "lastStableBuild", "lastSuccessfulBuild", "lastUnstableBuild", "lastUnsuccessfulBuild", "nextBuildNumber",
         "queueItem", "concurrentBuild" })
   public static JobInfo create(String description, String displayName, String displayNameOrNull, String name,
         String url, boolean buildable, List<Build> builds, String color, Build firstBuild, boolean inQueue,
         boolean keepDependencies, Build lastBuild, Build lastCompleteBuild, Build lastFailedBuild,
         Build lastStableBuild, Build lastSuccessfulBuild, Build lastUnstableBuild, Build lastUnsuccessfulBuild,
         int nextBuildNumber, QueueItem queueItem, boolean concurrentBuild) {
      return new AutoValue_JobInfo(description, displayName, displayNameOrNull, name, url, buildable,
            builds != null ? ImmutableList.copyOf(builds) : ImmutableList.<Build> of(), color, firstBuild, inQueue,
            keepDependencies, lastBuild, lastCompleteBuild, lastFailedBuild, lastStableBuild, lastSuccessfulBuild,
            lastUnstableBuild, lastUnsuccessfulBuild, nextBuildNumber, queueItem, concurrentBuild);
   }
}
