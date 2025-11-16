package org.example.flow.engine.node;

import java.util.Map;

public final class TaskDataUtils {

    private TaskDataUtils() {}

    public static <T> T getPayload(
        Map<String, TaskData<?>> input,
        String key,
        Class<T> expectedType
    ) {
        TaskData<?> data = input.get(key);
        if (data == null) {
            throw new IllegalArgumentException("input '" + key + "' is missing");
        }
        if (!expectedType.isAssignableFrom(data.getType())) {
            throw new IllegalArgumentException(
                "input '" + key + "' type mismatch, expected " 
                    + expectedType.getName() 
                    + ", actual " 
                    + data.getType().getName()
            );
        }
        @SuppressWarnings("unchecked")
        T payload = (T) data.getPayload();
        return payload;
    }
}