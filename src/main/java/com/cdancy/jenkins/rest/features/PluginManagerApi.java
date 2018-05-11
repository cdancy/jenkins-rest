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

import javax.inject.Named;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.QueryParam;
import javax.ws.rs.POST;
import javax.ws.rs.Produces;

import com.cdancy.jenkins.rest.domain.common.RequestStatus;
import com.cdancy.jenkins.rest.domain.plugins.Plugins;
import com.cdancy.jenkins.rest.fallbacks.JenkinsFallbacks;
import com.cdancy.jenkins.rest.filters.JenkinsAuthenticationFilter;
import com.cdancy.jenkins.rest.parsers.RequestStatusParser;

import org.jclouds.rest.annotations.RequestFilters;
import org.jclouds.javax.annotation.Nullable;
import org.jclouds.rest.annotations.Fallback;
import org.jclouds.rest.annotations.Payload;
import org.jclouds.rest.annotations.PayloadParam;
import org.jclouds.rest.annotations.ResponseParser;

@RequestFilters(JenkinsAuthenticationFilter.class)
@Consumes(MediaType.APPLICATION_JSON)
@Path("/pluginManager")
public interface PluginManagerApi {

    @Named("pluginManager:plugins")
    @Path("/api/json")
    @Fallback(JenkinsFallbacks.PluginsOnError.class)
    @GET
    Plugins plugins(@Nullable @QueryParam("depth") Integer depth,
            @Nullable @QueryParam("tree") String tree);

    @Named("pluginManager:install-necessary-plugins")
    @Path("/installNecessaryPlugins")
    @Fallback(JenkinsFallbacks.RequestStatusOnError.class)
    @ResponseParser(RequestStatusParser.class)
    @Produces(MediaType.APPLICATION_XML)
    @Payload("<jenkins><install plugin=\"{pluginID}\"/></jenkins>")
    @POST
    RequestStatus installNecessaryPlugins(@PayloadParam(value = "pluginID") String pluginID);
}
