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
 * The request entity has a Content-Type that the server does not support.
 * Some Jenkins REST API accept application/json format, but
 * check the individual resource documentation for more details. Additionally,
 * double-check that you are setting the Content-Type header correctly on your
 * request (e.g. using -H "Content-Type: application/json" in cURL).
 */
public class UnsupportedMediaTypeException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public UnsupportedMediaTypeException(final String arg0) {
      super(arg0);
    }
}
