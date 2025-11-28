package org.example.flow.engine.node;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public abstract class TaskNode {

    private boolean needStop = false;

    private String id;

    private List<String> dependencies = Collections.emptyList();

    public Map<String, TaskData<?>> input;

    public List<TaskData<?>> output;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public boolean isNeedStop() {
        return needStop;
    }

    public void setNeedStop(boolean needStop) {
        this.needStop = needStop;
    }

    public List<String> getDependencies() {
        return dependencies;
    }

    public void setDependencies(List<String> dependencies) {
        if (dependencies == null) {
            this.dependencies = Collections.emptyList();
        } else {
            this.dependencies = List.copyOf(dependencies);
        }
    }

    public final void execute() {
        this.output = task(this.input);
    }

    /**
     * Legacy method signature for backward compatibility.
     *
     * @param input the input data map
     * @return list of output TaskData
     */
    public abstract List<TaskData<?>> task(Map<String, TaskData<?>> input);
}
