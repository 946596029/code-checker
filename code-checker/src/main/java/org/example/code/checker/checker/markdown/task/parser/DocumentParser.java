package org.example.code.checker.checker.markdown.task.parser;

import org.example.code.checker.checker.markdown.domain.builder.standard.MarkdownDomainBuilder;
import org.example.code.checker.checker.markdown.domain.standard.block.Document;
import org.example.code.checker.checker.markdown.parser.MdAstGenerator;
import org.example.code.checker.checker.markdown.parser.ast.MdAstNode;
import org.example.flow.engine.node.TaskData;
import org.example.flow.engine.node.TaskDataUtils;
import org.example.flow.engine.node.TaskNode;

import java.util.Map;

public class DocumentParser extends TaskNode {

    @Override
    public TaskData task(Map<String, TaskData<?>> input) {
        if (input == null || input.isEmpty()) {
            throw new IllegalArgumentException("input is null or empty");
        }

        String rawCode = TaskDataUtils.getPayload(input, "rawCode", String.class);
        String fileId = TaskDataUtils.getPayload(input, "fileId", String.class);

        MdAstNode root = MdAstGenerator.generateStandardAst(rawCode, fileId);
        Document document = MarkdownDomainBuilder.buildDocument(root);

        return new TaskData<>(
            Document.class,
            Document.class.getSimpleName(),
            System.currentTimeMillis(), 
            document
        );
    }
}