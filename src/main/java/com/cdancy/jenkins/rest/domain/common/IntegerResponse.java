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

package com.cdancy.jenkins.rest.domain.common;

import java.util.List;

import org.jclouds.javax.annotation.Nullable;
import org.jclouds.json.SerializedNames;

import com.cdancy.jenkins.rest.JenkinsUtils;
import com.google.auto.value.AutoValue;

/**
 * Integer response to be returned when an endpoint returns
 * an integer.
 * 
 * <p>When the HTTP response code is valid the `value` parameter will 
 * be set to the integer value while a non-valid response has the `value` set to
 * null along with any potential `error` objects returned from Jenkins.
 */
@AutoValue
public abstract class IntegerResponse implements Value<Integer>, ErrorsHolder {
    
    @SerializedNames({ "value", "errors" })
    public static IntegerResponse create(@Nullable final Integer value, 
            final List<Error> errors) {
        
        return new AutoValue_IntegerResponse(value, 
                JenkinsUtils.nullToEmpty(errors));
    }
}
