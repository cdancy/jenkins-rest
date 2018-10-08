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
 * Generic response encapsulating a single value, or state, from server.
 */
@AutoValue
public abstract class RequestStatus<V> implements Value<V>, ErrorsHolder {
    
    @SerializedNames({ "value", "errors" })
    public static <V> RequestStatus<V> create(@Nullable final V value,
            final List<Error> errors) {
        
        return new AutoValue_RequestStatus(value, 
                JenkinsUtils.nullToEmpty(errors));
    }
}
