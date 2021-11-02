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

import java.util.Optional;
import javax.inject.Inject;
import javax.inject.Singleton;

import com.cdancy.jenkins.rest.JenkinsApi;
import com.cdancy.jenkins.rest.JenkinsAuthentication;
import com.cdancy.jenkins.rest.auth.AuthenticationType;
import com.cdancy.jenkins.rest.domain.crumb.Crumb;

import org.jclouds.http.HttpException;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpRequestFilter;

import com.google.common.net.HttpHeaders;

import org.jclouds.rest.ResourceNotFoundException;

@Singleton
public class JenkinsAuthenticationFilter implements HttpRequestFilter {
    private final JenkinsAuthentication creds;
    private final JenkinsApi jenkinsApi;

    // key = Crumb, value = true if exception is ResourceNotFoundException false otherwise
    private volatile Pair<Crumb, Boolean> crumbPair = null;
    private static final String CRUMB_HEADER = "Jenkins-Crumb";

    @Inject
    JenkinsAuthenticationFilter(final JenkinsAuthentication creds, final JenkinsApi jenkinsApi) {
        this.creds = creds;
        this.jenkinsApi = jenkinsApi;
    }

    @Override
    public HttpRequest filter(final HttpRequest request) throws HttpException {
        final HttpRequest.Builder<? extends HttpRequest.Builder<?>> builder = request.toBuilder();

        // Password and API Token are both Basic authentication
        if (creds.authType() == AuthenticationType.ApiToken || creds.authType() == AuthenticationType.UsernamePassword) {
            final String authHeader = creds.authType().getAuthScheme() + " " + creds.authValue();
            builder.addHeader(HttpHeaders.AUTHORIZATION, authHeader);
        }

        // Anon and Password need the crumb
        if (creds.authType() == AuthenticationType.UsernamePassword || creds.authType() == AuthenticationType.Anonymous) {
            final Pair<Crumb, Boolean> localCrumb = getCrumb();
            if (localCrumb.getKey().value() != null) {
                builder.addHeader(CRUMB_HEADER, localCrumb.getKey().value());
                Optional.ofNullable(localCrumb.getKey().sessionIdCookie())
                        .ifPresent(sessionId -> builder.addHeader(HttpHeaders.COOKIE, sessionId));
            } else {
                if (localCrumb.getValue() == false) {
                    throw new RuntimeException("Unexpected exception being thrown: error=" + localCrumb.getKey().errors().get(0));
                }
            }
        }
        return builder.build();
    }

    private Pair<Crumb, Boolean> getCrumb() {
        Pair<Crumb, Boolean> crumbValueInit = this.crumbPair;
        if (crumbValueInit == null) {
            synchronized(this) {
                crumbValueInit = this.crumbPair;
                if (crumbValueInit == null) {
                    final Crumb crumb = jenkinsApi.crumbIssuerApi().crumb();
                    final Boolean isRNFE = crumb.errors().isEmpty()
                            ? true
                            : crumb.errors().get(0).exceptionName().endsWith(ResourceNotFoundException.class.getSimpleName());
                    this.crumbPair = crumbValueInit = new Pair(crumb, isRNFE);
                }
            }
        }
        return crumbValueInit;
    }

    // simple impl/copy of javafx.util.Pair
    private class Pair<A, B> {
        private final A a;
        private final B b;
        public Pair(final A a, final B b) {
            this.a = a;
            this.b = b;
        }
        public A getKey() {
            return a;
        }
        public B getValue() {
            return b;
        }
    }
}
