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

package com.cdancy.jenkins.rest.domain.job;

import com.google.auto.value.AutoValue;
import org.jclouds.javax.annotation.Nullable;
import org.jclouds.json.SerializedNames;

@AutoValue
public abstract class Cause {

    @Nullable
    public abstract String clazz();

    public abstract String shortDescription();

    @Nullable
    public abstract String userId();

    @Nullable
    public abstract String userName();

    Cause() {
    }

    @SerializedNames({"_class", "shortDescription", "userId", "userName"})
    public static Cause create(final String clazz, final String shortDescription, final String userId, final String userName) {
        return new AutoValue_Cause(clazz, shortDescription, userId, userName);
    }
}
