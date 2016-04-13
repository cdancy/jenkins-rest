package com.cdancy.jenkins.rest.parsers;

import javax.inject.Singleton;

import org.jclouds.http.HttpResponse;

import com.cdancy.jenkins.rest.domain.system.Version;
import com.google.common.base.Function;

/**
 * Created by dancc on 3/11/16.
 */
@Singleton
public class JenkinsHeaderVersion implements Function<HttpResponse, Version> {

   public Version apply(HttpResponse response) {
      return Version.create(response.getFirstHeaderOrNull("X-Jenkins"),
            response.getFirstHeaderOrNull("X-Jenkins-Session"));
   }
}
