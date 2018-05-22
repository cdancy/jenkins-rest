package com.cdancy.jenkins.rest.parsers;

import com.cdancy.jenkins.rest.JenkinsUtils;
import com.google.common.base.Function;

import javax.inject.Singleton;

/*
 * Turn the optionalFolderPath param to jenkins URL style if needed
 */
@Singleton
public class OptionalFolderPathParser implements Function<Object,String> {

    public static final String EMPTY_STRING = "";
    @Override
    public String apply(Object optionalFolderPath) {
        return (optionalFolderPath == null) ? EMPTY_STRING : JenkinsUtils.amendFolderPath(String.class.cast(optionalFolderPath));
    }
}
