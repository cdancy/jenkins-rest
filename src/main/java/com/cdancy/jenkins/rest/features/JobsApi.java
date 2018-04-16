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
import java.util.Map;

import javax.inject.Named;
import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.jclouds.Fallbacks;
import org.jclouds.rest.annotations.BinderParam;
import org.jclouds.rest.annotations.Fallback;
import org.jclouds.rest.annotations.Payload;
import org.jclouds.rest.annotations.PayloadParam;
import org.jclouds.rest.annotations.RequestFilters;
import org.jclouds.rest.annotations.ResponseParser;

import com.cdancy.jenkins.rest.binders.BindMapToForm;
import com.cdancy.jenkins.rest.domain.common.RequestStatus;
import com.cdancy.jenkins.rest.domain.job.BuildInfo;
import com.cdancy.jenkins.rest.domain.job.JobInfo;
import com.cdancy.jenkins.rest.domain.job.ProgressiveText;
import com.cdancy.jenkins.rest.fallbacks.JenkinsFallbacks;
import com.cdancy.jenkins.rest.filters.JenkinsAuthenticationFilter;
import com.cdancy.jenkins.rest.parsers.BuildNumberToInteger;
import com.cdancy.jenkins.rest.parsers.LocationToQueueId;
import com.cdancy.jenkins.rest.parsers.OutputToProgressiveText;
import com.cdancy.jenkins.rest.parsers.RequestStatusParser;

@RequestFilters(JenkinsAuthenticationFilter.class)
@Path("/")
public interface JobsApi {

    @Named("jobs:job-info")
    @Path("/job/{name}/api/json")
    @Fallback(Fallbacks.NullOnNotFoundOr404.class)
    @Consumes(MediaType.APPLICATION_JSON)
    @GET
    JobInfo jobInfo(@PathParam("name") String jobName);

    @Named("jobs:build-info")
    @Path("/job/{name}/{number}/api/json")
    @Fallback(Fallbacks.NullOnNotFoundOr404.class)
    @Consumes(MediaType.APPLICATION_JSON)
    @GET
    BuildInfo buildInfo(@PathParam("name") String jobName, @PathParam("number") int buildNumber);

    @Named("jobs:create")
    @Path("/createItem")
    @Fallback(JenkinsFallbacks.RequestStatusOnError.class)
    @ResponseParser(RequestStatusParser.class)
    @Produces(MediaType.APPLICATION_XML)
    @Consumes(MediaType.WILDCARD)
    @Payload("{configXML}")
    @POST
    RequestStatus create(@QueryParam("name") String jobName, @PayloadParam(value = "configXML") String configXML);

    @Named("jobs:get-config")
    @Path("/job/{name}/config.xml")
    @Fallback(Fallbacks.NullOnNotFoundOr404.class)
    @Consumes(MediaType.TEXT_PLAIN)
    @GET
    String config(@PathParam("name") String jobName);

    @Named("jobs:update-config")
    @Path("/job/{name}/config.xml")
    @Fallback(Fallbacks.FalseOnNotFoundOr404.class)
    @Produces(MediaType.APPLICATION_XML)
    @Consumes(MediaType.TEXT_HTML)
    @Payload("{configXML}")
    @POST
    boolean config(@PathParam("name") String jobName, @PayloadParam(value = "configXML") String configXML);

    @Named("jobs:get-description")
    @Path("/job/{name}/description")
    @Fallback(Fallbacks.NullOnNotFoundOr404.class)
    @Consumes(MediaType.TEXT_PLAIN)
    @GET
    String description(@PathParam("name") String jobName);

    @Named("jobs:set-description")
    @Path("/job/{name}/description")
    @Fallback(Fallbacks.FalseOnNotFoundOr404.class)
    @Consumes(MediaType.TEXT_HTML)
    @POST
    boolean description(@PathParam("name") String jobName, @FormParam("description") String description);

    @Named("jobs:delete")
    @Path("/job/{name}/doDelete")
    @Consumes(MediaType.TEXT_HTML)
    @Fallback(JenkinsFallbacks.RequestStatusOnError.class)
    @ResponseParser(RequestStatusParser.class)
    @POST
    RequestStatus delete(@PathParam("name") String jobName);

    @Named("jobs:enable")
    @Path("/job/{name}/enable")
    @Fallback(Fallbacks.FalseOnNotFoundOr404.class)
    @Consumes(MediaType.TEXT_HTML)
    @POST
    boolean enable(@PathParam("name") String jobName);

    @Named("jobs:disable")
    @Path("/job/{name}/disable")
    @Fallback(Fallbacks.FalseOnNotFoundOr404.class)
    @Consumes(MediaType.TEXT_HTML)
    @POST
    boolean disable(@PathParam("name") String jobName);

    /**
     * Queue's a build for a given job
     *
     * @param jobName
     * @return queue id to reference build
      */
    @Named("jobs:build")
    @Path("/job/{name}/build")
    @Fallback(Fallbacks.NullOnNotFoundOr404.class)
    @ResponseParser(LocationToQueueId.class)
    @Consumes("application/unknown")
    @POST
    Integer build(@PathParam("name") String jobName);

    /**
     * Queue's a build with parameters for a given job
     *
     * @param jobName
     * @param properties
     * @return queue id to reference build
      */
    @Named("jobs:build-with-params")
    @Path("/job/{name}/buildWithParameters")
    @Fallback(Fallbacks.NullOnNotFoundOr404.class)
    @ResponseParser(LocationToQueueId.class)
    @Consumes("application/unknown")
    @POST
    Integer buildWithParameters(@PathParam("name") String jobName,
          @BinderParam(BindMapToForm.class) Map<String, List<String>> properties);

    @Named("jobs:last-build-number")
    @Path("/job/{name}/lastBuild/buildNumber")
    @Fallback(Fallbacks.NullOnNotFoundOr404.class)
    @ResponseParser(BuildNumberToInteger.class)
    @Consumes(MediaType.TEXT_PLAIN)
    @GET
    Integer lastBuildNumber(@PathParam("name") String jobName);

    @Named("jobs:last-build-timestamp")
    @Path("/job/{name}/lastBuild/buildTimestamp")
    @Fallback(Fallbacks.NullOnNotFoundOr404.class)
    @Consumes(MediaType.TEXT_PLAIN)
    @GET
    String lastBuildTimestamp(@PathParam("name") String jobName);

    @Named("jobs:progressive-text")
    @Path("/job/{name}/lastBuild/logText/progressiveText")
    @Fallback(Fallbacks.NullOnNotFoundOr404.class)
    @ResponseParser(OutputToProgressiveText.class)
    @Consumes(MediaType.TEXT_PLAIN)
    @GET
    ProgressiveText progressiveText(@PathParam("name") String jobName, @QueryParam("start") int start);
}
