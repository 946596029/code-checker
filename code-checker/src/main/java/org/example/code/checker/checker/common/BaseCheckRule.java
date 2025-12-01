package org.example.code.checker.checker.common;

import org.example.code.checker.checker.markdown.parser.ast.SourceRange;

/**
 * Base class for check rules, providing common error creation methods.
 */
public abstract class BaseCheckRule implements CheckRule {

    /**
     * Creates a check error with full context.
     */
    protected CheckError createError(CheckContext context, String errorCode,
                                    String message, SourceRange range,
                                    String nodeId, String nodeType) {
        String fullMessage = buildErrorMessage(
                context.getCheckerName() + "." + errorCode,
                message,
                context.getFileId(),
                range,
                nodeId,
                nodeType
        );
        return CheckError.builder()
                .message(fullMessage)
                .severity(CheckError.Severity.ERROR)
                .build();
    }

    /**
     * Builds error message. This method delegates to Checker's buildErrorMessage.
     * Subclasses can override to provide custom error message building.
     */
    protected String buildErrorMessage(String ruleId, String baseMessage,
                                      String fileId, SourceRange range,
                                      String nodeId, String nodeType) {
        // Build error message similar to Checker.buildErrorMessage
        StringBuilder sb = new StringBuilder();
        if (ruleId != null && !ruleId.isEmpty()) {
            sb.append("[").append(ruleId).append("] ");
        }
        if (baseMessage != null) {
            sb.append(baseMessage);
        }
        if (fileId != null && !fileId.isEmpty()) {
            sb.append(" (File: ").append(fileId).append(")");
        }
        if (range != null) {
            sb.append(" [Line: ").append(range.getLine())
                    .append(", Column: ").append(range.getColumn()).append("]");
        }
        boolean hasNodeId = nodeId != null && !nodeId.isEmpty();
        boolean hasNodeType = nodeType != null && !nodeType.isEmpty();
        if (hasNodeId && hasNodeType) {
            sb.append(" (NodeId: ").append(nodeId).append(", NodeType: ").append(nodeType).append(")");
        } else if (hasNodeId) {
            sb.append(" (NodeId: ").append(nodeId).append(")");
        } else if (hasNodeType) {
            sb.append(" (NodeType: ").append(nodeType).append(")");
        }
        return sb.toString();
    }
}

