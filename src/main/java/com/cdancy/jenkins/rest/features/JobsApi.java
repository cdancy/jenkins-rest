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

import static com.cdancy.jenkins.rest.JenkinsConstants.OPTIONAL_FOLDER_PATH_PARAM;
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
import org.jclouds.javax.annotation.Nullable;
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
import com.cdancy.jenkins.rest.filters.ScrubNullFolderParam;
import com.cdancy.jenkins.rest.parsers.BuildNumberToInteger;
import com.cdancy.jenkins.rest.parsers.LocationToQueueId;
import com.cdancy.jenkins.rest.parsers.OutputToProgressiveText;
import com.cdancy.jenkins.rest.parsers.RequestStatusParser;

@RequestFilters(JenkinsAuthenticationFilter.class)
@Path("/")
public interface JobsApi {

    @Named("jobs:job-info")
    @Path("{" + OPTIONAL_FOLDER_PATH_PARAM + "}/job/{name}/api/json")
    @RequestFilters(ScrubNullFolderParam.class)
    @Fallback(Fallbacks.NullOnNotFoundOr404.class)
    @Consumes(MediaType.APPLICATION_JSON)
    @GET
    JobInfo jobInfo(@Nullable @PathParam(OPTIONAL_FOLDER_PATH_PARAM) String optionalFolderPath,
                    @PathParam("name") String jobName);

    @Named("jobs:build-info")
    @Path("{" + OPTIONAL_FOLDER_PATH_PARAM + "}/job/{name}/{number}/api/json")
    @RequestFilters(ScrubNullFolderParam.class)
    @Fallback(Fallbacks.NullOnNotFoundOr404.class)
    @Consumes(MediaType.APPLICATION_JSON)
    @GET
    BuildInfo buildInfo(@Nullable @PathParam(OPTIONAL_FOLDER_PATH_PARAM) String optionalFolderPath,
                        @PathParam("name") String jobName,
                        @PathParam("number") int buildNumber);

    @Named("jobs:create")
    @Path("{" + OPTIONAL_FOLDER_PATH_PARAM + "}/createItem")
    @Fallback(JenkinsFallbacks.RequestStatusOnError.class)
    @RequestFilters(ScrubNullFolderParam.class)
    @ResponseParser(RequestStatusParser.class)
    @Produces(MediaType.APPLICATION_XML)
    @Consumes(MediaType.WILDCARD)
    @Payload("{configXML}")
    @POST
    RequestStatus create(@Nullable @PathParam(OPTIONAL_FOLDER_PATH_PARAM) String optionalFolderPath,
                         @QueryParam("name") String jobName,
                         @PayloadParam(value = "configXML") String configXML);

    @Named("jobs:get-config")
    @Path("{" + OPTIONAL_FOLDER_PATH_PARAM + "}/job/{name}/config.xml")
    @RequestFilters(ScrubNullFolderParam.class)
    @Fallback(Fallbacks.NullOnNotFoundOr404.class)
    @Consumes(MediaType.TEXT_PLAIN)
    @GET
    String config(@Nullable @PathParam(OPTIONAL_FOLDER_PATH_PARAM) String optionalFolderPath,
                  @PathParam("name") String jobName);

    @Named("jobs:update-config")
    @Path("{" + OPTIONAL_FOLDER_PATH_PARAM + "}/job/{name}/config.xml")
    @RequestFilters(ScrubNullFolderParam.class)
    @Fallback(Fallbacks.FalseOnNotFoundOr404.class)
    @Produces(MediaType.APPLICATION_XML)
    @Consumes(MediaType.TEXT_HTML)
    @Payload("{configXML}")
    @POST
    boolean config(@Nullable @PathParam(OPTIONAL_FOLDER_PATH_PARAM) String optionalFolderPath,
                   @PathParam("name") String jobName,
                   @PayloadParam(value = "configXML") String configXML);

    @Named("jobs:get-description")
    @Path("{" + OPTIONAL_FOLDER_PATH_PARAM + "}/job/{name}/description")
    @RequestFilters(ScrubNullFolderParam.class)
    @Fallback(Fallbacks.NullOnNotFoundOr404.class)
    @Consumes(MediaType.TEXT_PLAIN)
    @GET
    String description(@Nullable @PathParam(OPTIONAL_FOLDER_PATH_PARAM) String optionalFolderPath,
                       @PathParam("name") String jobName);

    @Named("jobs:set-description")
    @Path("{" + OPTIONAL_FOLDER_PATH_PARAM + "}/job/{name}/description")
    @RequestFilters(ScrubNullFolderParam.class)
    @Fallback(Fallbacks.FalseOnNotFoundOr404.class)
    @Consumes(MediaType.TEXT_HTML)
    @POST
    boolean description(@Nullable @PathParam(OPTIONAL_FOLDER_PATH_PARAM) String optionalFolderPath,
                        @PathParam("name") String jobName,
                        @FormParam("description") String description);

