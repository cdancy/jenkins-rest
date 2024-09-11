package com.cdancy.jenkins.rest.domain.job;

import com.google.auto.value.AutoValue;
import org.jclouds.json.SerializedNames;

/**
 * Author: kun.tang@daocloud.io
 * Date:2024/9/11
 * Time:12:45
 */

@AutoValue
public abstract class JobExisted {

    public abstract  String msg();
    public abstract  boolean existed();

    JobExisted(){}

    public static JobExisted create(String msg){
        boolean existed = false;
        if(msg!=null&&msg.contains("error")){
            msg = msg.replace("<div class=\"error\">","").replace("</div>","");
            existed = true;
        }else {
            msg = "";
        }
        return new AutoValue_JobExisted(msg,existed);
    }

}
