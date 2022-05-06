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

package com.cdancy.jenkins.rest.exception;

/**
 * The credential being passed cannot be processed.
 *
 * A valid credential is either
 * <ol>
 *     <li>a colon separated identity and password or token (tuple of )identity:password or identity:token); or</li>
 *     <li>the base64 encoded form of identity:password or identity:token</li>
 * </ol>
 * When the credential does not contain a colon, an attempt is made at decoding it to extract the identity.
 * When this fails, this exception is thrown.
 *
 */
public class UndetectableIdentityException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public UndetectableIdentityException(final String arg0) {
            super(arg0);
    }
}
