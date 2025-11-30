package org.example.code.checker.checker.markdown.task.structure.example;

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

public class ExampleChecker extends Checker {
    
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

        // Find "Example Usage" heading (level 2)
        Optional<TreeNode<MdAstNode>> exampleHeading = findHeadingByLevelAndText(document, 2, "Example Usage");

        if (exampleHeading.isEmpty()) {
            String message = buildErrorMessage(
                    "ExampleChecker.MissingExampleUsage",
                    "Document is missing required 'Example Usage' section",
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

        // Find the next heading of the same level (level 2) after Example Usage heading
        TreeNode<MdAstNode> exampleNode = exampleHeading.get();
        TreeNode<MdAstNode> nextSameLevelHeading = findNextHeading(exampleNode, 2);

        // Collect all nodes from Example Usage heading to next same-level heading
        // (excluding next heading)
        List<TreeNode<MdAstNode>> exampleUsageSection = collectNodesBetween(exampleNode, nextSameLevelHeading);

        // Extract example items from the section
        List<Example.ExampleItem> exampleItems = extractExampleItems(exampleUsageSection, fileId, errors);

        // Create Example object
        Example example = new Example(exampleItems);

        // Return result
        List<TaskData<?>> output = new ArrayList<>();
        if (errors.size() > 0) {
            setErrorList(errors);
            setNeedStop(true);
            return null;
        }

        output.add(new TaskData<>("exampleResult", example));
        return output;
    }

    /**
     * Extracts example items from the example usage section.
     * Handles both single example (no subheading) and multiple examples (with level 3 subheadings).
     * Rules:
     * - Single example: no headings allowed
     * - Multiple examples: each example must be preceded by a level 3 heading
     * - Other level headings (1, 2, 4, 5, 6) are not allowed and will cause errors
     */
    private List<Example.ExampleItem> extractExampleItems(List<TreeNode<MdAstNode>> exampleUsageSection,
            String fileId, List<CheckError> errors) {
        List<Example.ExampleItem> exampleItems = new ArrayList<>();

        // First, scan for headings to validate and determine structure
        List<TreeNode<MdAstNode>> headings = new ArrayList<>();
        for (int i = 1; i < exampleUsageSection.size(); i++) {
            TreeNode<MdAstNode> node = exampleUsageSection.get(i);
            MdAstNode nodeData = node.getData();
            if (nodeData != null && nodeData.getNodeType() == MdNodeType.HEADING) {
                headings.add(node);
            }
        }

        // Count code blocks to determine if single or multiple examples
        int codeBlockCount = 0;
        for (int j = 1; j < exampleUsageSection.size(); j++) {
            TreeNode<MdAstNode> node = exampleUsageSection.get(j);
            MdAstNode nodeData = node.getData();
            if (nodeData != null && nodeData.getNodeType() == MdNodeType.CODE_BLOCK) {
                codeBlockCount++;
            }
        }

        boolean isSingleExample = codeBlockCount <= 1;
        boolean hasHeadings = !headings.isEmpty();

        // Validate headings based on example count
        for (TreeNode<MdAstNode> headingNode : headings) {
            MdAstNode nodeData = headingNode.getData();
            Heading heading = (Heading) nodeData.getCommonMarkNode();
            if (heading == null) {
                continue;
            }

            int level = heading.getLevel();
            
            if (isSingleExample) {
                // Single example: no headings allowed at all
                String headingText = extractTextFromNode(headingNode);
                String message = buildErrorMessage(
                        "ExampleChecker.SingleExampleWithHeading",
                        String.format("Single example cannot have headings. Found level %d heading: %s", level, headingText),
                        fileId,
                        nodeData.getSourceRange(),
                        nodeData.getNodeId(),
                        "HEADING");
                errors.add(CheckError.builder()
                        .message(message)
                        .severity(CheckError.Severity.ERROR)
                        .build());
            } else {
                // Multiple examples: only level 3 headings are allowed
                if (level != 3) {
                    String headingText = extractTextFromNode(headingNode);
                    String message = buildErrorMessage(
                            "ExampleChecker.InvalidHeadingLevel",
                            String.format("Multiple examples must use level 3 headings only. Found level %d heading: %s", level, headingText),
                            fileId,
                            nodeData.getSourceRange(),
                            nodeData.getNodeId(),
                            "HEADING");
                    errors.add(CheckError.builder()
                            .message(message)
                            .severity(CheckError.Severity.ERROR)
                            .build());
                }
            }
        }

        // If there are invalid headings, stop processing
        if (errors.size() > 0) {
            return exampleItems;
        }
        
        // Skip the first node which is the "Example Usage" heading
        int i = 1;
        String currentName = null;
        StringBuilder currentCode = new StringBuilder();
        int codeBlockIndex = 0; // Track which code block we're processing

        while (i < exampleUsageSection.size()) {
            TreeNode<MdAstNode> node = exampleUsageSection.get(i);
            MdAstNode nodeData = node.getData();

            if (nodeData == null) {
                i++;
                continue;
            }

            // Check if it's a level 3 heading - indicates a new example
            if (nodeData.getNodeType() == MdNodeType.HEADING) {
                Heading heading = (Heading) nodeData.getCommonMarkNode();
                if (heading != null && heading.getLevel() == 3) {
                    // Save previous example if exists
                    if (currentCode.length() > 0) {
                        String code = currentCode.toString().trim();
                        if (!code.isEmpty()) {
                            exampleItems.add(new Example.ExampleItem(
                                    currentName != null ? currentName : "Example",
                                    code));
                        }
                        currentCode.setLength(0);
                    }
                    // Extract subheading text as example name
                    currentName = extractTextFromNode(node);
                }
            }
            // Check if it's a code block
            else if (nodeData.getNodeType() == MdNodeType.CODE_BLOCK) {
                // For multiple examples, each code block (including the first) must be preceded by a level 3 heading
                if (!isSingleExample && currentName == null && codeBlockIndex == 0) {
                    // First code block in multiple examples without a heading
                    String message = buildErrorMessage(
                            "ExampleChecker.MissingHeadingBeforeFirstExample",
                            "Multiple examples require each example to be preceded by a level 3 heading",
                            fileId,
                            nodeData.getSourceRange(),
                            nodeData.getNodeId(),
                            "CODE_BLOCK");
                    errors.add(CheckError.builder()
                            .message(message)
                            .severity(CheckError.Severity.ERROR)
                            .build());
                } else if (!isSingleExample && currentName == null && codeBlockIndex > 0) {
                    // Subsequent code block without a heading
                    String message = buildErrorMessage(
                            "ExampleChecker.MissingHeadingBeforeExample",
                            "Multiple examples require each example to be preceded by a level 3 heading",
                            fileId,
                            nodeData.getSourceRange(),
                            nodeData.getNodeId(),
                            "CODE_BLOCK");
                    errors.add(CheckError.builder()
                            .message(message)
                            .severity(CheckError.Severity.ERROR)
                            .build());
                }
                
                String code = nodeData.getText();
                if (code != null && !code.trim().isEmpty()) {
                    if (currentCode.length() > 0) {
                        currentCode.append("\n\n");
                    }
                    currentCode.append(code);
                    codeBlockIndex++;
                    // After processing a code block, reset currentName to require a heading before next one
                    if (!isSingleExample) {
                        currentName = null;
                    }
                }
            }

            i++;
        }

        // Add the last example
        if (currentCode.length() > 0) {
            String code = currentCode.toString().trim();
            if (!code.isEmpty()) {
                exampleItems.add(new Example.ExampleItem(
                        currentName != null ? currentName : "Example",
                        code));
            }
        }

        // Validate structure consistency: multiple examples must have level 3 headings
        if (!isSingleExample && !hasHeadings) {
            // Multiple examples without headings - this is an error
            String message = buildErrorMessage(
                    "ExampleChecker.MultipleExamplesWithoutHeadings",
                    "Multiple examples found but no level 3 headings. Each example must be preceded by a level 3 heading.",
                    fileId,
                    null,
                    null,
                    "EXAMPLE_USAGE");
            errors.add(CheckError.builder()
                    .message(message)
                    .severity(CheckError.Severity.ERROR)
                    .build());
        }

        // Validate that at least one example was found
        if (exampleItems.isEmpty()) {
            String message = buildErrorMessage(
                    "ExampleChecker.NoExamples",
                    "Example Usage section must contain at least one code block",
                    fileId,
                    null,
                    null,
                    "EXAMPLE_USAGE");
            errors.add(CheckError.builder()
                    .message(message)
                    .severity(CheckError.Severity.ERROR)
                    .build());
        }

        return exampleItems;
    }

    /**
     * Finds a heading node by level and text content.
     *
     * @param document The document root node
     * @param level    The heading level (1-6)
     * @param text     Text content to match (case-insensitive)
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
     * Extracts text content from a heading node.
     */
    private String extractHeadingText(TreeNode<MdAstNode> node) {
        StringBuilder sb = new StringBuilder();
        extractTextRecursive(node, sb);
        return sb.toString().trim();
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
