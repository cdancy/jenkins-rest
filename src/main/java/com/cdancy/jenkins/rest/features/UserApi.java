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
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.jclouds.Fallbacks;
import org.jclouds.rest.annotations.Fallback;
import org.jclouds.rest.annotations.Payload;
import org.jclouds.rest.annotations.PayloadParam;
import org.jclouds.rest.annotations.RequestFilters;

import com.cdancy.jenkins.rest.domain.user.ApiToken;
import com.cdancy.jenkins.rest.domain.user.User;
import com.cdancy.jenkins.rest.domain.common.RequestStatus;
import com.cdancy.jenkins.rest.fallbacks.JenkinsFallbacks;
import com.cdancy.jenkins.rest.filters.JenkinsAuthenticationFilter;
import com.cdancy.jenkins.rest.filters.JenkinsUserInjectionFilter;
import com.cdancy.jenkins.rest.parsers.RequestStatusParser;
import org.jclouds.rest.annotations.ResponseParser;

/**
 * The UserApi.
 *
 * Implements some of the User Rest Api defined in Jenkins.
 * For the User Api, see <a href="https://github.com/jenkinsci/jenkins/blob/master/core/src/main/java/hudson/model/User.java">User.java</a>
 * For the Api Token, see <a href="https://github.com/jenkinsci/jenkins/blob/master/core/src/main/java/jenkins/security/ApiTokenProperty.java">ApiTokenProperty.java</a>.
 */
@RequestFilters({JenkinsAuthenticationFilter.class, JenkinsUserInjectionFilter.class})
@Path("/user")
public interface UserApi {

    @Named("user:get")
    @Path("/{user}/api/json")
    @Fallback(Fallbacks.NullOnNotFoundOr404.class)
    @Consumes(MediaType.APPLICATION_JSON)
    @GET
    User get();

    @Named("user:generateNewToken")
    @Path("/{user}/descriptorByName/jenkins.security.ApiTokenProperty/generateNewToken")
    @Fallback(Fallbacks.NullOnNotFoundOr404.class)
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_FORM_URLENCODED)
    @Payload("newTokenName={tokenName}")
    @POST
    ApiToken generateNewToken(@PayloadParam(value = "tokenName") String tokenName);

    @Named("user:revoke")
    @Path("/{user}/descriptorByName/jenkins.security.ApiTokenProperty/revoke")
    @Fallback(JenkinsFallbacks.RequestStatusOnError.class)
    @ResponseParser(RequestStatusParser.class)
    @Produces(MediaType.APPLICATION_FORM_URLENCODED)
    @Payload("tokenUuid={tokenUuid}")
    @POST
    RequestStatus revoke(@PayloadParam(value = "tokenUuid") String tokenUuid);
}
