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

package com.cdancy.jenkins.rest.internal;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static org.assertj.core.api.Assertions.assertThat;
import static org.jclouds.util.Strings2.toStringAndClose;
import static org.testng.Assert.assertEquals;

import java.io.IOException;
import java.net.URL;
import java.util.Properties;

import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;

import org.jclouds.Constants;
import org.jclouds.ContextBuilder;

import com.cdancy.jenkins.rest.JenkinsApi;
import com.google.common.base.Charsets;
import com.google.common.base.Throwables;
import com.google.gson.JsonParser;
import com.squareup.okhttp.mockwebserver.MockWebServer;
import com.squareup.okhttp.mockwebserver.RecordedRequest;

/**
 * Base class for all Jenkins mock tests.
 */
public class BaseJenkinsMockTest {

   protected String provider;
   private final JsonParser parser = new JsonParser();

   public BaseJenkinsMockTest() {
      provider = "jenkins";
   }

   public JenkinsApi api(URL url) {
      return ContextBuilder.newBuilder(provider).endpoint(url.toString()).overrides(setupProperties())
            .buildApi(JenkinsApi.class);
   }

   protected Properties setupProperties() {
      Properties properties = new Properties();
      properties.setProperty(Constants.PROPERTY_MAX_RETRIES, "0");
      return properties;
   }

   public static MockWebServer mockEtcdJavaWebServer() throws IOException {
      MockWebServer server = new MockWebServer();
      server.play();
      return server;
   }

   public String payloadFromResource(String resource) {
      try {
         return new String(toStringAndClose(getClass().getResourceAsStream(resource)).getBytes(Charsets.UTF_8));
      } catch (IOException e) {
         throw Throwables.propagate(e);
      }
   }

   protected RecordedRequest assertSent(MockWebServer server, String method, String path) throws InterruptedException {
      RecordedRequest request = server.takeRequest();
      assertThat(request.getMethod()).isEqualTo(method);
      assertThat(request.getPath()).isEqualTo(path);
      assertThat(request.getHeader(HttpHeaders.ACCEPT)).isEqualTo(MediaType.APPLICATION_JSON);
      return request;
   }

   protected RecordedRequest assertSentWithFormData(MockWebServer server, String method, String path, String body)
         throws InterruptedException {
      RecordedRequest request = server.takeRequest();
      assertThat(request.getMethod()).isEqualTo(method);
      assertThat(request.getPath()).isEqualTo(path);
      assertThat(request.getUtf8Body()).isEqualTo(body);
      assertThat(request.getHeader(HttpHeaders.ACCEPT)).isEqualTo(MediaType.APPLICATION_JSON);
      assertThat(request.getHeader(HttpHeaders.CONTENT_TYPE)).isEqualTo(MediaType.APPLICATION_FORM_URLENCODED);
      return request;
   }

   protected RecordedRequest assertSentWithXMLFormData(MockWebServer server, String method, String path, String body)
         throws InterruptedException {
      RecordedRequest request = server.takeRequest();
      assertThat(request.getMethod()).isEqualTo(method);
      assertThat(request.getPath()).isEqualTo(path);
      assertThat(request.getUtf8Body()).isEqualTo(body);
      assertThat(request.getHeader(HttpHeaders.ACCEPT)).isEqualTo(MediaType.APPLICATION_JSON);
      assertThat(request.getHeader(HttpHeaders.CONTENT_TYPE)).isEqualTo(MediaType.APPLICATION_XML);
      return request;
   }

   protected RecordedRequest assertSentAcceptText(MockWebServer server, String method, String path)
         throws InterruptedException {
      RecordedRequest request = server.takeRequest();
      assertThat(request.getMethod()).isEqualTo(method);
      assertThat(request.getPath()).isEqualTo(path);
      assertThat(request.getHeader(HttpHeaders.ACCEPT)).isEqualTo(MediaType.TEXT_PLAIN);
      return request;
   }

   protected RecordedRequest assertSent(MockWebServer server, String method, String path, String json)
         throws InterruptedException {
      RecordedRequest request = assertSent(server, method, path);
      assertEquals(request.getHeader("Content-Type"), APPLICATION_JSON);
      assertEquals(parser.parse(request.getUtf8Body()), parser.parse(json));
      return request;
   }
}
