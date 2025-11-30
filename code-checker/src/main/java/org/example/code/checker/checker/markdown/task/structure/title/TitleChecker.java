package org.example.code.checker.checker.markdown.task.structure.title;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

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

        // Get originalDocument from input
        TaskData<?> documentData = input.get("originalDocument");
        if (documentData == null) {
            throw new IllegalArgumentException("Missing required input: originalDocument");
        }

        @SuppressWarnings("unchecked")
        TreeNode<MdAstNode> document = (TreeNode<MdAstNode>) documentData.getPayload();

        // Get fileId for error reporting (optional)
        TaskData<?> fileIdData = input.get("fileId");
        String fileId = fileIdData != null ? (String) fileIdData.getPayload() : null;

        // Find level 1 heading (title)
        Optional<TreeNode<MdAstNode>> titleHeading = findHeadingByLevel(document, 1);

        if (titleHeading.isEmpty()) {
            String message = buildErrorMessage(
                    "TitleChecker.MissingTitle",
                    "Document is missing required Title (level 1 heading)",
                    fileId,
                    null,
                    null,
                    "DOCUMENT");
            errors.add(CheckError.builder()
                    .message(message)
                    .severity(CheckError.Severity.ERROR)
                    .build());
            setErrorList(errors);
            setNeedStop(true);
            return null;
        }

        TreeNode<MdAstNode> titleNode = titleHeading.get();
        
        // Extract title text
        String title = extractTextFromNode(titleNode);
        if (title == null || title.trim().isEmpty()) {
            MdAstNode titleNodeData = titleNode.getData();
            String message = buildErrorMessage(
                    "TitleChecker.MissingTitle",
                    "Title section must start with a level 1 heading",
                    fileId,
                    titleNodeData != null ? titleNodeData.getSourceRange() : null,
                    titleNodeData != null ? titleNodeData.getNodeId() : null,
                    titleNodeData != null ? titleNodeData.getNodeType().name() : null);
            errors.add(CheckError.builder()
                    .message(message)
                    .severity(CheckError.Severity.ERROR)
                    .build());
            setErrorList(errors);
            setNeedStop(true);
            return null;
        }

        // Find the next heading (any level) after title heading
        TreeNode<MdAstNode> nextHeading = findNextHeading(titleNode, null);

        // Collect all nodes from title heading to next heading (excluding next heading)
        List<TreeNode<MdAstNode>> titleSection = collectNodesBetween(titleNode, nextHeading);

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
     * Finds a heading node by level.
     *
     * @param document The document root node
     * @param level    The heading level (1-6)
     * @return Optional containing the found heading node, or empty if not found
     */
    private Optional<TreeNode<MdAstNode>> findHeadingByLevel(TreeNode<MdAstNode> document, int level) {
        List<TreeNode<MdAstNode>> headingNodes = document.query()
                .all()
                .ofType(MdAstNode.class)
                .filter(MdAstNode.class, data -> data != null && data.getNodeType() == MdNodeType.HEADING)
                .list();

        return headingNodes.stream()
                .filter(node -> {
                    MdAstNode data = node.getData();
                    Heading heading = (Heading) data.getCommonMarkNode();
                    return heading != null && heading.getLevel() == level;
                })
                .findFirst();
    }

    /**
     * Finds the next heading node after the given node.
     *
     * @param startNode The starting node
     * @param level     Optional heading level to match. If null, matches any heading level
     * @return The next heading node, or null if not found
     */
    private TreeNode<MdAstNode> findNextHeading(TreeNode<MdAstNode> startNode, Integer level) {
        TreeNode<MdAstNode> parent = startNode.getParent();
        if (parent == null) {
            return null;
        }

        List<TreeNode<MdAstNode>> siblings = parent.getChildren();
        int startIndex = siblings.indexOf(startNode);

        for (int i = startIndex + 1; i < siblings.size(); i++) {
            MdAstNode siblingData = siblings.get(i).getData();
            if (siblingData.getNodeType() == MdNodeType.HEADING) {
                if (level == null) {
                    // Match any heading level
                    return siblings.get(i);
                } else {
                    // Match specific level
                    Heading heading = (Heading) siblingData.getCommonMarkNode();
                    if (heading != null && heading.getLevel() == level) {
                        return siblings.get(i);
                    }
                }
            }
        }

        return null;
    }

    /**
     * Collects all nodes between startNode and endNode (excluding endNode).
     *
     * @param startNode The starting node (inclusive)
     * @param endNode   The ending node (exclusive). If null, collects until the end of parent's children
     * @return List of nodes between startNode and endNode
     */
    private List<TreeNode<MdAstNode>> collectNodesBetween(TreeNode<MdAstNode> startNode,
            TreeNode<MdAstNode> endNode) {
        List<TreeNode<MdAstNode>> content = new ArrayList<>();
        TreeNode<MdAstNode> parent = startNode.getParent();

        if (parent != null) {
            List<TreeNode<MdAstNode>> siblings = parent.getChildren();
            int startIndex = siblings.indexOf(startNode);
            int endIndex = endNode != null ? siblings.indexOf(endNode) : siblings.size();

            for (int i = startIndex; i < endIndex; i++) {
                content.add(siblings.get(i));
            }
        } else {
            // If no parent, just add the start node itself
            content.add(startNode);
        }

        return content;
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
