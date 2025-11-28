package org.example.code.checker.checker.markdown.task.structure;

import org.commonmark.node.Heading;
import org.example.code.checker.checker.common.CheckError;
import org.example.code.checker.checker.Checker;
import org.example.code.checker.checker.markdown.parser.ast.MdAstNode;
import org.example.code.checker.checker.markdown.parser.ast.MdNodeType;
import org.example.code.checker.checker.utils.TreeNode;
import org.example.flow.engine.node.TaskData;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class StructureChecker extends Checker {

    @Override
    public List<TaskData<?>> task(Map<String, TaskData<?>> input) {
        List<CheckError> errors = new ArrayList<>();

        TaskData<?> documentData = input.get("originalDocument");
        if (documentData == null) {
            throw new IllegalArgumentException("Missing required input: originalDocument");
        }
        @SuppressWarnings("unchecked")
        TreeNode<MdAstNode> document = (TreeNode<MdAstNode>) documentData.getPayload();

        TaskData<?> fileIdData = input.get("fileId");
        if (fileIdData == null) {
            throw new IllegalArgumentException("Missing required input: fileId");
        }
        String fileId = (String) fileIdData.getPayload();

        // Check required sections and collect results
        List<TreeNode<MdAstNode>> frontMatterSection = checkFrontMatter(document, fileId, errors);
        List<TreeNode<MdAstNode>> titleSection = checkTitle(document, fileId, errors);
        List<TreeNode<MdAstNode>> exampleUsageSection = checkExampleUsage(document, fileId, errors);
        List<TreeNode<MdAstNode>> argumentReferenceSection = checkArgumentReference(document, fileId, errors);
        List<TreeNode<MdAstNode>> attributeReferenceSection = checkAttributeReference(document, fileId, errors);

        // Convert errors to TaskData list
        List<TaskData<?>> result = new ArrayList<>();
        if (errors.size() > 0) {
            setErrorList(errors);
            setNeedStop(true);
            return null;
        }

        // Add all section results to output
        if (frontMatterSection != null) {
            result.add(new TaskData<>("frontMatterSection", frontMatterSection));
        }
        if (titleSection != null) {
            result.add(new TaskData<>("titleSection", titleSection));
        }
        if (exampleUsageSection != null) {
            result.add(new TaskData<>("exampleUsageSection", exampleUsageSection));
        }
        if (argumentReferenceSection != null) {
            result.add(new TaskData<>("argumentReferenceSection", argumentReferenceSection));
        }
        if (attributeReferenceSection != null) {
            result.add(new TaskData<>("attributeReferenceSection", attributeReferenceSection));
        }

        return result;
    }

    private List<TreeNode<MdAstNode>> checkFrontMatter(TreeNode<MdAstNode> document, String fileId,
            List<CheckError> errors) {
        Optional<TreeNode<MdAstNode>> frontMatter = document.query()
                .children()
                .ofType(MdAstNode.class)
                .filter(MdAstNode.class, data -> data != null && data.getNodeType() == MdNodeType.FRONT_MATTER)
                .first();

        if (frontMatter.isEmpty()) {
            String message = buildErrorMessage(
                    "StructureChecker.MissingFrontMatter",
                    "Document is missing required FrontMatter section",
                    fileId,
                    null,
                    null,
                    "DOCUMENT");
            errors.add(CheckError.builder()
                    .message(message)
                    .severity(CheckError.Severity.ERROR)
                    .build());
            return null;
        }

        return List.of(frontMatter.get());
    }

    private List<TreeNode<MdAstNode>> checkTitle(TreeNode<MdAstNode> document, String fileId, List<CheckError> errors) {
        // Check for level 1 heading (title name)
        Optional<TreeNode<MdAstNode>> titleHeading = findHeadingByLevelAndText(document, 1, null);

        if (titleHeading.isEmpty()) {
            String message = buildErrorMessage(
                    "StructureChecker.MissingTitle",
                    "Document is missing required Title (level 1 heading)",
                    fileId,
                    null,
                    null,
                    "DOCUMENT");
            errors.add(CheckError.builder()
                    .message(message)
                    .severity(CheckError.Severity.ERROR)
                    .build());
            return null;
        }

        // Find the next heading (any level) after title heading
        TreeNode<MdAstNode> titleNode = titleHeading.get();
        TreeNode<MdAstNode> nextHeading = findNextHeading(titleNode, 2);

        // Collect all nodes from title heading to next heading (excluding next heading)
        return collectNodesBetween(titleNode, nextHeading);
    }

    private List<TreeNode<MdAstNode>> checkExampleUsage(TreeNode<MdAstNode> document, String fileId,
            List<CheckError> errors) {
        // Check for "Example Usage" heading (level 2)
        Optional<TreeNode<MdAstNode>> exampleHeading = findHeadingByLevelAndText(document, 2, "Example Usage");

        if (exampleHeading.isEmpty()) {
            String message = buildErrorMessage(
                    "StructureChecker.MissingExampleUsage",
                    "Document is missing required 'Example Usage' section",
                    fileId,
                    null,
                    null,
                    "DOCUMENT");
            errors.add(CheckError.builder()
                    .message(message)
                    .severity(CheckError.Severity.ERROR)
                    .build());
            return null;
        }

        // Find the next heading of the same level (level 2) after Example Usage heading
        TreeNode<MdAstNode> exampleNode = exampleHeading.get();
        TreeNode<MdAstNode> nextSameLevelHeading = findNextHeading(exampleNode, 2);

        // Collect all nodes from Example Usage heading to next same-level heading
        // (excluding next heading)
        return collectNodesBetween(exampleNode, nextSameLevelHeading);
    }

    private List<TreeNode<MdAstNode>> checkArgumentReference(TreeNode<MdAstNode> document, String fileId,
            List<CheckError> errors) {
        // Check for "Argument Reference" heading (level 2)
        Optional<TreeNode<MdAstNode>> argumentHeading = findHeadingByLevelAndText(document, 2, "Argument Reference");

        if (argumentHeading.isEmpty()) {
            String message = buildErrorMessage(
                    "StructureChecker.MissingArgumentReference",
                    "Document is missing required 'Argument Reference' section",
                    fileId,
                    null,
                    null,
                    "DOCUMENT");
            errors.add(CheckError.builder()
                    .message(message)
                    .severity(CheckError.Severity.ERROR)
                    .build());
            return null;
        }

        // Find the next heading of the same level (level 2) after Argument Reference
        // heading
        TreeNode<MdAstNode> argumentNode = argumentHeading.get();
        TreeNode<MdAstNode> nextSameLevelHeading = findNextHeading(argumentNode, 2);

        // Collect all nodes from Argument Reference heading to next same-level heading
        // (excluding next heading)
        return collectNodesBetween(argumentNode, nextSameLevelHeading);
    }

    private List<TreeNode<MdAstNode>> checkAttributeReference(TreeNode<MdAstNode> document, String fileId,
            List<CheckError> errors) {
        // Check for "Attribute Reference" heading (level 2)
        Optional<TreeNode<MdAstNode>> attributeHeading = findHeadingByLevelAndText(document, 2, "Attribute Reference");

        if (attributeHeading.isEmpty()) {
            String message = buildErrorMessage(
                    "StructureChecker.MissingAttributeReference",
                    "Document is missing required 'Attribute Reference' section",
                    fileId,
                    null,
                    null,
                    "DOCUMENT");
            errors.add(CheckError.builder()
                    .message(message)
                    .severity(CheckError.Severity.ERROR)
                    .build());
            return null;
        }

        // Find the next heading of the same level (level 2) after Attribute Reference
        // heading
        TreeNode<MdAstNode> attributeNode = attributeHeading.get();
        TreeNode<MdAstNode> nextSameLevelHeading = findNextHeading(attributeNode, 2);

        // Collect all nodes from Attribute Reference heading to next same-level heading
        // (excluding next heading)
        return collectNodesBetween(attributeNode, nextSameLevelHeading);
    }

    /**
     * Finds a heading node by level and optional text content.
     *
     * @param document The document root node
     * @param level    The heading level (1-6)
     * @param text     Optional text content to match (case-insensitive). If null,
     *                 only level is matched
     * @return Optional containing the found heading node, or empty if not found
     */
    private Optional<TreeNode<MdAstNode>> findHeadingByLevelAndText(TreeNode<MdAstNode> document, int level,
            String text) {
        List<TreeNode<MdAstNode>> headingNodes = document.query()
                .all()
                .ofType(MdAstNode.class)
                .filter(MdAstNode.class, data -> data != null && data.getNodeType() == MdNodeType.HEADING)
                .list();

        return headingNodes.stream()
                .filter(node -> {
                    MdAstNode data = node.getData();
                    Heading heading = (Heading) data.getCommonMarkNode();
                    if (heading == null || heading.getLevel() != level) {
                        return false;
                    }
                    if (text != null) {
                        String headingText = extractHeadingText(node);
                        return headingText != null && headingText.trim().equalsIgnoreCase(text);
                    }
                    return true;
                })
                .findFirst();
    }

    /**
     * Finds the next heading node after the given node.
     *
     * @param startNode The starting node
     * @param level     Optional heading level to match. If null, matches any
     *                  heading
     *                  level
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
     * @param endNode   The ending node (exclusive). If null, collects until the end
     *                  of parent's children
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

    private String extractHeadingText(TreeNode<MdAstNode> node) {
        // Extract text from heading node by traversing its children
        StringBuilder sb = new StringBuilder();
        extractTextRecursive(node, sb);
        return sb.toString().trim();
    }

    private void extractTextRecursive(TreeNode<MdAstNode> node, StringBuilder sb) {
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

        // Traverse children if available
        List<TreeNode<MdAstNode>> children = node.getChildren();
        if (children != null) {
            for (TreeNode<MdAstNode> child : children) {
                extractTextRecursive(child, sb);
            }
        }
    }
}

