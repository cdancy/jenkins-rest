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

package com.cdancy.jenkins.rest;

import org.jclouds.ContextBuilder;

public class JenkinsClient {

   private String endPoint;
   private String credentials;
   private final JenkinsApi jenkinsApi;

   public JenkinsClient(final String endPoint, final String credentials) {
      this.endPoint = endPoint;
      this.credentials = credentials;

      configureParameters();

      this.jenkinsApi = ContextBuilder.newBuilder(new JenkinsApiMetadata.Builder().build()).endpoint(endPoint())
            .credentials("N/A", credentials()).buildApi(JenkinsApi.class);
   }

   private void configureParameters() {

      // query system for endPoint value
      if (endPoint == null) {
         if ((endPoint = retrivePropertyValue("JenkinsApi.rest.endpoint")) == null) {
            if ((endPoint = retrivePropertyValue("jenkinsRestEndpoint")) == null) {
               if ((endPoint = retrivePropertyValue("JENKINS_REST_ENDPOINT")) == null) {
                  endPoint = "http://127.0.0.1:2379";
                  System.out.println("Jenkins REST endpoint was not found. Defaulting to: " + endPoint);
               }
            }
         }
      }

      // query system for credentials value
      if (credentials == null) {
         if ((credentials = retrivePropertyValue("jenkins.rest.credentials")) == null) {
            if ((credentials = retrivePropertyValue("jenkinsRestCredentials")) == null) {
               if ((credentials = retrivePropertyValue("JENKINS_REST_CREDENTIALS")) == null) {
                  credentials = "";
                  System.out.println("Jenkins REST credentials was not found. Assuming anonymous usage.");
               }
            }
         }
      }
   }

   private String retrivePropertyValue(String key) {
      String value = System.getProperty(key);
      return value != null ? value : System.getenv(key);
   }

   public String endPoint() {
      return endPoint;
   }

   public String credentials() {
      return credentials;
   }

   public JenkinsApi api() {
      return jenkinsApi;
   }

   public static class Builder {
      private String endPoint;
      private String credentials;

      public Builder() {
      }

      public Builder(final String endPoint, final String credentials) {
         this.endPoint = endPoint;
         this.credentials = credentials;
      }

      public Builder endPoint(String endPoint) {
         this.endPoint = endPoint;
         return this;
      }

      public Builder credentials(String credentials) {
         this.credentials = credentials;
         return this;
      }

      public JenkinsClient build() {
         return new JenkinsClient(endPoint, credentials);
      }
   }
}
