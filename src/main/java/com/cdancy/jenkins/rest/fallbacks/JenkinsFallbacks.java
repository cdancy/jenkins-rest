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

import com.cdancy.jenkins.rest.domain.common.IntegerResponse;
import com.cdancy.jenkins.rest.domain.common.RequestStatus;
import com.cdancy.jenkins.rest.domain.common.Error;
import com.cdancy.jenkins.rest.domain.crumb.Crumb;
import com.cdancy.jenkins.rest.domain.plugins.Plugins;
import com.cdancy.jenkins.rest.domain.system.SystemInfo;

import com.google.common.collect.Lists;
import com.google.gson.JsonSyntaxException;

import java.util.List;

import org.jclouds.Fallback;
import org.jclouds.rest.ResourceNotFoundException;

public final class JenkinsFallbacks {

    public static final class SystemInfoOnError implements Fallback<Object> {
        @Override
        public Object createOrPropagate(final Throwable throwable) {
            checkNotNull(throwable, "throwable");
            return createSystemInfoFromErrors(getErrors(throwable));
        }
    }

    public static final class RequestStatusOnError implements Fallback<Object> {
        @Override
        public Object createOrPropagate(final Throwable throwable) {
            checkNotNull(throwable, "throwable");
            try {
                return RequestStatus.create(false, getErrors(throwable));
            } catch (JsonSyntaxException e) {
                return RequestStatus.create(false, getErrors(e));
            }
        }
    }

    public static final class IntegerResponseOnError implements Fallback<Object> {
        @Override
        public Object createOrPropagate(final Throwable throwable) {
            checkNotNull(throwable, "throwable");
            try {
                return IntegerResponse.create(null, getErrors(throwable));
            } catch (JsonSyntaxException e) {
                return IntegerResponse.create(null, getErrors(e));
            }
        }
    }

    public static final class CrumbOnError implements Fallback<Object> {
        @Override
        public Object createOrPropagate(final Throwable throwable) {
            checkNotNull(throwable, "throwable");
            try {
                return Crumb.create(null, getErrors(throwable));
            } catch (JsonSyntaxException e) {
                return Crumb.create(null, getErrors(e));
            }
        }
    }

    public static final class PluginsOnError implements Fallback<Object> {
        @Override
        public Object createOrPropagate(final Throwable throwable) {
            checkNotNull(throwable, "throwable");
            try {
                return Plugins.create(null, null, getErrors(throwable));
            } catch (JsonSyntaxException e) {
                return Plugins.create(null, null, getErrors(e));
            }
        }
    }

    // fix/hack for Jenkins jira issue: JENKINS-21311
    public static final class JENKINS_21311 implements Fallback<Object> {
        @Override
        public Object createOrPropagate(final Throwable throwable) {
            checkNotNull(throwable, "throwable");
            try {
                if (throwable.getClass() == ResourceNotFoundException.class) {
                    return RequestStatus.create(true, null);
                } else {
                    return RequestStatus.create(false, getErrors(throwable));
                }
            } catch (JsonSyntaxException e) {
                return RequestStatus.create(false, getErrors(e));
            }
        }
    }

    public static SystemInfo createSystemInfoFromErrors(final List<Error> errors) {
        final String illegalValue = "-1";
        return SystemInfo.create(illegalValue, illegalValue, illegalValue,
                illegalValue, illegalValue, illegalValue, errors);
    }

    /**
     * Parse list of Error's from generic Exception.
     *
     * @param output Exception containing error data
     * @return List of culled Error's
     */
    public static List<Error> getErrors(final Exception output) {
        final Error error = Error.create(null, output.getMessage(),
                output.getClass().getName());
        return Lists.newArrayList(error);
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
        String message = output.getMessage();
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
