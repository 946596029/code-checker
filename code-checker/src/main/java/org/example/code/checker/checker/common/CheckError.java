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
     * Logical identifier of the rule that produced this error,
     * for example {@code "FrontMatter.Missing"}.
     */
    private final String ruleId;

    /**
     * Human readable error message.
     */
    private final String message;

    /**
     * Severity of this error.
     */
    private final Severity severity;

    /**
     * Optional file identifier, usually the same as the one used
     * when generating the AST.
     */
    private final String fileId;

    /**
     * Optional source range where the error occurred.
     */
    private final SourceRange range;

    /**
     * Optional domain node identifier associated with this error.
     */
    private final String nodeId;

    /**
     * Optional domain node type associated with this error
     * (for example {@code "DOCUMENT"}, {@code "HEADING"} and so on).
     */
    private final String nodeType;

    public CheckError(
        String ruleId,
        String message,
        Severity severity,
        String fileId,
        SourceRange range,
        String nodeId,
        String nodeType
    ) {
        this.ruleId = ruleId;
        this.message = message;
        this.severity = severity == null ? Severity.ERROR : severity;
        this.fileId = fileId;
        this.range = range;
        this.nodeId = nodeId;
        this.nodeType = nodeType;
    }

    public String getRuleId() {
        return ruleId;
    }

    public String getMessage() {
        return message;
    }

    public Severity getSeverity() {
        return severity;
    }

    public String getFileId() {
        return fileId;
    }

    public SourceRange getRange() {
        return range;
    }

    public String getNodeId() {
        return nodeId;
    }

    public String getNodeType() {
        return nodeType;
    }

    /**
     * Builder for {@link CheckError} to simplify creation of error objects.
     */
    public static class Builder {
        private String ruleId;
        private String message;
        private Severity severity = Severity.ERROR; // 默认严重级别为 ERROR
        private String fileId;
        private SourceRange range;
        private String nodeId;
        private String nodeType;

        public Builder ruleId(String ruleId) {
            this.ruleId = ruleId;
            return this;
        }

        public Builder message(String message) {
            this.message = message;
            return this;
        }

        public Builder severity(Severity severity) {
            this.severity = severity;
            return this;
        }

        public Builder fileId(String fileId) {
            this.fileId = fileId;
            return this;
        }

        public Builder range(SourceRange range) {
            this.range = range;
            return this;
        }

        public Builder nodeId(String nodeId) {
            this.nodeId = nodeId;
            return this;
        }

        public Builder nodeType(String nodeType) {
            this.nodeType = nodeType;
            return this;
        }

        public CheckError build() {
            return new CheckError(
                    ruleId,
                    message,
                    severity,
                    fileId,
                    range,
                    nodeId,
                    nodeType);
        }
    }

    // 添加静态 builder() 方法
    public static Builder builder() {
        return new Builder();
    }
}


