package org.example.code.checker.checker;

import org.example.code.checker.checker.common.CheckError;
import org.example.flow.engine.node.TaskNode;

import java.util.List;

public abstract class Checker extends TaskNode {
    private List<CheckError> errorList;

    public void setErrorList(List<CheckError> errorList) {
        this.errorList = errorList;
    }

    public List<CheckError> getErrorList() {
        return this.errorList;
    }

    /**
     * Builds a comprehensive error message that includes rule ID, file ID,
     * location, node information, and the base error message.
     *
     * @param ruleId      The rule identifier (e.g.,
     *                    "FrontMatterChecker.InvalidProperties")
     * @param baseMessage The base error message
     * @param fileId      The file identifier
     * @param range       Optional source range for location information
     * @param nodeId      Optional node identifier
     * @param nodeType    Optional node type string
     * @return A formatted error message containing all available information
     */
    protected String buildErrorMessage(
            String ruleId,
            String baseMessage,
            String fileId,
            org.example.code.checker.checker.markdown.parser.ast.SourceRange range,
            String nodeId,
            String nodeType) {
        return String.format(
                "%s%s%s%s%s",
                (ruleId != null && !ruleId.isEmpty()) ? String.format("[%s] ", ruleId) : "",
                baseMessage != null ? baseMessage : "",
                (fileId != null && !fileId.isEmpty()) ? String.format(" (File: %s)", fileId) : "",
                (range != null) ? String.format(" [Line: %d, Column: %d]", range.getLine(), range.getColumn()) : "",
                buildNodePart(nodeId, nodeType));
    }

    /**
     * Builds the node information part of the error message.
     *
     * @param nodeId   Optional node identifier
     * @param nodeType Optional node type string
     * @return Formatted node part, or empty string if both are null/empty
     */
    protected String buildNodePart(String nodeId, String nodeType) {
        boolean hasNodeId = nodeId != null && !nodeId.isEmpty();
        boolean hasNodeType = nodeType != null && !nodeType.isEmpty();

        if (hasNodeId && hasNodeType) {
            return String.format(" (NodeId: %s, NodeType: %s)", nodeId, nodeType);
        } else if (hasNodeId) {
            return String.format(" (NodeId: %s)", nodeId);
        } else if (hasNodeType) {
            return String.format(" (NodeType: %s)", nodeType);
        }
        return "";
    }
}
