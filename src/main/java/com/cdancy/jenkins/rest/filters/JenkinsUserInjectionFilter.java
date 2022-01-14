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

package com.cdancy.jenkins.rest.filters;

import org.jclouds.http.HttpException;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpRequestFilter;

import com.cdancy.jenkins.rest.JenkinsAuthentication;
import static com.cdancy.jenkins.rest.JenkinsConstants.USER_IN_USER_API;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class JenkinsUserInjectionFilter implements HttpRequestFilter {

    private static final String USER_PLACE_HOLDER = "%7B" + USER_IN_USER_API + "%7D";
    private final JenkinsAuthentication creds;

    @Inject
    public JenkinsUserInjectionFilter(final JenkinsAuthentication creds) {
        this.creds = creds;
    }

    @Override
    public HttpRequest filter(final HttpRequest request) throws HttpException {
        final String requestPath = request.getEndpoint().getRawPath().replaceAll(USER_PLACE_HOLDER, creds.identity);
        return request.toBuilder().fromHttpRequest(request).replacePath(requestPath).build();
    }
}
