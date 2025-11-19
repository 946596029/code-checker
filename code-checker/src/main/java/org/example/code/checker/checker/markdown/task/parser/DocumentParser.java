package org.example.code.checker.checker.markdown.task.parser;

import org.example.code.checker.checker.markdown.domain.MdDomain;
import org.example.code.checker.checker.markdown.domain.builder.MarkdownDomainBuilder;
import org.example.code.checker.checker.markdown.parser.MdAstGenerator;
import org.example.code.checker.checker.markdown.parser.ast.MdAstNode;
import org.example.code.checker.checker.utils.TreeNode;
import org.example.flow.engine.node.TaskData;
import org.example.flow.engine.node.TaskDataUtils;
import org.example.flow.engine.node.TaskNode;

import java.util.List;
import java.util.Map;

public class DocumentParser extends TaskNode {

    @Override
    public List<TaskData<?>> task(Map<String, TaskData<?>> input) {
        if (input == null || input.isEmpty()) {
            throw new IllegalArgumentException("input is null or empty");
        }

        String rawCode = TaskDataUtils.getPayload(input, "rawCode", String.class);
        String fileId = TaskDataUtils.getPayload(input, "fileId", String.class);

        MdAstNode root = MdAstGenerator.generateStandardAst(rawCode, fileId);
        TreeNode<MdDomain> document = MarkdownDomainBuilder.buildDocument(root);

        return List.of(
            new TaskData<>(
                TreeNode.class,
                DocumentParser.class.getSimpleName(),
                System.currentTimeMillis(),
                document));
    }
}