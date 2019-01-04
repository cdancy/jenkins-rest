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
package com.cdancy.jenkins.rest.binders;

import static com.google.common.base.Preconditions.checkArgument;

import java.util.List;
import java.util.Map;

import javax.inject.Singleton;

import com.google.common.collect.Lists;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpRequest.Builder;
import org.jclouds.rest.Binder;

@Singleton
public class BindMapToForm implements Binder {
   @SuppressWarnings("unchecked")
   @Override
   public <R extends HttpRequest> R bindToRequest(final R request, final Object properties) {

       if (properties == null) {
           return (R) request.toBuilder().build();
       }

      checkArgument(properties instanceof Map, "binder is only valid for Map");
      Map<String, List<String>> props = (Map<String, List<String>>) properties;

      Builder<?> builder = request.toBuilder();
      for (Map.Entry<String, List<String>> prop : props.entrySet()) {
         if (prop.getKey() != null) {
            String potentialKey = prop.getKey().trim();
            if (potentialKey.length() > 0) {
                if (prop.getValue() == null) {
                    prop.setValue(Lists.newArrayList(""));
                }

                builder.addFormParam(potentialKey, prop.getValue().toArray(new String[prop.getValue().size()]));
            }
         }
      }

      return (R) builder.build();
   }
}
