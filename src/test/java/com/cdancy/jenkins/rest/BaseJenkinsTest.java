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

import java.io.IOException;
import java.net.URL;
import java.util.Objects;
import java.util.Properties;

import org.jclouds.Constants;
import org.jclouds.ContextBuilder;
import org.jclouds.logging.slf4j.config.SLF4JLoggingModule;
import static org.jclouds.util.Strings2.toStringAndClose;

import com.cdancy.jenkins.rest.config.JenkinsAuthenticationModule;
import com.cdancy.jenkins.rest.auth.AuthenticationType;

import com.google.common.base.Charsets;
import com.google.common.base.Throwables;
import com.google.common.collect.Lists;

/**
 * Base class for Jenkins mock tests and some Live tests.
 */
public class BaseJenkinsTest {

    // This token can only be used by mock test as real tokens can only be obtained from jenkins itself
    public static final String USERNAME_APITOKEN = "user:token";

    protected final String provider;

    public BaseJenkinsTest() {
        provider = "jenkins";
    }

    /**
     * Create API from passed URL.
     *
     * The default authentication is the ApiToken, for it requires no crumb and simplifies mockTests expectations.
     *
     * @param url endpoint of instance.
     * @return instance of JenkinsApi.
     */
    public JenkinsApi api(final URL url) {
        return api(url, AuthenticationType.UsernameApiToken, USERNAME_APITOKEN);
    }

    /**
     * Create API for Anonymous access using the passed URL.
     *
     * @param url endpoint of instance.
     * @return instance of JenkinsApi.
     */
    public JenkinsApi anonymousAuthApi(final URL url) {
        return api(url, AuthenticationType.Anonymous, AuthenticationType.Anonymous.name().toLowerCase());
    }

    /**
     * Create API for the given authentication type and string.
     *
     * @param url the endpoint of the instance.
     * @param authType the type of authentication.
     * @param authString the string to use as the credential.
     * @return instance of JenkinsApi.
     */
    public JenkinsApi api(final URL url, final AuthenticationType authType, final String authString) {
        final JenkinsAuthentication creds = creds(authType, authString);
        final JenkinsAuthenticationModule credsModule = new JenkinsAuthenticationModule(creds);
        return ContextBuilder.newBuilder(provider)
                .endpoint(url.toString())
                .overrides(setupProperties())
                .modules(Lists.newArrayList(credsModule, new SLF4JLoggingModule()))
                .buildApi(JenkinsApi.class);
    }
    /**
     * Create the Jenkins Authentication instance.
     *
     * @param authType authentication type. Falls back to anonymous when null.
     * @param authString the authentication string to use (username:password, username:apiToken, or base64 encoded).
     * @return an authentication instance.
     */
    public JenkinsAuthentication creds(final AuthenticationType authType, final String authString) {
        final JenkinsAuthentication.Builder authBuilder = JenkinsAuthentication.builder();
        if (authType == AuthenticationType.UsernamePassword) {
            authBuilder.credentials(authString);
        } else if (authType == AuthenticationType.UsernameApiToken) {
            authBuilder.apiToken(authString);
        }
        // Anonymous authentication is the default when not specified
        return authBuilder.build();
    }

    protected Properties setupProperties() {
        final Properties properties = new Properties();
        properties.setProperty(Constants.PROPERTY_MAX_RETRIES, "0");
        properties.setProperty(Constants.PROPERTY_CONNECTION_TIMEOUT, "60");
        return properties;
    }

    /**
     * Get the String representation of some resource to be used as payload.
     *
     * @param resource
     *            String representation of a given resource
     * @return payload in String form
     */
    public String payloadFromResource(String resource) {
        try {
            return new String(toStringAndClose(Objects.requireNonNull(getClass().getResourceAsStream(resource))).getBytes(Charsets.UTF_8));
        } catch (IOException e) {
            throw Throwables.propagate(e);
        }
    }
}
