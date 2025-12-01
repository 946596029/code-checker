package org.example.code.checker.checker.common;

import org.example.code.checker.checker.markdown.parser.ast.MdAstNode;
import org.example.code.checker.checker.utils.TreeNode;
import org.example.flow.engine.node.TaskData;

import java.util.Map;

/**
 * Check context that encapsulates all information needed for checking.
 */
public class CheckContext {
    private final TreeNode<MdAstNode> document;
    private final String fileId;
    private final Map<String, TaskData<?>> input;
    private final String checkerName;

    public CheckContext(TreeNode<MdAstNode> document, String fileId,
                       Map<String, TaskData<?>> input, String checkerName) {
        this.document = document;
        this.fileId = fileId;
        this.input = input;
        this.checkerName = checkerName;
    }

    public TreeNode<MdAstNode> getDocument() {
        return document;
    }

    public String getFileId() {
        return fileId;
    }

    public Map<String, TaskData<?>> getInput() {
        return input;
    }

    public String getCheckerName() {
        return checkerName;
    }

    /**
     * Gets data of specified type from input.
     */
    @SuppressWarnings("unchecked")
    public <T> T getInputData(String key, Class<T> type) {
        TaskData<?> data = input.get(key);
        return data != null ? (T) data.getPayload() : null;
    }
}

