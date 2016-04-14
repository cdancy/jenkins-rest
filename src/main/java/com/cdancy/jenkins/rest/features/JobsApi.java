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
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.jclouds.Fallbacks;
import org.jclouds.rest.annotations.Fallback;
import org.jclouds.rest.annotations.Payload;
import org.jclouds.rest.annotations.PayloadParam;
import org.jclouds.rest.annotations.RequestFilters;

import com.cdancy.jenkins.rest.fallbacks.JenkinsFallbacks;
import com.cdancy.jenkins.rest.filters.JenkinsAuthentication;

@RequestFilters(JenkinsAuthentication.class)
@Consumes(MediaType.APPLICATION_JSON)
@Path("/")
public interface JobsApi {

   @Named("jobs:create")
   @Path("/createItem")
   @Produces(MediaType.APPLICATION_XML)
   @Fallback(JenkinsFallbacks.FalseOn400AndJobAlreadyExists.class)
   @Payload("{configXML}")
   @POST
   boolean create(@QueryParam("name") String jobName, @PayloadParam(value = "configXML") String configXML);

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
   @Consumes(MediaType.WILDCARD)
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
   @POST
   boolean description(@PathParam("name") String jobName, @FormParam("description") String description);

   @Named("jobs:delete")
   @Path("/job/{name}/doDelete")
   @Fallback(Fallbacks.FalseOnNotFoundOr404.class)
   @POST
   boolean delete(@PathParam("name") String jobName);

   @Named("jobs:enable")
   @Path("/job/{name}/enable")
   @POST
   boolean enable(@PathParam("name") String jobName);

   @Named("jobs:disable")
   @Path("/job/{name}/disable")
   @POST
   boolean disable(@PathParam("name") String jobName);
}
