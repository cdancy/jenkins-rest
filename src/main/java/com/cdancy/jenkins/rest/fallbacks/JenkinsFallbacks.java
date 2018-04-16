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
package com.cdancy.jenkins.rest.fallbacks;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Predicates.equalTo;
import static com.google.common.base.Throwables.propagate;

import static org.jclouds.http.HttpUtils.returnValueOnCodeOrNull;

import com.cdancy.jenkins.rest.JenkinsUtils;
import com.cdancy.jenkins.rest.domain.common.RequestStatus;
import com.cdancy.jenkins.rest.domain.common.Error;
import com.google.common.collect.Lists;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import java.util.Iterator;
import java.util.List;

import org.jclouds.Fallback;

public final class JenkinsFallbacks {

    public static final class FalseOn503 implements Fallback<Boolean> {
        public Boolean createOrPropagate(Throwable t) throws Exception {
            if (checkNotNull(t, "throwable") != null && t.getMessage().contains("{\"health\": \"false\"}")
                    && returnValueOnCodeOrNull(t, true, equalTo(503)) != null) {
                return Boolean.FALSE;
            }
            throw propagate(t);
        }
    }

    public static final class FalseOn400AndJobAlreadyExists implements Fallback<Boolean> {
        public Boolean createOrPropagate(Throwable t) throws Exception {
            if (checkNotNull(t, "throwable") != null && t.getMessage().contains("A job already exists with the name")) {
               return Boolean.FALSE;
            }
            throw propagate(t);
        }
    }

    public static final class RequestStatusOnError implements Fallback<Object> {
        @Override
        public Object createOrPropagate(final Throwable throwable) throws Exception {
            if (checkNotNull(throwable, "throwable") != null) {
                try {
                    return createRequestStatusFromErrors(getErrors(throwable));
                } catch (JsonSyntaxException e) {
                    final Error error = Error.create(null, throwable.getMessage(),
                            throwable.getClass().getName());
                    final List<Error> errors = Lists.newArrayList(error);
                    return RequestStatus.create(false, errors);
                }
            }
            throw propagate(throwable);
        }
    }

    public static RequestStatus createRequestStatusFromErrors(final List<Error> errors) {
        return RequestStatus.create(false, errors);
    }

    /**
     * Parse list of Error's from output.
     *
     * @param output Throwable containing error data
     * @return List of culled Error's
     */
    public static List<Error> getErrors(final Throwable output) {

        final List<Error> errors = Lists.newArrayList();

        String context = null;
        String message = null;
        final String [] messageParts = output.getMessage().split("->");
        switch (messageParts.length) {
            case 1: message = messageParts[0].trim(); break;
            case 3: context = messageParts[0].trim(); message = messageParts[2].trim(); break;
        }

        final Error error = Error.create(context, message, output.getClass().getCanonicalName());
        errors.add(error);

        return errors;
    }
}
