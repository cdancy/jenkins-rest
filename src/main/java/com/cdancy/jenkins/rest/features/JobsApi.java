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

import java.io.InputStream;
import com.google.gson.JsonObject;
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

import com.cdancy.jenkins.rest.domain.job.*;
import com.cdancy.jenkins.rest.parsers.*;
import org.jclouds.Fallbacks;
import org.jclouds.javax.annotation.Nullable;
import org.jclouds.rest.annotations.BinderParam;
import org.jclouds.rest.annotations.Fallback;
import org.jclouds.rest.annotations.ParamParser;
import org.jclouds.rest.annotations.Payload;
import org.jclouds.rest.annotations.PayloadParam;
import org.jclouds.rest.annotations.RequestFilters;
import org.jclouds.rest.annotations.ResponseParser;

import com.cdancy.jenkins.rest.binders.BindMapToForm;
import com.cdancy.jenkins.rest.domain.common.IntegerResponse;
import com.cdancy.jenkins.rest.domain.common.RequestStatus;
import com.cdancy.jenkins.rest.fallbacks.JenkinsFallbacks;
import com.cdancy.jenkins.rest.filters.JenkinsAuthenticationFilter;

@RequestFilters(JenkinsAuthenticationFilter.class)
@Path("/")
public interface JobsApi {

    @Named("jobs:get-jobs")
    @Path("{folderPath}api/json")
    @Fallback(Fallbacks.NullOnNotFoundOr404.class)
    @Consumes(MediaType.APPLICATION_JSON)
    @GET
    JobList jobList(@PathParam("folderPath") @ParamParser(FolderPathParser.class) String folderPath);

    @Named("jobs:job-info")
    @Path("{optionalFolderPath}job/{name}/api/json")
    @Fallback(Fallbacks.NullOnNotFoundOr404.class)
    @Consumes(MediaType.APPLICATION_JSON)
    @GET
    JobInfo jobInfo(@Nullable @PathParam("optionalFolderPath") @ParamParser(OptionalFolderPathParser.class) String optionalFolderPath,
                    @PathParam("name") String jobName);

    @Named("jobs:artifact")
    @Path("{optionalFolderPath}job/{name}/{number}/api/json")
    @Fallback(Fallbacks.NullOnNotFoundOr404.class)
    @Consumes(MediaType.APPLICATION_JSON)
    @GET
    BuildInfo buildInfo(@Nullable @PathParam("optionalFolderPath") @ParamParser(OptionalFolderPathParser.class) String optionalFolderPath,
                        @PathParam("name") String jobName,
                        @PathParam("number") int buildNumber);

    @Named("jobs:artifact")
    @Path("{optionalFolderPath}job/{name}/{number}/artifact/{relativeArtifactPath}")
    @Fallback(Fallbacks.NullOnNotFoundOr404.class)
    @Consumes(MediaType.WILDCARD)
    @GET
    InputStream artifact(@Nullable @PathParam("optionalFolderPath") @ParamParser(OptionalFolderPathParser.class) String optionalFolderPath,
                         @PathParam("name") String jobName,
                         @PathParam("number") int buildNumber,
                         @PathParam("relativeArtifactPath") String relativeArtifactPath);

    @Named("jobs:create")
    @Path("{optionalFolderPath}createItem")
    @Fallback(JenkinsFallbacks.RequestStatusOnError.class)
    @ResponseParser(RequestStatusParser.class)
    @Produces(MediaType.APPLICATION_XML)
    @Consumes(MediaType.WILDCARD)
    @Payload("{configXML}")
    @POST
    RequestStatus create(@Nullable @PathParam("optionalFolderPath") @ParamParser(OptionalFolderPathParser.class) String optionalFolderPath,
                         @QueryParam("name") String jobName,
                         @PayloadParam(value = "configXML") String configXML);

    @Named("jobs:get-config")
    @Path("{optionalFolderPath}job/{name}/config.xml")
    @Fallback(Fallbacks.NullOnNotFoundOr404.class)
    @Consumes(MediaType.TEXT_PLAIN)
    @GET
    String config(@Nullable @PathParam("optionalFolderPath") @ParamParser(OptionalFolderPathParser.class) String optionalFolderPath,
                  @PathParam("name") String jobName);

    @Named("jobs:update-config")
    @Path("{optionalFolderPath}job/{name}/config.xml")
    @Fallback(Fallbacks.FalseOnNotFoundOr404.class)
    @Produces(MediaType.APPLICATION_XML + ";charset=UTF-8")
    @Consumes(MediaType.TEXT_HTML)
    @Payload("{configXML}")
    @POST
    boolean config(@Nullable @PathParam("optionalFolderPath") @ParamParser(OptionalFolderPathParser.class) String optionalFolderPath,
                   @PathParam("name") String jobName,
                   @PayloadParam(value = "configXML") String configXML);

    @Named("jobs:get-description")
    @Path("{optionalFolderPath}job/{name}/description")
    @Fallback(Fallbacks.NullOnNotFoundOr404.class)
    @Consumes(MediaType.TEXT_PLAIN)
    @GET
    String description(@Nullable @PathParam("optionalFolderPath") @ParamParser(OptionalFolderPathParser.class) String optionalFolderPath,
                       @PathParam("name") String jobName);

