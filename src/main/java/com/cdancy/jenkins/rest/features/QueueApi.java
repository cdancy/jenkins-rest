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

import java.util.List;

import javax.inject.Named;
import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.MediaType;

import org.jclouds.Fallbacks;
import org.jclouds.rest.annotations.Fallback;
import org.jclouds.rest.annotations.RequestFilters;
import org.jclouds.rest.annotations.SelectJson;

import com.cdancy.jenkins.rest.domain.queue.QueueItem;
import com.cdancy.jenkins.rest.filters.JenkinsAuthenticationFilter;

@RequestFilters(JenkinsAuthenticationFilter.class)
@Consumes(MediaType.APPLICATION_JSON)
public interface QueueApi {

    @Named("queue:queue")
    @SelectJson("items")
    @Path("/queue/api/json")
    @GET
    List<QueueItem> queue();

    /**
     * Get a specific queue item.
     * Queue items are builds that have been scheduled to run, but are waiting for a slot.
     * You can poll the queueItem that corresponds to a build to detect whether the build is still pending or is executing.
     * @param queueId The queue id value as returned by the JobsApi build or buildWithParameters methods.
     * @return The queue item corresponding to the queue id.
     */
    @Named("queue:item")
    @Path("/queue/item/{queueId}/api/json")
    @GET
    QueueItem queueItem(@PathParam("queueId") Integer queueId);

    /**
     * Cancel a queue item before it gets built.
     * @param id The queue id value of the queue item to cancel.
     *           This is the value is returned by the JobsApi build or buildWithParameters methods.
     * @return Always returns true due to JENKINS-21311.
     */
    @Named("queue:cancel")
    @Path("/queue/cancelItem")
    @Fallback(Fallbacks.TrueOnNotFoundOr404.class)
    @POST
    boolean cancel(@FormParam("id") Integer id);

}
