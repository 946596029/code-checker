package org.example.code.checker.checker;

import org.example.code.checker.checker.common.CheckContext;
import org.example.code.checker.checker.common.CheckError;
import org.example.code.checker.checker.common.CheckRule;
import org.example.code.checker.checker.markdown.parser.ast.MdAstNode;
import org.example.code.checker.checker.utils.TreeNode;
import org.example.flow.engine.node.TaskData;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Rule-based checker base class.
 * Uses Template Method pattern to define the check flow.
 */
public abstract class RuleBasedChecker extends Checker {

    /**
     * Template method: defines the check flow.
     */
    @Override
    public final List<TaskData<?>> task(Map<String, TaskData<?>> input) {
        // 1. Extract context
        CheckContext context = extractContext(input);

        // 2. Get rule list
        List<CheckRule> rules = getRules();

        // 3. Execute all rules
        List<CheckError> errors = new ArrayList<>();
        for (CheckRule rule : rules) {
            errors.addAll(rule.check(context));
        }

        // 4. Unified error handling
        return handleErrors(errors);
    }

    /**
     * Extracts check context (part of template method).
     */
    protected CheckContext extractContext(Map<String, TaskData<?>> input) {
        TaskData<?> documentData = input.get("originalDocument");
        if (documentData == null) {
            throw new IllegalArgumentException("Missing required input: originalDocument");
        }

        @SuppressWarnings("unchecked")
        TreeNode<MdAstNode> document = (TreeNode<MdAstNode>) documentData.getPayload();

        TaskData<?> fileIdData = input.get("fileId");
        String fileId = fileIdData != null ? (String) fileIdData.getPayload() : null;

        return new CheckContext(document, fileId, input, getCheckerName());
    }

    /**
     * Unified error handling.
     */
    protected List<TaskData<?>> handleErrors(List<CheckError> errors) {
        if (!errors.isEmpty()) {
            setErrorList(errors);
            setNeedStop(true);
            return null;
        }
        return buildSuccessResult();
    }

    /**
     * Builds success result (can be overridden by subclasses).
     */
    protected List<TaskData<?>> buildSuccessResult() {
        return new ArrayList<>();
    }

    /**
     * Gets checker name (for error message prefix).
     */
    protected abstract String getCheckerName();

    /**
     * Gets rule list (implemented by subclasses).
     */
    protected abstract List<CheckRule> getRules();
}

