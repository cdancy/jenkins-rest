package com.cdancy.jenkins.rest.domain.job;

import java.util.List;

import com.google.auto.value.AutoValue;
import org.jclouds.json.SerializedNames;

@AutoValue
public abstract class PipelineNode {

    public abstract String name();

    public abstract String status();

    public abstract long startTimeMillis();

    public abstract long durationTimeMillis();

    public abstract List<StageFlowNode> stageFlowNodes();

    PipelineNode() {
    }

    @SerializedNames({ "name", "status", "startTimeMillis", "durationTimeMillis", "stageFlowNodes" })
    public static PipelineNode create(String name, String status, long startTimeMillis, long durationTimeMillis, List<StageFlowNode> stageFlowNodes) {
        return new AutoValue_PipelineNode(name, status, startTimeMillis, durationTimeMillis, stageFlowNodes);
    }
}
