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

import java.io.InputStream;
import java.io.InputStreamReader;

import javax.inject.Singleton;

import org.jclouds.http.HttpResponse;

import com.google.common.base.Charsets;
import com.google.common.base.Function;
import com.google.common.base.Throwables;
import com.google.common.io.CharStreams;

/**
 * Created by dancc on 3/11/16.
 */
@Singleton
public class BuildNumberToInteger implements Function<HttpResponse, Integer> {

   public Integer apply(HttpResponse response) {
      return Integer.valueOf(getTextOutput(response));
   }

   public String getTextOutput(HttpResponse response) {
      InputStream is = null;
      try {
         is = response.getPayload().openStream();
         return CharStreams.toString(new InputStreamReader(is, Charsets.UTF_8)).trim();
      } catch (Exception e) {
         Throwables.propagate(e);
      } finally {
         if (is != null) {
            try {
               is.close();
            } catch (Exception e) {
               Throwables.propagate(e);
            }
         }
      }

      return null;
   }
}
