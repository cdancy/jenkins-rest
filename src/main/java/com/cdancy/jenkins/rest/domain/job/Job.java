package com.cdancy.jenkins.rest.domain.job;

import com.google.auto.value.AutoValue;
import org.jclouds.javax.annotation.Nullable;
import org.jclouds.json.SerializedNames;

@AutoValue
public abstract class Job {

    @Nullable
    public abstract String clazz();

    public abstract String name();

    public abstract String url();

    Job() {
    }

    @SerializedNames({"_class", "name", "url"})
    public static Job create(final String clazz, final String name, final String url) {
        return new AutoValue_Job(clazz, name, url);
    }
}