    @Named("jobs:set-description")
    @Path("{optionalFolderPath}job/{name}/description")
    @Fallback(Fallbacks.FalseOnNotFoundOr404.class)
    @Consumes(MediaType.TEXT_HTML)
    @POST
    boolean description(@Nullable @PathParam("optionalFolderPath") @ParamParser(OptionalFolderPathParser.class) String optionalFolderPath,
                        @PathParam("name") String jobName,
                        @FormParam("description") String description);

    @Named("jobs:delete")
    @Path("{optionalFolderPath}job/{name}/doDelete")
    @Consumes(MediaType.TEXT_HTML)
    @Fallback(JenkinsFallbacks.RequestStatusOnError.class)
    @ResponseParser(RequestStatusParser.class)
    @POST
    RequestStatus delete(@Nullable @PathParam("optionalFolderPath") @ParamParser(OptionalFolderPathParser.class) String optionalFolderPath,
                         @PathParam("name") String jobName);

    @Named("jobs:enable")
    @Path("{optionalFolderPath}job/{name}/enable")
    @Fallback(Fallbacks.FalseOnNotFoundOr404.class)
    @Consumes(MediaType.TEXT_HTML)
    @POST
    boolean enable(@Nullable @PathParam("optionalFolderPath") @ParamParser(OptionalFolderPathParser.class) String optionalFolderPath,
                   @PathParam("name") String jobName);

    @Named("jobs:disable")
    @Path("{optionalFolderPath}job/{name}/disable")
    @Fallback(Fallbacks.FalseOnNotFoundOr404.class)
    @Consumes(MediaType.TEXT_HTML)
    @POST
    boolean disable(@Nullable @PathParam("optionalFolderPath") @ParamParser(OptionalFolderPathParser.class) String optionalFolderPath,
                    @PathParam("name") String jobName);

    @Named("jobs:build")
    @Path("{optionalFolderPath}job/{name}/build")
    @Fallback(JenkinsFallbacks.IntegerResponseOnError.class)
    @ResponseParser(LocationToQueueId.class)
    @Consumes("application/unknown")
    @POST
    IntegerResponse build(@Nullable @PathParam("optionalFolderPath") @ParamParser(OptionalFolderPathParser.class) String optionalFolderPath,
                  @PathParam("name") String jobName);

    @Named("jobs:stop-build")
    @Path("{optionalFolderPath}job/{name}/{number}/stop")
    @Fallback(JenkinsFallbacks.RequestStatusOnError.class)
    @ResponseParser(RequestStatusParser.class)
    @Consumes(MediaType.APPLICATION_JSON)
    @POST
    RequestStatus stop(@Nullable @PathParam("optionalFolderPath") @ParamParser(OptionalFolderPathParser.class) String optionalFolderPath,
                            @PathParam("name") String jobName,
                            @PathParam("number") int buildNumber);

    @Named("jobs:term-build")
    @Path("{optionalFolderPath}job/{name}/{number}/term")
    @Fallback(JenkinsFallbacks.RequestStatusOnError.class)
    @ResponseParser(RequestStatusParser.class)
    @Consumes(MediaType.APPLICATION_JSON)
    @POST
    RequestStatus term(@Nullable @PathParam("optionalFolderPath") @ParamParser(OptionalFolderPathParser.class) String optionalFolderPath,
                            @PathParam("name") String jobName,
                            @PathParam("number") int buildNumber);

    @Named("jobs:kill-build")
    @Path("{optionalFolderPath}job/{name}/{number}/kill")
    @Fallback(JenkinsFallbacks.RequestStatusOnError.class)
    @ResponseParser(RequestStatusParser.class)
    @Consumes(MediaType.APPLICATION_JSON)
    @POST
    RequestStatus kill(@Nullable @PathParam("optionalFolderPath") @ParamParser(OptionalFolderPathParser.class) String optionalFolderPath,
                            @PathParam("name") String jobName,
                            @PathParam("number") int buildNumber);

    @Named("jobs:build-with-params")
    @Path("{optionalFolderPath}job/{name}/buildWithParameters")
    @Fallback(JenkinsFallbacks.IntegerResponseOnError.class)
    @ResponseParser(LocationToQueueId.class)
    @Consumes("application/unknown")
    @POST
    IntegerResponse buildWithParameters(@Nullable @PathParam("optionalFolderPath") @ParamParser(OptionalFolderPathParser.class) String optionalFolderPath,
                                @PathParam("name") String jobName,
                                @Nullable @BinderParam(BindMapToForm.class) Map<String, List<String>> properties);

    @Named("jobs:last-build-number")
    @Path("{optionalFolderPath}job/{name}/lastBuild/buildNumber")
    @Fallback(Fallbacks.NullOnNotFoundOr404.class)
    @ResponseParser(BuildNumberToInteger.class)
    @Consumes(MediaType.TEXT_PLAIN)
    @GET
    Integer lastBuildNumber(@Nullable @PathParam("optionalFolderPath") @ParamParser(OptionalFolderPathParser.class) String optionalFolderPath,
                            @PathParam("name") String jobName);

