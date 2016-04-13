package com.cdancy.jenkins.rest.parsers;

import javax.inject.Singleton;

import org.jclouds.http.HttpResponse;

import com.cdancy.jenkins.rest.domain.system.SystemInfo;
import com.google.common.base.Function;

/**
 * Created by dancc on 3/11/16.
 */
@Singleton
public class SystemInfoFromJenkinsHeaders implements Function<HttpResponse, SystemInfo> {

   public SystemInfo apply(HttpResponse response) {
      return SystemInfo.create(response.getFirstHeaderOrNull("X-Hudson"), response.getFirstHeaderOrNull("X-Jenkins"),
            response.getFirstHeaderOrNull("X-Jenkins-Session"), response.getFirstHeaderOrNull("X-Hudson-CLI-Port"),
            response.getFirstHeaderOrNull("X-Jenkins-CLI-Port"), response.getFirstHeaderOrNull("X-Jenkins-CLI2-Port"),
            response.getFirstHeaderOrNull("X-Instance-Identity"), response.getFirstHeaderOrNull("X-SSH-Endpoint"),
            response.getFirstHeaderOrNull("Server"));
   }
}
