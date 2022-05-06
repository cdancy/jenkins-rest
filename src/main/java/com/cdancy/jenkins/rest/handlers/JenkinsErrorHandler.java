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
package com.cdancy.jenkins.rest.handlers;

import static org.jclouds.util.Closeables2.closeQuietly;

import com.cdancy.jenkins.rest.exception.ForbiddenException;
import com.cdancy.jenkins.rest.exception.MethodNotAllowedException;
import com.cdancy.jenkins.rest.exception.RedirectTo404Exception;
import com.cdancy.jenkins.rest.exception.UnsupportedMediaTypeException;

import java.io.IOException;

import org.jclouds.http.HttpCommand;
import org.jclouds.http.HttpErrorHandler;
import org.jclouds.http.HttpResponse;
import org.jclouds.http.HttpResponseException;
import org.jclouds.rest.ResourceAlreadyExistsException;
import org.jclouds.rest.ResourceNotFoundException;
import org.jclouds.rest.AuthorizationException;
import org.jclouds.util.Strings2;

import com.google.common.base.Throwables;

/**
 * Handle errors and propagate exception
 */
public class JenkinsErrorHandler implements HttpErrorHandler {

    @Override
    public void handleError(final HttpCommand command, final HttpResponse response) {

        Exception exception = null;
        try {
            final String message = parseMessage(command, response);

            switch (response.getStatusCode()) {
                case 400:
                    if (command.getCurrentRequest().getMethod().equals("POST")) {
                        if (command.getCurrentRequest().getRequestLine().contains("/createItem")) {
                            if (message.contains("A job already exists with the name")) {
                                exception = new ResourceAlreadyExistsException(message);
                                break;
                            }
                        }
                    }
                    exception = new IllegalArgumentException(message);
                    break;
                case 401:
                    exception = new AuthorizationException(message);
                    break;
                case 403:
                    exception = new ForbiddenException(message);
                    break;
                case 404:
                    // When Jenkins replies to term or kill with a redirect to a non-existent URL
                    // we want to return a custom error message and avoid an exception in the user code.
                    if (command.getCurrentRequest().getMethod().equals("POST")) {
                        final String path = command.getCurrentRequest().getEndpoint().getPath();
                        if (path.endsWith("/term/")) {
                            exception = new RedirectTo404Exception("The term operation does not exist for " + command.getCurrentRequest().getEndpoint().toString() + ", try stop instead.");
                            break;
                        } else if (path.endsWith("/kill/")) {
                            exception = new RedirectTo404Exception("The kill operation does not exist for " + command.getCurrentRequest().getEndpoint().toString() + ", try stop instead.");
                            break;
                        }
                    }
                    exception = new ResourceNotFoundException(message);
                    break;
                case 405:
                    exception = new MethodNotAllowedException(message);
                    break;
                case 409:
                    exception = new ResourceAlreadyExistsException(message);
                    break;
                case 415:
                    exception = new UnsupportedMediaTypeException(message);
                    break;
                default:
                    exception = new HttpResponseException(command, response);
            }
        } catch (Exception e) {
            exception = new HttpResponseException(command, response, e);
        } finally {
            closeQuietly(response.getPayload());
            command.setException(exception);
        }
    }

    private String parseMessage(final HttpCommand command, final HttpResponse response) {
        if (response.getPayload() != null) {
            try {
                return Strings2.toStringAndClose(response.getPayload().openStream());
            } catch (IOException e) {
                throw Throwables.propagate(e);
            }
        } else {
            final String errorMessage = response.getFirstHeaderOrNull("X-Error");
            return command.getCurrentRequest().getRequestLine() +
                " -> " +
                response.getStatusLine() +
                " -> " +
                (errorMessage != null ? errorMessage : "");
        }
    }
}