    @Named("jobs:last-build-timestamp")
    @Path("{optionalFolderPath}job/{name}/lastBuild/buildTimestamp")
    @Fallback(Fallbacks.NullOnNotFoundOr404.class)
    @Consumes(MediaType.TEXT_PLAIN)
    @GET
    String lastBuildTimestamp(@Nullable @PathParam("optionalFolderPath") @ParamParser(OptionalFolderPathParser.class) String optionalFolderPath,
                              @PathParam("name") String jobName);

    @Named("jobs:progressive-text")
    @Path("{optionalFolderPath}job/{name}/lastBuild/logText/progressiveText")
    @Fallback(Fallbacks.NullOnNotFoundOr404.class)
    @ResponseParser(OutputToProgressiveText.class)
    @Consumes(MediaType.TEXT_PLAIN)
    @GET
    ProgressiveText progressiveText(@Nullable @PathParam("optionalFolderPath") @ParamParser(OptionalFolderPathParser.class) String optionalFolderPath,
                                    @PathParam("name") String jobName,
                                    @QueryParam("start") int start);

    @Named("jobs:progressive-text")
    @Path("{optionalFolderPath}job/{name}/{number}/logText/progressiveText")
    @Fallback(Fallbacks.NullOnNotFoundOr404.class)
    @ResponseParser(OutputToProgressiveText.class)
    @Consumes(MediaType.TEXT_PLAIN)
    @GET
    ProgressiveText progressiveText(@Nullable @PathParam("optionalFolderPath") @ParamParser(OptionalFolderPathParser.class) String optionalFolderPath,
                                    @PathParam("name") String jobName,
                                    @PathParam("number") int buildNumber,
                                    @QueryParam("start") int start);

    @Named("jobs:rename")
    @Path("{optionalFolderPath}job/{name}/doRename")
    @Fallback(Fallbacks.FalseOnNotFoundOr404.class)
    @Consumes(MediaType.TEXT_HTML)
    @POST
    boolean rename(@Nullable @PathParam("optionalFolderPath") @ParamParser(OptionalFolderPathParser.class) String optionalFolderPath,
                   @PathParam("name") String jobName,
                   @QueryParam("newName") String newName);

    // below four apis are for "pipeline-stage-view-plugin",
    // see https://github.com/jenkinsci/pipeline-stage-view-plugin/tree/master/rest-api
    @Named("jobs:run-history")
    @Path("{optionalFolderPath}job/{name}/wfapi/runs")
    @Fallback(Fallbacks.NullOnNotFoundOr404.class)
    @Consumes(MediaType.APPLICATION_JSON)
    @GET
    List<Workflow> runHistory(@Nullable @PathParam("optionalFolderPath") @ParamParser(OptionalFolderPathParser.class) String optionalFolderPath,
                              @PathParam("name") String jobName);

    @Named("jobs:workflow")
    @Path("{optionalFolderPath}job/{name}/{number}/wfapi/describe")
    @Fallback(Fallbacks.NullOnNotFoundOr404.class)
    @Consumes(MediaType.APPLICATION_JSON)
    @GET
    Workflow workflow(@Nullable @PathParam("optionalFolderPath") @ParamParser(OptionalFolderPathParser.class) String optionalFolderPath,
        @PathParam("name") String jobName,
        @PathParam("number") int buildNumber);

    @Named("jobs:pipeline-node")
    @Path("{optionalFolderPath}job/{name}/{number}/execution/node/{nodeId}/wfapi/describe")
    @Fallback(Fallbacks.NullOnNotFoundOr404.class)
    @Consumes(MediaType.APPLICATION_JSON)
    @GET
    PipelineNode pipelineNode(@Nullable @PathParam("optionalFolderPath") @ParamParser(OptionalFolderPathParser.class) String optionalFolderPath,
        @PathParam("name") String jobName,
        @PathParam("number") int buildNumber, @PathParam("nodeId") int nodeId);

    @Named("jobs:pipeline-node-log")
    @Path("{optionalFolderPath}job/{name}/{number}/execution/node/{nodeId}/wfapi/log")
    @Fallback(Fallbacks.NullOnNotFoundOr404.class)
    @Consumes(MediaType.APPLICATION_JSON)
    @GET
    PipelineNodeLog pipelineNodeLog(@Nullable @PathParam("optionalFolderPath") @ParamParser(OptionalFolderPathParser.class) String optionalFolderPath,
                              @PathParam("name") String jobName,
                              @PathParam("number") int buildNumber, @PathParam("nodeId") int nodeId);

    @Named("jobs:testReport")
    @Path("{optionalFolderPath}job/{name}/{number}/testReport/api/json")
    @Fallback(Fallbacks.NullOnNotFoundOr404.class)
    @Consumes(MediaType.APPLICATION_JSON)
    @GET
    JsonObject testReport(@Nullable @PathParam("optionalFolderPath") @ParamParser(OptionalFolderPathParser.class) String optionalFolderPath,
        @PathParam("name") String jobName,
        @PathParam("number") int buildNumber);

}