    @Named("jobs:delete")
    @Path("{" + OPTIONAL_FOLDER_PATH_PARAM + "}/job/{name}/doDelete")
    @RequestFilters(ScrubNullFolderParam.class)
    @Consumes(MediaType.TEXT_HTML)
    @Fallback(JenkinsFallbacks.RequestStatusOnError.class)
    @ResponseParser(RequestStatusParser.class)
    @POST
    RequestStatus delete(@Nullable @PathParam(OPTIONAL_FOLDER_PATH_PARAM) String optionalFolderPath,
                         @PathParam("name") String jobName);

    @Named("jobs:enable")
    @Path("{" + OPTIONAL_FOLDER_PATH_PARAM + "}/job/{name}/enable")
    @RequestFilters(ScrubNullFolderParam.class)
    @Fallback(Fallbacks.FalseOnNotFoundOr404.class)
    @Consumes(MediaType.TEXT_HTML)
    @POST
    boolean enable(@Nullable @PathParam(OPTIONAL_FOLDER_PATH_PARAM) String optionalFolderPath,
                   @PathParam("name") String jobName);

    @Named("jobs:disable")
    @Path("{" + OPTIONAL_FOLDER_PATH_PARAM + "}/job/{name}/disable")
    @RequestFilters(ScrubNullFolderParam.class)
    @Fallback(Fallbacks.FalseOnNotFoundOr404.class)
    @Consumes(MediaType.TEXT_HTML)
    @POST
    boolean disable(@Nullable @PathParam(OPTIONAL_FOLDER_PATH_PARAM) String optionalFolderPath,
                    @PathParam("name") String jobName);

    @Named("jobs:build")
    @Path("{" + OPTIONAL_FOLDER_PATH_PARAM + "}/job/{name}/build")
    @RequestFilters(ScrubNullFolderParam.class)
    @Fallback(Fallbacks.NullOnNotFoundOr404.class)
    @ResponseParser(LocationToQueueId.class)
    @Consumes("application/unknown")
    @POST
    Integer build(@Nullable @PathParam(OPTIONAL_FOLDER_PATH_PARAM) String optionalFolderPath,
                  @PathParam("name") String jobName);

    @Named("jobs:build-with-params")
    @Path("{" + OPTIONAL_FOLDER_PATH_PARAM + "}/job/{name}/buildWithParameters")
    @RequestFilters(ScrubNullFolderParam.class)
    @Fallback(Fallbacks.NullOnNotFoundOr404.class)
    @ResponseParser(LocationToQueueId.class)
    @Consumes("application/unknown")
    @POST
    Integer buildWithParameters(@Nullable @PathParam(OPTIONAL_FOLDER_PATH_PARAM) String optionalFolderPath,
                                @PathParam("name") String jobName,
                                @BinderParam(BindMapToForm.class) Map<String, List<String>> properties);

    @Named("jobs:last-build-number")
    @Path("{" + OPTIONAL_FOLDER_PATH_PARAM + "}/job/{name}/lastBuild/buildNumber")
    @RequestFilters(ScrubNullFolderParam.class)
    @Fallback(Fallbacks.NullOnNotFoundOr404.class)
    @ResponseParser(BuildNumberToInteger.class)
    @Consumes(MediaType.TEXT_PLAIN)
    @GET
    Integer lastBuildNumber(@Nullable @PathParam(OPTIONAL_FOLDER_PATH_PARAM) String optionalFolderPath,
                            @PathParam("name") String jobName);

    @Named("jobs:last-build-timestamp")
    @Path("{" + OPTIONAL_FOLDER_PATH_PARAM + "}/job/{name}/lastBuild/buildTimestamp")
    @RequestFilters(ScrubNullFolderParam.class)
    @Fallback(Fallbacks.NullOnNotFoundOr404.class)
    @Consumes(MediaType.TEXT_PLAIN)
    @GET
    String lastBuildTimestamp(@Nullable @PathParam(OPTIONAL_FOLDER_PATH_PARAM) String optionalFolderPath,
                              @PathParam("name") String jobName);

    @Named("jobs:progressive-text")
    @Path("{" + OPTIONAL_FOLDER_PATH_PARAM + "}/job/{name}/lastBuild/logText/progressiveText")
    @RequestFilters(ScrubNullFolderParam.class)
    @Fallback(Fallbacks.NullOnNotFoundOr404.class)
    @ResponseParser(OutputToProgressiveText.class)
    @Consumes(MediaType.TEXT_PLAIN)
    @GET
    ProgressiveText progressiveText(@Nullable @PathParam(OPTIONAL_FOLDER_PATH_PARAM) String optionalFolderPath,
                                    @PathParam("name") String jobName,
                                    @QueryParam("start") int start);
}
