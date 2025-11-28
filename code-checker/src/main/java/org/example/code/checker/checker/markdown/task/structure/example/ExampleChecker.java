package org.example.code.checker.checker.markdown.task.structure.example;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.example.code.checker.checker.Checker;
import org.example.code.checker.checker.common.CheckError;
import org.example.code.checker.checker.markdown.parser.ast.MdAstNode;
import org.example.code.checker.checker.utils.TreeNode;
import org.example.flow.engine.node.TaskData;

public class ExampleChecker extends Checker {
    
    @Override
    public List<TaskData<?>> task(Map<String, TaskData<?>> input) {
        List<CheckError> errors = new ArrayList<>();
        List<TreeNode<MdAstNode>> exampleUsageSection = checkParameters(input);

        TreeNode<MdAstNode> exampleTitle =  exampleUsageSection.get(0);
        if


        return null;
    }

    private List<TreeNode<MdAstNode>> checkParameters(Map<String, TaskData<?>> input) {
        // Get frontMatterSection from input
        TaskData<?> exampleUsageSectionData = input.get("exampleUsageSection");
        if (exampleUsageSectionData == null) {
            throw new IllegalArgumentException("Missing required input: exampleUsageSection");
        }

        @SuppressWarnings("unchecked")
        List<TreeNode<MdAstNode>> exampleUsageSection = (List<TreeNode<MdAstNode>>) exampleUsageSectionData.getPayload();

        if (exampleUsageSection == null || exampleUsageSection.isEmpty()) {
            throw new IllegalArgumentException("exampleUsageSection is empty");
        }

        return exampleUsageSection;
    }
}
