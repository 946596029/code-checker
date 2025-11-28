package org.example.code.checker.checker.markdown.task.structure.title;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.commonmark.node.Heading;
import org.example.code.checker.checker.Checker;
import org.example.code.checker.checker.common.CheckError;
import org.example.code.checker.checker.markdown.parser.ast.MdAstNode;
import org.example.code.checker.checker.markdown.parser.ast.MdNodeType;
import org.example.code.checker.checker.utils.TreeNode;
import org.example.flow.engine.node.TaskData;

public class TitleChecker extends Checker {
    @Override
    public List<TaskData<?>> task(Map<String, TaskData<?>> input) {
        List<CheckError> errors = new ArrayList<>();

        // Get titleSection from input
        TaskData<?> titleSectionData = input.get("titleSection");
        if (titleSectionData == null) {
            throw new IllegalArgumentException("Missing required input: titleSection");
        }

        @SuppressWarnings("unchecked")
        List<TreeNode<MdAstNode>> titleSection = (List<TreeNode<MdAstNode>>) titleSectionData.getPayload();

        if (titleSection == null || titleSection.isEmpty()) {
            throw new IllegalArgumentException("titleSection is empty");
        }

        // Get fileId for error reporting (optional)
        TaskData<?> fileIdData = input.get("fileId");
        String fileId = fileIdData != null ? (String) fileIdData.getPayload() : null;

        // Extract title from first node (should be level 1 heading)
        String title = null;
        TreeNode<MdAstNode> firstNode = titleSection.get(0);
        MdAstNode firstNodeData = firstNode.getData();

        if (firstNodeData != null && firstNodeData.getNodeType() == MdNodeType.HEADING) {
            Heading heading = (Heading) firstNodeData.getCommonMarkNode();
            if (heading != null && heading.getLevel() == 1) {
                title = extractTextFromNode(firstNode);
            }
        }

        if (title == null || title.trim().isEmpty()) {
            String message = buildErrorMessage(
                    "TitleChecker.MissingTitle",
                    "Title section must start with a level 1 heading",
                    fileId,
                    firstNodeData != null ? firstNodeData.getSourceRange() : null,
                    firstNodeData != null ? firstNodeData.getNodeId() : null,
                    firstNodeData != null ? firstNodeData.getNodeType().name() : null);
            errors.add(CheckError.builder()
                    .message(message)
                    .severity(CheckError.Severity.ERROR)
                    .build());
        }

        // Extract description and other paragraphs from remaining nodes
        String description = null;
        List<String> otherParagraphs = new ArrayList<>();

        // Process remaining nodes in titleSection (skip first node which is the heading)
        boolean foundFirstParagraph = false;
        for (int i = 1; i < titleSection.size(); i++) {
            TreeNode<MdAstNode> node = titleSection.get(i);
            MdAstNode nodeData = node.getData();

            if (nodeData != null && nodeData.getNodeType() == MdNodeType.PARAGRAPH) {
                String paragraphText = extractTextFromNode(node);
                if (paragraphText != null && !paragraphText.trim().isEmpty()) {
                    if (!foundFirstParagraph) {
                        description = paragraphText.trim();
                        foundFirstParagraph = true;
                    } else {
                        otherParagraphs.add(paragraphText.trim());
                    }
                }
            }
        }

        // Create Title object
        Title titleObj = new Title(
                title != null ? title.trim() : "",
                description != null ? description : "",
                otherParagraphs);

        // Return result
        List<TaskData<?>> output = new ArrayList<>();
        if (errors.size() > 0) {
            setErrorList(errors);
            setNeedStop(true);
            return null;
        }

        output.add(new TaskData<>("titleResult", titleObj));
        return output;
    }

    /**
     * Extracts text content from a node by traversing its children.
     */
    private String extractTextFromNode(TreeNode<MdAstNode> node) {
        if (node == null) {
            return null;
        }
        StringBuilder sb = new StringBuilder();
        extractTextRecursive(node, sb);
        return sb.toString().trim();
    }

    /**
     * Recursively extracts text from a node and its children.
     */
    private void extractTextRecursive(TreeNode<MdAstNode> node, StringBuilder sb) {
        if (node == null) {
            return;
        }
        MdAstNode data = node.getData();
        if (data == null) {
            return;
        }

        if (data.getNodeType() == MdNodeType.TEXT) {
            if (data.getText() != null) {
                sb.append(data.getText());
            }
        } else if (data.getNodeType() == MdNodeType.CODE) {
            if (data.getText() != null) {
                sb.append(data.getText());
            }
        }

        List<TreeNode<MdAstNode>> children = node.getChildren();
        if (children != null) {
            for (TreeNode<MdAstNode> child : children) {
                extractTextRecursive(child, sb);
            }
        }
    }
}
