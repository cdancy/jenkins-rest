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

import static com.google.common.io.BaseEncoding.base64;

import com.cdancy.jenkins.rest.auth.AuthenticationType;
import java.util.Objects;

import org.jclouds.domain.Credentials;
import org.jclouds.javax.annotation.Nullable;

/**
 * Credentials instance for Jenkins authentication. 
 */
public class JenkinsAuthentication extends Credentials {

    private final AuthenticationType authType;

    /**
     * Create instance of JenkinsAuthentication
     * 
     * @param authValue value to use for authentication type HTTP header.
     * @param authType authentication type (e.g. UsernamePassword, ApiToken, Anonymous).
     */
    private JenkinsAuthentication(final String authValue, final AuthenticationType authType) {
        //super(authValue != null ? (authValue.contains(":") ? authValue.split(":")[0] : null) : null, (authType == AuthenticationType.UsernamePassword || authType == AuthenticationType.ApiToken) && authValue.contains(":")
        super(null, (authType == AuthenticationType.UsernamePassword || authType == AuthenticationType.ApiToken) && authValue.contains(":")
                ? base64().encode(authValue.getBytes())
                : authValue);
        this.authType = authType;    
    }

    @Nullable
    public String authValue() {
        return this.credential;
    }

    public AuthenticationType authType() {
        return authType;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {

        private String authValue;
        private AuthenticationType authType;

        /**
         * Set 'UsernamePassword' credentials.
         * 
         * @param usernamePassword value to use for 'UsernamePassword' credentials.
         * @return this Builder.
         */
        public Builder credentials(final String usernamePassword) {
            this.authValue = Objects.requireNonNull(usernamePassword);
            this.authType = AuthenticationType.UsernamePassword;
            return this;
        }

        /**
         * Set 'ApiToken' credentials.
         *
         * @param apiTokenCredentials value to use for 'ApiToken' credentials.
         * @return this Builder.
         */
        public Builder apiToken(final String apiTokenCredentials) {
            this.authValue = Objects.requireNonNull(apiTokenCredentials);
            this.authType = AuthenticationType.ApiToken;
            return this;
        }

        /**
         * Build and instance of JenkinsCredentials.
         * 
         * @return instance of JenkinsCredentials.
         */
        public JenkinsAuthentication build() {
            return new JenkinsAuthentication(authValue, authType != null
                    ? authType
                    : AuthenticationType.Anonymous);
        }
    }
}
