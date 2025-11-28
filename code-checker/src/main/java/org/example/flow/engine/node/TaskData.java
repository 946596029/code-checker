package org.example.flow.engine.node;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class TaskData<T> {

    private final String name;
    private final T payload;

    @JsonCreator
    public TaskData(
            @JsonProperty("name") String name,
        @JsonProperty("payload") T payload
    ) {
        this.name = name;
        this.payload = payload;
    }

    public String getName() {
        return name;
    }

    public T getPayload() {
        return payload;
    }
}
