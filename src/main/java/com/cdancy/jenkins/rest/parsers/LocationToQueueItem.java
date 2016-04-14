package com.cdancy.jenkins.rest.parsers;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.inject.Singleton;

import org.jclouds.http.HttpResponse;

import com.cdancy.jenkins.rest.domain.queue.QueueItem;
import com.google.common.base.Function;

/**
 * Created by dancc on 3/11/16.
 */
@Singleton
public class LocationToQueueItem implements Function<HttpResponse, QueueItem> {

   private static final Pattern pattern = Pattern.compile("^.*/queue/item/(\\d+)/$");

   public QueueItem apply(HttpResponse response) {

      String url = response.getFirstHeaderOrNull("Location");
      Matcher matcher = pattern.matcher(url);
      if (matcher.find() && matcher.groupCount() == 1) {
         int number = Integer.valueOf(matcher.group(1));
         return QueueItem.create(number, url);
      } else {
         return null;
      }
   }
}
