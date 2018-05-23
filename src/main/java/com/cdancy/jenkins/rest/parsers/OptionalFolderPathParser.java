package com.cdancy.jenkins.rest.parsers;

import com.cdancy.jenkins.rest.JenkinsUtils;
import com.google.common.base.Function;

import javax.inject.Singleton;

/*
 * Turn the optionalFolderPath param to jenkins URL style
 */
@Singleton
public class OptionalFolderPathParser implements Function<Object,String> {

    public static final String EMPTY_STRING = "";

    @Override
    public String apply(Object optionalFolderPath) {
        if(optionalFolderPath == null) {
            return EMPTY_STRING;
        }

        StringBuilder path = new StringBuilder(String.class.cast(optionalFolderPath));
        StringBuilder amendedPath = new StringBuilder();

        if(path.charAt(0) == '/'){
            path.deleteCharAt(0);
        }
        if(path.charAt(path.length()-1) == '/') {
            path.deleteCharAt(path.length()-1);
        }

        String[] folders = path.toString().split("/");
        for(String folder:folders) {
            amendedPath.append("job/").append(folder).append("/");
        }
        return amendedPath.toString();
    }
}
