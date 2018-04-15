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

import static org.jclouds.util.Strings2.toStringAndClose;

import java.io.IOException;
import java.net.URL;
import java.util.Map;
import java.util.Properties;

import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;

import org.jclouds.Constants;
import org.jclouds.ContextBuilder;

import com.cdancy.jenkins.rest.config.JenkinsAuthenticationModule;

import com.google.common.base.Charsets;
import com.google.common.base.Functions;
import com.google.common.base.Throwables;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.JsonParser;
import com.squareup.okhttp.mockwebserver.MockResponse;
import com.squareup.okhttp.mockwebserver.MockWebServer;
import com.squareup.okhttp.mockwebserver.RecordedRequest;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static org.assertj.core.api.Assertions.assertThat;
import static org.testng.Assert.assertEquals;

/**
 * Base class for all Jenkins mock tests.
 */
public class BaseJenkinsMockTest {

    protected final String provider;
    private final JsonParser parser = new JsonParser();

    public BaseJenkinsMockTest() {
        provider = "jenkins";
    }

    /**
     * Create API from passed URL.
     * 
     * @param url endpoint of instance.
     * @return instance of JenkinsApi.
     */
    public JenkinsApi api(final URL url) {
        final JenkinsAuthentication creds = JenkinsAuthentication.builder().build();
        final JenkinsAuthenticationModule credsModule = new JenkinsAuthenticationModule(creds);
        return ContextBuilder.newBuilder(provider)
                .endpoint(url.toString())
                .overrides(setupProperties())
                .modules(Lists.newArrayList(credsModule))
                .buildApi(JenkinsApi.class);
    }

    protected Properties setupProperties() {
        final Properties properties = new Properties();
        properties.setProperty(Constants.PROPERTY_MAX_RETRIES, "0");
        return properties;
    }

    /**
     * Create a MockWebServer with an initial bread-crumb response.
     *
     * @return instance of MockWebServer
     * @throws IOException
     *             if unable to start/play server
     */
    public static MockWebServer mockWebServer() throws IOException {
        final MockWebServer server = new MockWebServer();
        server.start();
        return server;
    }

    /**
     * Get the String representation of some resource to be used as payload.
     *
     * @param resource
     *            String representation of a given resource
     * @return payload in String form
     */
    public String payloadFromResource(final String resource) {
        try {
            return new String(toStringAndClose(getClass().getResourceAsStream(resource)).getBytes(Charsets.UTF_8));
        } catch (IOException e) {
            throw Throwables.propagate(e);
        }
    }

    private static Map<String, String> extractParams(final String path) {

        final int qmIndex = path.indexOf('?');
        if (qmIndex <= 0) {
            return ImmutableMap.of();
        }

        final ImmutableMap.Builder<String, String> builder = ImmutableMap.builder();

        final String[] params = path.substring(qmIndex + 1).split("&");
        for (final String param : params) {
            final String[] keyValue = param.split("=", 2);
            if (keyValue.length > 1) {
                builder.put(keyValue[0], keyValue[1]);
            }
        }

        return builder.build();
    }

    protected RecordedRequest assertSent(final MockWebServer server, 
            final String method, 
            final String path) throws InterruptedException {

        return assertSent(server, method, path, ImmutableMap.<String, Void> of());
    }

    protected RecordedRequest assertSent(final MockWebServer server, 
            final String method, 
            final String expectedPath, 
            final Map<String, ?> queryParams) throws InterruptedException {

        RecordedRequest request = server.takeRequest();
        assertThat(request.getMethod()).isEqualTo(method);
        assertThat(request.getHeader(HttpHeaders.ACCEPT)).isEqualTo(APPLICATION_JSON);

        final String path = request.getPath();
        final String rawPath = path.contains("?") ? path.substring(0, path.indexOf('?')) : path;
        assertThat(rawPath).isEqualTo(expectedPath);

        final Map<String, String> normalizedParams = Maps.transformValues(queryParams, Functions.toStringFunction());
        assertThat(normalizedParams).isEqualTo(extractParams(path));

        return request;
    }

    protected RecordedRequest assertSentWithFormData(final MockWebServer server, 
            final String method, 
            final String path, 
            final String body) throws InterruptedException {

        RecordedRequest request = server.takeRequest();
        assertThat(request.getMethod()).isEqualTo(method);
        assertThat(request.getPath()).isEqualTo(path);
        assertThat(request.getUtf8Body()).isEqualTo(body);
        assertThat(request.getHeader(HttpHeaders.ACCEPT)).isEqualTo(APPLICATION_JSON);
        assertThat(request.getHeader(HttpHeaders.CONTENT_TYPE)).isEqualTo(MediaType.APPLICATION_FORM_URLENCODED);
        return request;
    }

    protected RecordedRequest assertSentWithFormData(MockWebServer server, String method,
                                                    String path, String body,
                                                    String acceptType) throws InterruptedException {

        RecordedRequest request = server.takeRequest();
        assertThat(request.getMethod()).isEqualTo(method);
        assertThat(request.getPath()).isEqualTo(path);
        assertThat(request.getUtf8Body()).isEqualTo(body);
        assertThat(request.getHeader(HttpHeaders.ACCEPT)).isEqualTo(acceptType);
        assertThat(request.getHeader(HttpHeaders.CONTENT_TYPE)).isEqualTo(MediaType.APPLICATION_FORM_URLENCODED);
        return request;
    }

    protected RecordedRequest assertSentWithXMLFormDataAccept(MockWebServer server, String method, String path,
        String body, String acceptType) throws InterruptedException {

        RecordedRequest request = server.takeRequest();
        assertThat(request.getMethod()).isEqualTo(method);
        assertThat(request.getPath()).isEqualTo(path);
        assertThat(request.getUtf8Body()).isEqualTo(body);
        assertThat(request.getHeader(HttpHeaders.ACCEPT)).isEqualTo(acceptType);
        assertThat(request.getHeader(HttpHeaders.CONTENT_TYPE)).isEqualTo(MediaType.APPLICATION_XML);
        return request;
    }

    protected RecordedRequest assertSentAcceptText(MockWebServer server, String method, String path) throws InterruptedException {
        RecordedRequest request = server.takeRequest();
        assertThat(request.getMethod()).isEqualTo(method);
        assertThat(request.getPath()).isEqualTo(path);
        assertThat(request.getHeader(HttpHeaders.ACCEPT)).isEqualTo(MediaType.TEXT_PLAIN);
        return request;
    }

    protected RecordedRequest assertSentAccept(MockWebServer server, String method, String path, String acceptType) throws InterruptedException {
        RecordedRequest request = server.takeRequest();
        assertThat(request.getMethod()).isEqualTo(method);
        assertThat(request.getPath()).isEqualTo(path);
        assertThat(request.getHeader(HttpHeaders.ACCEPT)).isEqualTo(acceptType);
        return request;
    }

    protected RecordedRequest assertSent(MockWebServer server, String method, String path, String json) throws InterruptedException {
        RecordedRequest request = server.takeRequest();
        assertEquals(request.getHeader("Content-Type"), APPLICATION_JSON);
        assertEquals(parser.parse(request.getUtf8Body()), parser.parse(json));
        return request;
    }
}
