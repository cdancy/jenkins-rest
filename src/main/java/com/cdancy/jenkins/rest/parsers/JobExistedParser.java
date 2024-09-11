package com.cdancy.jenkins.rest.parsers;

import com.cdancy.jenkins.rest.domain.job.JobExisted;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.jclouds.http.HttpResponse;
import com.google.common.base.Function;
import javax.inject.Singleton;

import static com.cdancy.jenkins.rest.JenkinsUtils.getTextOutput;

/**
 * Author: kun.tang@daocloud.io
 * Date:2024/9/11
 * Time:15:34
 */


@Singleton
public class JobExistedParser implements Function<HttpResponse, JobExisted> {
    @Override
    public @Nullable JobExisted apply(@Nullable HttpResponse httpResponse) {
        return JobExisted.create(getTextOutput(httpResponse));
    }
}
