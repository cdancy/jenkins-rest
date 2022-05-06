package com.cdancy.jenkins.rest.parsers;

import com.google.common.base.Function;

import javax.inject.Singleton;

/*
 * Turn the optionalFolderPath param to jenkins URL style
 */
@Singleton
public class OptionalFolderPathParser implements Function<Object,String> {

    public static final String EMPTY_STRING = "";
    public static final String FOLDER_NAME_PREFIX = "job/";
    public static final Character FOLDER_NAME_SEPARATOR = '/';

    @Override
    public String apply(Object optionalFolderPath) {
        if(optionalFolderPath == null) {
            return EMPTY_STRING;
        }

        final StringBuilder path = new StringBuilder((String) optionalFolderPath);
        if (path.length() == 0) {
            return EMPTY_STRING;
        }

        if(path.charAt(0) == FOLDER_NAME_SEPARATOR){
            path.deleteCharAt(0);
        }
        if (path.length() == 0) {
            return EMPTY_STRING;
        }

        if(path.charAt(path.length() - 1) == FOLDER_NAME_SEPARATOR) {
            path.deleteCharAt(path.length() - 1);
        }
        if (path.length() == 0) {
            return EMPTY_STRING;
        }

        final String[] folders = path.toString().split(Character.toString(FOLDER_NAME_SEPARATOR));
        path.setLength(0);
        for(final String folder : folders) {
            path.append(FOLDER_NAME_PREFIX).append(folder).append(FOLDER_NAME_SEPARATOR);
        }

        return path.toString();
    }
}
