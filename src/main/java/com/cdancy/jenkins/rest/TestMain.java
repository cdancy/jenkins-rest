package com.cdancy.jenkins.rest;

import com.cdancy.jenkins.rest.domain.job.Folder;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

/**
 * Author: kun.tang@daocloud.io
 * Date:2024/9/11
 * Time:11:33
 */

public class TestMain {

    private static final String xml = "<?xml version='1.1' encoding='UTF-8'?>\n" +
        "<flow-definition plugin=\"workflow-job@1436.vfa_244484591f\">\n" +
        "  <actions/>\n" +
        "  <description></description>\n" +
        "  <keepDependencies>false</keepDependencies>\n" +
        "  <properties>\n" +
        "    <com.dabsquared.gitlabjenkins.connection.GitLabConnectionProperty plugin=\"gitlab-plugin@1.8.1\">\n" +
        "      <gitLabConnection></gitLabConnection>\n" +
        "      <jobCredentialId></jobCredentialId>\n" +
        "      <useAlternativeCredential>false</useAlternativeCredential>\n" +
        "    </com.dabsquared.gitlabjenkins.connection.GitLabConnectionProperty>\n" +
        "    <hudson.plugins.throttleconcurrents.ThrottleJobProperty plugin=\"throttle-concurrents@2.14\">\n" +
        "      <maxConcurrentPerNode>0</maxConcurrentPerNode>\n" +
        "      <maxConcurrentTotal>0</maxConcurrentTotal>\n" +
        "      <categories class=\"java.util.concurrent.CopyOnWriteArrayList\"/>\n" +
        "      <throttleEnabled>false</throttleEnabled>\n" +
        "      <throttleOption>project</throttleOption>\n" +
        "      <limitOneJobWithMatchingParams>false</limitOneJobWithMatchingParams>\n" +
        "      <paramsToUseForLimit></paramsToUseForLimit>\n" +
        "    </hudson.plugins.throttleconcurrents.ThrottleJobProperty>\n" +
        "  </properties>\n" +
        "  <definition class=\"org.jenkinsci.plugins.workflow.cps.CpsFlowDefinition\" plugin=\"workflow-cps@3908.vd6b_b_5a_a_54010\">\n" +
        "    <script>node { \n" +
        "    def pipelineId = &quot;70783e1713c3438699d3cd8b98d0e556&quot;\n" +
        "\tstage(&apos;Git Clone&apos;) {\n" +
        "\t\tcheckout([$class: &apos;GitSCM&apos;, branches: [[name: &apos;master-jdk17&apos;]], extensions: [[$class: &apos;SubmoduleOption&apos;, disableSubmodules: false, parentCredentials: true, recursiveSubmodules: true, reference: &apos;&apos;, trackingSubmodules: true]], userRemoteConfigs: [[credentialsId: &apos;git-395&apos;, url: &apos;http://10.50.160.62:42677/dos/dso-environment.git&apos;]]]) \n" +
        "\t\techo pipelineId\n" +
        "\t\techo &quot;pipelineId=&quot; + pipelineId\n" +
        "\t}\n" +
        " \n" +
        "\t\n" +
        " \n" +
        "}</script>\n" +
        "    <sandbox>true</sandbox>\n" +
        "  </definition>\n" +
        "  <triggers/>\n" +
        "  <disabled>false</disabled>\n" +
        "</flow-definition>";
    public static void main(String[] args) {
        JenkinsClient client = JenkinsClient.builder()
            .endPoint("http://10.50.160.1:39931")//.endPoint("http://10.50.160.1:39931") // Optional. Defaults to http://127.0.0.1:8080 http://10.50.160.1:33592/
            .credentials("admin:dangerous") // Optional.
            .build();

        var result = client.api().jobsApi().checkJobName("yudao", "system");
        System.out.println(result);
        //System.out.println(new String(result.getBytes(Charset.forName("utf-8"))));
        var job = client.api().jobsApi().jobInfo("","yudao");
        //client.api().jobsApi().
        System.out.println(job);

        var status = client.api().jobsApi().createFolder("yudao","yudaoq");
        System.out.println(status.value());

/*
var jobs = client.api().jobsApi().jobList("");
jobs.jobs().forEach(job -> {
            System.out.println("name:"+job.name());
            System.out.println("clazz:"+job.clazz());
            System.out.println("url:"+job.url());
        });*/
    }
}
