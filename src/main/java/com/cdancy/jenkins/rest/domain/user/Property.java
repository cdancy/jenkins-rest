package com.cdancy.jenkins.rest.domain.user;

import com.google.auto.value.AutoValue;
import org.jclouds.json.SerializedNames;

@AutoValue
public abstract class Property {

    public abstract String clazz();

    Property() {
    }

    @SerializedNames({"_class"})
    public static Property create(final String clazz) {
        return new AutoValue_Property(clazz);
    }
}
