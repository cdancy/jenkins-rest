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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.inject.Singleton;

import org.jclouds.http.HttpResponse;

import com.google.common.base.Function;
import com.google.common.collect.Lists;

import com.cdancy.jenkins.rest.domain.common.Error;
import com.cdancy.jenkins.rest.domain.common.IntegerResponse;

/**
 * Created by dancc on 3/11/16.
 */
@Singleton
public class LocationToQueueId implements Function<HttpResponse, IntegerResponse> {

   private static final Pattern pattern = Pattern.compile("^.*/queue/item/(\\d+)/$");

   public IntegerResponse apply(HttpResponse response) {
       if (response == null) {
           throw new RuntimeException("Unexpected NULL HttpResponse object");
       }

      String url = response.getFirstHeaderOrNull("Location");
      if (url != null) {
         Matcher matcher = pattern.matcher(url);
         if (matcher.find() && matcher.groupCount() == 1) {
            return IntegerResponse.create(Integer.valueOf(matcher.group(1)), null);
         }
      }
      final Error error = Error.create(null,
         "No queue item Location header could be found despite getting a valid HTTP response.",
         NumberFormatException.class.getCanonicalName());
      return IntegerResponse.create(null, Lists.newArrayList(error));
   }
}
