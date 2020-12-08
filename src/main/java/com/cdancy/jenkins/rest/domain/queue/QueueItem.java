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

package com.cdancy.jenkins.rest.domain.queue;

import java.util.Map;

import org.jclouds.json.SerializedNames;
import org.jclouds.javax.annotation.Nullable;

import com.google.auto.value.AutoValue;
import com.google.common.collect.Maps;

@AutoValue
public abstract class QueueItem {

   public abstract boolean blocked();

   public abstract boolean buildable();

   public abstract int id();

   public abstract long inQueueSince();

   public abstract Map<String, String> params();

   public abstract boolean stuck();

   public abstract Task task();

   public abstract String url();

   @Nullable
   public abstract String why();

    // https://javadoc.jenkins.io/hudson/model/Queue.NotWaitingItem.html
    /**
     * When did this job exit the Queue.waitingList phase?
     * For a Queue.NotWaitingItem
     * @return The time expressed in milliseconds after January 1, 1970, 0:00:00 GMT.
     */
   public abstract long buildableStartMilliseconds();

   public abstract boolean cancelled();

   @Nullable
   public abstract Executable executable();

    // https://javadoc.jenkins.io/hudson/model/Queue.WaitingItem.html
    /**
     * This item can be run after this time.
     * For a Queue.WaitingItem
     * @return The time expressed in milliseconds after January 1, 1970, 0:00:00 GMT.
     */
   @Nullable
   public abstract Long timestamp();

   QueueItem() {
   }

   @SerializedNames({ "blocked", "buildable", "id", "inQueueSince", "params", "stuck", "task", "url", "why",
         "buildableStartMilliseconds", "cancelled", "executable", "timestamp"})
   public static QueueItem create(boolean blocked, boolean buildable, int id, long inQueueSince, String params,
         boolean stuck, Task task, String url, String why, long buildableStartMilliseconds,
	 boolean cancelled, Executable executable, Long timestamp) {
      Map<String, String> parameters = Maps.newHashMap();
      if (params != null) {
         params = params.trim();
         if (params.length() > 0) {
            for (String keyValue : params.split("\n")) {
               String[] pair = keyValue.split("=");
               parameters.put(pair[0], pair.length > 1 ? pair[1] : "");
            }
         }
      }
      return new AutoValue_QueueItem(blocked, buildable, id, inQueueSince, parameters, stuck, task, url, why,
            buildableStartMilliseconds, cancelled, executable, timestamp);
   }
}
