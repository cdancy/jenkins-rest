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
import com.cdancy.jenkins.rest.exception.UndetectableIdentityException;

import java.nio.charset.StandardCharsets;
import java.util.Objects;

import org.jclouds.domain.Credentials;
import org.jclouds.javax.annotation.Nullable;

/**
 * Credentials instance for Jenkins authentication.
 */
public class JenkinsAuthentication extends Credentials {

    private final AuthenticationType authType;

    /**
     * Create instance of JenkinsAuthentication.
     *
     * @param identity the identity of the credential, this would be the username for the password or the api token or the base64 encoded value.
     * @param credential the username:password, or the username:apiToken, or their base64 encoded value. This is base64 encoded before being stored.
     * @param authType authentication type (e.g. UsernamePassword, UsernameApiToken, Anonymous).
     */
    private JenkinsAuthentication(final String identity, final String credential, final AuthenticationType authType) {
        super(identity,  credential.contains(":") ? base64().encode(credential.getBytes()) : credential);
        this.authType = authType;
    }

    /**
     * Return the base64 encoded value of the credential.
     *
     * @return the base 64 encoded authentication value.
     */
    @Nullable
    public String authValue() {
        return this.credential;
    }

    /**
     * Return the authentication type.
     *
     * @return the authentication type.
     */
    public AuthenticationType authType() {
        return authType;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {

        private String identity = "anonymous";
        private String credential = identity + ":";
        private AuthenticationType authType = AuthenticationType.Anonymous;

        /**
         * Set 'UsernamePassword' credentials.
         *
         * @param usernamePassword value to use for 'UsernamePassword' credentials. It can be the {@code username:password} in clear text or its base64 encoded value.
         * @return this Builder.
         */
        public Builder credentials(final String usernamePassword) {
            this.identity = Objects.requireNonNull(extractIdentity(usernamePassword));
            this.credential = Objects.requireNonNull(usernamePassword);
            this.authType = AuthenticationType.UsernamePassword;
            return this;
        }

        /**
         * Set 'UsernameApiToken' credentials.
         *
         * @param apiTokenCredentials value to use for 'ApiToken' credentials. It can be the {@code username:apiToken} in clear text or its base64 encoded value.
         * @return this Builder.
         */
        public Builder apiToken(final String apiTokenCredentials) {
            this.identity = Objects.requireNonNull(extractIdentity(apiTokenCredentials));
            this.credential = Objects.requireNonNull(apiTokenCredentials);
            this.authType = AuthenticationType.UsernameApiToken;
            return this;
        }

        /**
         * Extract the identity from the credential.
         *
         * The credential is entered by the user in one of two forms:
         * <ol>
         *  <li>Colon separated form: <code>username:password</code> or <code>username:password</code>
         *  <li>Base64 encoded of the colon separated form.
         * </ol>
         * Either way the identity is the username, and it can be extracted directly or by decoding.
         */
        private String extractIdentity(final String credentialString) {
            String decoded;
            if (!credentialString.contains(":")) {
                decoded = new String(base64().decode(credentialString),StandardCharsets.UTF_8);
            } else {
                decoded = credentialString;
            }
            if (!decoded.contains(":")) {
                throw new UndetectableIdentityException("Unable to detect the identity being used in '" + credentialString + "'. Supported types are a user:password, or a user:apiToken, or their base64 encoded value.");
            }
            if (decoded.equals(":")) {
                return "";
            }
            return decoded.split(":")[0];
        }

       /**
         * Build and instance of JenkinsCredentials.
         *
         * @return instance of JenkinsCredentials.
         */
        public JenkinsAuthentication build() {
            return new JenkinsAuthentication(identity, credential, authType);
        }
    }
}
