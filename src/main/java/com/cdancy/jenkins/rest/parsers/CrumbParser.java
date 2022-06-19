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

package com.cdancy.jenkins.rest.parsers;

import static com.cdancy.jenkins.rest.JenkinsConstants.JENKINS_COOKIES_JSESSIONID;

import com.cdancy.jenkins.rest.domain.crumb.Crumb;

import com.google.common.base.Function;
import java.io.IOException;
import java.util.Collection;

import javax.inject.Singleton;
import javax.ws.rs.core.HttpHeaders;

import org.jclouds.http.HttpResponse;
import org.jclouds.util.Strings2;

/**
 * Turn a valid response, but one that has no body, into a Crumb.
 */
@Singleton
public class CrumbParser implements Function<HttpResponse, Crumb> {

    @Override
    public Crumb apply(final HttpResponse input) {
        if (input == null) {
            throw new RuntimeException("Unexpected NULL HttpResponse object");
        }

        final int statusCode = input.getStatusCode();
        if (statusCode >= 200 && statusCode < 400) {
            try {
                return Crumb.create(crumbValue(input), sessionIdCookie(input));
            } catch (final IOException e) {
                throw new RuntimeException(input.getStatusLine(), e);
            }
        } else {
            throw new RuntimeException(input.getStatusLine());
        }
    }

    private static String crumbValue(HttpResponse input) throws IOException {
        return Strings2.toStringAndClose(input.getPayload().openStream())
                .split(":")[1];
    }

    private static String sessionIdCookie(HttpResponse input) {
        return setCookieValues(input).stream()
            .filter(c -> c.startsWith(JENKINS_COOKIES_JSESSIONID))
            .findFirst()
            .orElse("");
    }

    private static Collection<String> setCookieValues(HttpResponse input) {
        Collection<String> setCookieValues = input.getHeaders().get(HttpHeaders.SET_COOKIE);
        if(setCookieValues.isEmpty()) {
            return input.getHeaders().get(HttpHeaders.SET_COOKIE.toLowerCase());
        } else {
            return setCookieValues;
        }
    }
}
