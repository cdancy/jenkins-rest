package com.cdancy.jenkins.rest.domain.job;

/**
 * Author: kun.tang@daocloud.io
 * Date:2024/9/11
 * Time:15:52
 */

public class Folder {

    private String name;
    private String mode;
    private Folder(){}

    public Folder(String folderName){
        this.name = folderName;
        this.mode = "com.cloudbees.hudson.plugins.folder.Folder";
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMode() {
        return mode;
    }

}
