package org.example.flow.engine.node;

import com.fasterxml.jackson.databind.JsonNode;

import java.util.List;
import java.util.Map;

public abstract class TaskNode {
    public Map<String, TaskData<?>> input;
    public List<TaskData<?>> output;

    public abstract List<TaskData<?>> task(Map<String, TaskData<?>> input);
}
