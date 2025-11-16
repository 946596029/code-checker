package org.example.flow.engine.node;

import com.fasterxml.jackson.databind.JsonNode;

import java.util.Map;

public abstract class TaskNode {
    public Map<String, TaskData<?>> input;
    public TaskData output;

    public abstract TaskData task(Map<String, TaskData<?>> input);
}
