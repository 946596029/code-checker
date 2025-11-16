package org.example.flow.engine.node;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class TaskData<T> {

    private final Class<T> type;
    private final String source;
    private final long ts;
    private final T payload;

    @JsonCreator
    public TaskData(
        @JsonProperty("type") Class<T> type,
        @JsonProperty("source") String source,
        @JsonProperty("ts") long ts,
        @JsonProperty("payload") T payload
    ) {
        this.type = type;
        this.source = source;
        this.ts = ts;
        this.payload = payload;
    }

    public Class<T> getType() {
        return type;
    }

    public String getSource() {
        return source;
    }

    public long getTs() {
        return ts;
    }

    public T getPayload() {
        return payload;
    }
}
