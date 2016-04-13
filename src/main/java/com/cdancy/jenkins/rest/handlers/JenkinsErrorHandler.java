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

import java.io.IOException;

import javax.annotation.Resource;

import org.jclouds.http.HttpCommand;
import org.jclouds.http.HttpErrorHandler;
import org.jclouds.http.HttpResponse;
import org.jclouds.http.HttpResponseException;
import org.jclouds.logging.Logger;
import org.jclouds.rest.ResourceAlreadyExistsException;
import org.jclouds.rest.ResourceNotFoundException;
import org.jclouds.util.Strings2;

import com.google.common.base.Throwables;

/**
 * Handle errors and propagate exception
 */
public class JenkinsErrorHandler implements HttpErrorHandler {
   @Resource
   protected Logger logger = Logger.NULL;

   public void handleError(HttpCommand command, HttpResponse response) {

      String message = parseMessage(response);
      Exception exception = null;
      try {

         message = message != null ? message
               : String.format("%s -> %s", command.getCurrentRequest().getRequestLine(), response.getStatusLine());

         switch (response.getStatusCode()) {
            case 400:
               exception = new IllegalArgumentException(message);
               break;
            case 404:
               exception = new ResourceNotFoundException(message);
               break;
            case 409:
               exception = new ResourceAlreadyExistsException(message);
               break;
         }
      } catch (Exception e) {
         exception = new HttpResponseException(command, response, e);
      } finally {
         if (exception == null) {
            exception = message != null ? new HttpResponseException(command, response, message)
                  : new HttpResponseException(command, response);
         }
         closeQuietly(response.getPayload());
         command.setException(exception);
      }
   }

   private String parseMessage(HttpResponse response) {
      if (response.getPayload() == null)
         return null;
      try {
         return Strings2.toStringAndClose(response.getPayload().openStream());
      } catch (IOException e) {
         throw Throwables.propagate(e);
      }
   }
}
