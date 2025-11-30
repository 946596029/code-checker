package org.example.code.checker.checker.markdown.task.structure.attributes;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Pattern;

import org.commonmark.node.Heading;
import org.example.code.checker.checker.Checker;
import org.example.code.checker.checker.common.CheckError;
import org.example.code.checker.checker.markdown.parser.ast.MdAstNode;
import org.example.code.checker.checker.markdown.parser.ast.MdNodeType;
import org.example.code.checker.checker.utils.TreeNode;
import org.example.flow.engine.node.TaskData;

public class AttributeListChecker extends Checker {
    
    private static final String REQUIRED_TITLE = "Attribute Reference";

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

        // Find "Attribute Reference" heading (level 2)
        Optional<TreeNode<MdAstNode>> attributeHeading = findHeadingByLevelAndText(document, 2, REQUIRED_TITLE);

        if (attributeHeading.isEmpty()) {
            String message = buildErrorMessage(
                    "AttributeListChecker.MissingAttributeReference",
                    "Document is missing required 'Attribute Reference' section",
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

        // Find the next heading of the same level (level 2) after Attribute Reference heading
        TreeNode<MdAstNode> attributeNode = attributeHeading.get();
        TreeNode<MdAstNode> nextSameLevelHeading = findNextHeading(attributeNode, 2);

        // Collect all nodes from Attribute Reference heading to next same-level heading
        // (excluding next heading)
        List<TreeNode<MdAstNode>> attributeSection = collectNodesBetween(attributeNode, nextSameLevelHeading);

        // Extract title, description, and attributes
        String title = extractTextFromNode(attributeNode);
        String description = null;
        List<Attribute> attributes = new ArrayList<>();

        // Skip the first node which is the "Attribute Reference" heading
        int i = 1;
        TreeNode<MdAstNode> descriptionNode = null;
        TreeNode<MdAstNode> listNode = null;

        // Find description paragraph (optional, first paragraph after heading if exists)
        while (i < attributeSection.size()) {
            TreeNode<MdAstNode> node = attributeSection.get(i);
            MdAstNode nodeData = node.getData();

            if (nodeData != null && nodeData.getNodeType() == MdNodeType.PARAGRAPH) {
                String paragraphText = extractTextFromNode(node);
                if (paragraphText != null && !paragraphText.trim().isEmpty()) {
                    description = paragraphText.trim();
                    descriptionNode = node;
                    i++;
                    break;
                }
            } else if (nodeData != null && nodeData.getNodeType() == MdNodeType.LIST) {
                // If we find a list before a paragraph, there's no description
                break;
            }
            i++;
        }

        // Find list node (should be after description or heading)
        while (i < attributeSection.size()) {
            TreeNode<MdAstNode> node = attributeSection.get(i);
            MdAstNode nodeData = node.getData();

            if (nodeData != null && nodeData.getNodeType() == MdNodeType.LIST) {
                listNode = node;
                break;
            }
            i++;
        }

        // Extract attributes from list
        if (listNode != null) {
            attributes = extractAttributesFromList(listNode, fileId, errors);
        } else {
            String message = buildErrorMessage(
                    "AttributeListChecker.MissingAttributeList",
                    "Attribute Reference section must contain a list of attributes",
                    fileId,
                    null,
                    null,
                    "ATTRIBUTE_REFERENCE");
            errors.add(CheckError.builder()
                    .message(message)
                    .severity(CheckError.Severity.ERROR)
                    .build());
        }

        // Create AttributeList object
        AttributeList attributeList = new AttributeList(
                title != null ? title : REQUIRED_TITLE,
                description != null ? description : "",
                attributes);

        // Return result
        List<TaskData<?>> output = new ArrayList<>();
        if (errors.size() > 0) {
            setErrorList(errors);
            setNeedStop(true);
            return null;
        }

        output.add(new TaskData<>("attributeListResult", attributeList));
        return output;
    }

    /**
     * Extracts attributes from a list node.
     */
    private List<Attribute> extractAttributesFromList(TreeNode<MdAstNode> listNode, String fileId,
            List<CheckError> errors) {
        List<Attribute> attributes = new ArrayList<>();

        List<TreeNode<MdAstNode>> children = listNode.getChildren();
        if (children == null) {
            return attributes;
        }

        for (TreeNode<MdAstNode> listItem : children) {
            MdAstNode itemData = listItem.getData();
            if (itemData == null || itemData.getNodeType() != MdNodeType.LIST_ITEM) {
                continue;
            }

            // Extract attribute from list item
            Attribute attribute = extractAttributeFromListItem(listItem, fileId, errors);
            if (attribute != null) {
                attributes.add(attribute);
            }
        }

        return attributes;
    }

    /**
     * Extracts a single attribute from a list item.
     * Format: name (CODE) - (tags) description (TEXT)
     */
    private Attribute extractAttributeFromListItem(TreeNode<MdAstNode> listItem, String fileId,
            List<CheckError> errors) {
        // Find the first paragraph in the list item
        TreeNode<MdAstNode> paragraph = findFirstParagraph(listItem);
        if (paragraph == null) {
            String message = buildErrorMessage(
                    "AttributeListChecker.MissingParagraph",
                    "List item must contain a paragraph with attribute definition",
                    fileId,
                    listItem.getData() != null ? listItem.getData().getSourceRange() : null,
                    listItem.getData() != null ? listItem.getData().getNodeId() : null,
                    "LIST_ITEM");
            errors.add(CheckError.builder()
                    .message(message)
                    .severity(CheckError.Severity.ERROR)
                    .build());
            return null;
        }

        // Extract name from first CODE node
        String name = extractCodeFromNode(paragraph);
        if (name == null || name.trim().isEmpty()) {
            String message = buildErrorMessage(
                    "AttributeListChecker.MissingAttributeName",
                    "Attribute must start with a code block containing the attribute name",
                    fileId,
                    paragraph.getData() != null ? paragraph.getData().getSourceRange() : null,
                    paragraph.getData() != null ? paragraph.getData().getNodeId() : null,
                    "PARAGRAPH");
            errors.add(CheckError.builder()
                    .message(message)
                    .severity(CheckError.Severity.ERROR)
                    .build());
            return null;
        }

        // Extract full text from paragraph (excluding nested lists)
        String fullText = extractTextFromParagraph(paragraph);
        
        // Parse: name - (tags) description
        // Remove the name part we already extracted
        String remainingText = fullText;
        if (remainingText.startsWith(name)) {
            remainingText = remainingText.substring(name.length()).trim();
        }

        // Extract tags and description
        Pattern pattern = Pattern.compile("^\\s*-\\s*\\(([^)]+)\\)\\s*(.*)$", Pattern.DOTALL);
        java.util.regex.Matcher matcher = pattern.matcher(remainingText);
        
        if (matcher.matches()) {
            String tagsStr = matcher.group(1).trim();
            String description = matcher.group(2).trim();

            // Parse tags (comma-separated)
            List<String> tags = parseTags(tagsStr);

            return new Attribute(name.trim(), tags, description);
        } else {
            // Try to parse without the dash pattern
            // Sometimes format might be: name (tags) description
            Pattern altPattern = Pattern.compile("^\\s*\\(([^)]+)\\)\\s*(.*)$", Pattern.DOTALL);
            java.util.regex.Matcher altMatcher = altPattern.matcher(remainingText);
            if (altMatcher.matches()) {
                String tagsStr = altMatcher.group(1).trim();
                String description = altMatcher.group(2).trim();
                List<String> tags = parseTags(tagsStr);
                return new Attribute(name.trim(), tags, description);
            } else {
                // Invalid attribute format
                String message = buildErrorMessage(
                        "AttributeListChecker.InvalidAttributeFormat",
                        String.format("Invalid attribute format. Expected: 'name - (tags) description'. Found: %s", 
                                fullText.length() > 100 ? fullText.substring(0, 100) + "..." : fullText),
                        fileId,
                        paragraph.getData() != null ? paragraph.getData().getSourceRange() : null,
                        paragraph.getData() != null ? paragraph.getData().getNodeId() : null,
                        "PARAGRAPH");
                errors.add(CheckError.builder()
                        .message(message)
                        .severity(CheckError.Severity.ERROR)
                        .build());
                return null;
            }
        }
    }

    /**
     * Parses tags from a string like "Optional, String, ForceNew"
     */
    private List<String> parseTags(String tagsStr) {
        List<String> tags = new ArrayList<>();
        if (tagsStr == null || tagsStr.trim().isEmpty()) {
            return tags;
        }

        String[] parts = tagsStr.split(",");
        for (String part : parts) {
            String tag = part.trim();
            if (!tag.isEmpty()) {
                tags.add(tag);
            }
        }
        return tags;
    }

    /**
     * Finds the first paragraph node in a tree.
     */
    private TreeNode<MdAstNode> findFirstParagraph(TreeNode<MdAstNode> node) {
        if (node == null) {
            return null;
        }

        MdAstNode data = node.getData();
        if (data != null && data.getNodeType() == MdNodeType.PARAGRAPH) {
            return node;
        }

        List<TreeNode<MdAstNode>> children = node.getChildren();
        if (children != null) {
            for (TreeNode<MdAstNode> child : children) {
                TreeNode<MdAstNode> paragraph = findFirstParagraph(child);
                if (paragraph != null) {
                    return paragraph;
                }
            }
        }

        return null;
    }

    /**
     * Extracts the first CODE node text from a node.
     */
    private String extractCodeFromNode(TreeNode<MdAstNode> node) {
        if (node == null) {
            return null;
        }

        MdAstNode data = node.getData();
        if (data != null && data.getNodeType() == MdNodeType.CODE) {
            return data.getText();
        }

        List<TreeNode<MdAstNode>> children = node.getChildren();
        if (children != null) {
            for (TreeNode<MdAstNode> child : children) {
                String code = extractCodeFromNode(child);
                if (code != null) {
                    return code;
                }
            }
        }

        return null;
    }

    /**
     * Extracts text from a paragraph node, excluding nested lists.
     */
    private String extractTextFromParagraph(TreeNode<MdAstNode> paragraph) {
        StringBuilder sb = new StringBuilder();
        extractTextFromParagraphRecursive(paragraph, sb, true);
        return sb.toString().trim();
    }

    /**
     * Recursively extracts text from a paragraph, skipping nested lists.
     */
    private void extractTextFromParagraphRecursive(TreeNode<MdAstNode> node, StringBuilder sb, boolean isRoot) {
        if (node == null) {
            return;
        }

        MdAstNode data = node.getData();
        if (data == null) {
            return;
        }

        // Skip nested lists (but include the root paragraph)
        if (!isRoot && data.getNodeType() == MdNodeType.LIST) {
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
                extractTextFromParagraphRecursive(child, sb, false);
            }
        }
    }

    /**
     * Finds a heading node by level and text content.
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
                        String headingText = extractTextFromNode(node);
                        return headingText != null && headingText.trim().equalsIgnoreCase(text);
                    }
                    return true;
                })
                .findFirst();
    }

    /**
     * Finds the next heading node after the given node.
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
                    return siblings.get(i);
                } else {
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

