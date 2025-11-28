package org.example.code.checker.checker.common;

import org.example.code.checker.checker.markdown.parser.ast.SourceRange;

/**
 * Generic error description used by markdown checking tasks.
 * <p>
 * This type is intended to be wrapped inside {@code TaskData<CheckError>}
 * when a task detects a structural or semantic problem in the document.
 */
public final class CheckError {

    /**
     * Error severity level.
     */
    public enum Severity {
        INFO,
        WARNING,
        ERROR
    }

    /**
     * Human readable error message.
     */
    private final String message;

    /**
     * Severity of this error.
     */
    private final Severity severity;

    public CheckError(
        String message,
        Severity severity
    ) {
        this.message = message;
        this.severity = severity == null ? Severity.ERROR : severity;
    }

    public String getMessage() {
        return message;
    }

    public Severity getSeverity() {
        return severity;
    }

    /**
     * Builder for {@link CheckError} to simplify creation of error objects.
     */
    public static class Builder {
        private String message;
        private Severity severity = Severity.ERROR; // 默认严重级别为 ERROR

        public Builder message(String message) {
            this.message = message;
            return this;
        }

        public Builder severity(Severity severity) {
            this.severity = severity;
            return this;
        }

        public CheckError build() {
            return new CheckError(
                    message,
                    severity);
        }
    }

    // 添加静态 builder() 方法
    public static Builder builder() {
        return new Builder();
    }
}


