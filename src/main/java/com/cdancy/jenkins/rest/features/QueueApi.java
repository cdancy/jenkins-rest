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
import org.jclouds.rest.annotations.ResponseParser;
import org.jclouds.rest.annotations.SelectJson;

import com.cdancy.jenkins.rest.fallbacks.JenkinsFallbacks;

import com.cdancy.jenkins.rest.domain.common.RequestStatus;
import com.cdancy.jenkins.rest.domain.queue.QueueItem;
import com.cdancy.jenkins.rest.filters.JenkinsAuthenticationFilter;
import com.cdancy.jenkins.rest.parsers.RequestStatusParser;

@RequestFilters(JenkinsAuthenticationFilter.class)
@Consumes(MediaType.APPLICATION_JSON)
public interface QueueApi {

    @Named("queue:queue")
    @SelectJson("items")
    @Path("/queue/api/json")
    @GET
    List<QueueItem> queue();

    @Named("queue:item")
    @Path("/queue/item/{queueId}/api/json")
    @GET
    QueueItem queueItem(@PathParam("queueId") Integer queueId);

    // TODO: Remove this comment once this method is understood and the test passes
    // curl -v -u martin:martin -H "Jenkins-Crumb:53842b63c40e494a74d261a4773339ef" -X POST "http://localhost:8082/queue/cancelItem?id=210"
    // Trying 127.0.0.1...
    // * Connected to localhost (127.0.0.1) port 8082 (#0)
    // * Server auth using Basic with user 'martin'
    // > POST /queue/cancelItem?id=210 HTTP/1.1
    // > Host: localhost:8082
    // > Authorization: Basic bWFydGluOm1hcnRpbg==
    // > User-Agent: curl/7.47.0
    // > Accept: */*
    // > Jenkins-Crumb:53842b63c40e494a74d261a4773339ef
    // >
    // < HTTP/1.1 302 Found
    // < Date: Wed, 18 Apr 2018 22:01:00 GMT
    // < X-Content-Type-Options: nosniff
    // < Location: http://localhost:8082/queue/
    // < Content-Length: 0
    // < Server: Jetty(9.4.z-SNAPSHOT)
    // <
    // * Connection #0 to host localhost left intact
    @Named("queue:cancel")
    @Path("/queue/cancelItem")
    //@Fallback(Fallbacks.NullOnNotFoundOr404.class)
    //@Fallback(JenkinsFallbacks.RequestStatusOnError.class)
    @ResponseParser(RequestStatusParser.class)
    @POST
    RequestStatus cancel(@FormParam("id") Integer queueId);

}
