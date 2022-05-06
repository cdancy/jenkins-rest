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

import javax.inject.Singleton;

import org.jclouds.http.HttpResponse;

import com.cdancy.jenkins.rest.domain.system.SystemInfo;

import com.google.common.base.Function;

/**
 * Created by dancc on 3/11/16.
 */
@Singleton
public class SystemInfoFromJenkinsHeaders implements Function<HttpResponse, SystemInfo> {

    @Override
    public SystemInfo apply(HttpResponse response) {
        if (response == null) {
            throw new RuntimeException("Unexpected NULL HttpResponse object");
        }

        final int statusCode = response.getStatusCode();
        if (statusCode >= 200 && statusCode < 400) {
            return SystemInfo.create(response.getFirstHeaderOrNull("X-Hudson"), response.getFirstHeaderOrNull("X-Jenkins"),
                response.getFirstHeaderOrNull("X-Jenkins-Session"),
                response.getFirstHeaderOrNull("X-Instance-Identity"), response.getFirstHeaderOrNull("X-SSH-Endpoint"),
                response.getFirstHeaderOrNull("Server"), null);
        } else {
            throw new RuntimeException(response.getStatusLine());
        }
    }
}
