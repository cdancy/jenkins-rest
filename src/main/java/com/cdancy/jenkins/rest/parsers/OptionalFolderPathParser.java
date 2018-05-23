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
        if(optionalFolderPath == null) {
            return EMPTY_STRING;
        }

        String folderPath = String.class.cast(optionalFolderPath);
        String amendedPath = "";

        if(folderPath.startsWith("/")) {
            folderPath = folderPath.replaceAll("^/+","");
        }

        if(folderPath.endsWith("/")) {
            folderPath = folderPath.replaceAll("/+$","");
        }

        String[] folderNames = folderPath.split("/");
        for (String folder:folderNames) {
            amendedPath += "job/" + folder + "/";
        }
        return amendedPath;
    }
}
