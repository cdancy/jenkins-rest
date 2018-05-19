package com.cdancy.jenkins.rest.parsers;

import com.cdancy.jenkins.rest.JenkinsUtils;
import com.google.common.base.Function;

import javax.inject.Singleton;

/*
 * Turn the optionalFolderPath param to jenkins URL style if needed
 */
@Singleton
public class optionalFolderPathParser implements Function<Object,String> {

    @Override
    public String apply(Object optionalFolderPath) {
        if(optionalFolderPath != null) {
            return JenkinsUtils.amendFolderPath(String.class.cast(optionalFolderPath));
        }
        return null;
    }
}
