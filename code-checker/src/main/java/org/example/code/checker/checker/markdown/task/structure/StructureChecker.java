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

        // Check section order: Title -> Example Usage -> Argument Reference -> Attribute Reference
        checkSectionOrder(document, fileId, errors);

        // Check line formatting: trailing spaces and line length
        checkLineFormatting(document, fileId, errors);

        // Return result
        List<TaskData<?>> result = new ArrayList<>();
        if (errors.size() > 0) {
            setErrorList(errors);
            setNeedStop(true);
            return null;
        }

        return result;
    }

    /**
     * Checks the order of sections: Title -> Example Usage -> Argument Reference -> Attribute Reference
     */
    private void checkSectionOrder(TreeNode<MdAstNode> document, String fileId, List<CheckError> errors) {
        // Find all required headings
        Optional<TreeNode<MdAstNode>> titleHeading = findHeadingByLevelAndText(document, 1, null);
        Optional<TreeNode<MdAstNode>> exampleHeading = findHeadingByLevelAndText(document, 2, "Example Usage");
        Optional<TreeNode<MdAstNode>> argumentHeading = findHeadingByLevelAndText(document, 2, "Argument Reference");
        Optional<TreeNode<MdAstNode>> attributeHeading = findHeadingByLevelAndText(document, 2, "Attribute Reference");

        // If any section is missing, report error and skip order check
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
            return;
        }

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
            return;
        }

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
            return;
        }

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
            return;
        }

        // Get positions of each heading in the document
        int titlePosition = getNodePosition(document, titleHeading.get());
        int examplePosition = getNodePosition(document, exampleHeading.get());
        int argumentPosition = getNodePosition(document, argumentHeading.get());
        int attributePosition = getNodePosition(document, attributeHeading.get());

        // Check order: Title < Example Usage < Argument Reference < Attribute Reference
        if (titlePosition >= examplePosition) {
            String message = buildErrorMessage(
                    "StructureChecker.InvalidSectionOrder",
                    "Section order is incorrect. Title must come before Example Usage",
                    fileId,
                    titleHeading.get().getData() != null ? titleHeading.get().getData().getSourceRange() : null,
                    titleHeading.get().getData() != null ? titleHeading.get().getData().getNodeId() : null,
                    "HEADING");
            errors.add(CheckError.builder()
                    .message(message)
                    .severity(CheckError.Severity.ERROR)
                    .build());
        }

        if (examplePosition >= argumentPosition) {
            String message = buildErrorMessage(
                    "StructureChecker.InvalidSectionOrder",
                    "Section order is incorrect. Example Usage must come before Argument Reference",
                    fileId,
                    exampleHeading.get().getData() != null ? exampleHeading.get().getData().getSourceRange() : null,
                    exampleHeading.get().getData() != null ? exampleHeading.get().getData().getNodeId() : null,
                    "HEADING");
            errors.add(CheckError.builder()
                    .message(message)
                    .severity(CheckError.Severity.ERROR)
                    .build());
        }

        if (argumentPosition >= attributePosition) {
            String message = buildErrorMessage(
                    "StructureChecker.InvalidSectionOrder",
                    "Section order is incorrect. Argument Reference must come before Attribute Reference",
                    fileId,
                    argumentHeading.get().getData() != null ? argumentHeading.get().getData().getSourceRange() : null,
                    argumentHeading.get().getData() != null ? argumentHeading.get().getData().getNodeId() : null,
                    "HEADING");
            errors.add(CheckError.builder()
                    .message(message)
                    .severity(CheckError.Severity.ERROR)
                    .build());
        }
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
     * Gets the position of a node in the document's children list.
     * Returns -1 if not found.
     */
    private int getNodePosition(TreeNode<MdAstNode> document, TreeNode<MdAstNode> targetNode) {
        // Get all direct children of the document
        List<TreeNode<MdAstNode>> children = document.getChildren();
        if (children == null) {
            return -1;
        }

        // Search for the target node in the children list
        for (int i = 0; i < children.size(); i++) {
            if (children.get(i) == targetNode) {
                return i;
            }
        }

        // If not found in direct children, search recursively
        // But for headings, they should be direct children of document
        return -1;
    }

    /**
     * Checks line formatting: trailing spaces and line length (max 120 characters).
     */
    private void checkLineFormatting(TreeNode<MdAstNode> document, String fileId, List<CheckError> errors) {
        MdAstNode documentData = document.getData();
        if (documentData == null) {
            return;
        }

        String rawStr = documentData.getRawStr();
        if (rawStr == null) {
            return;
        }

        // Process line by line to accurately track positions
        int lineNumber = 1; // 1-based line number
        int lineStartIndex = 0; // Start index of current line in rawStr
        int column = 0;
        
        for (int i = 0; i < rawStr.length(); i++) {
            char c = rawStr.charAt(i);
            
            // Check for newline characters
            if (c == '\n' || (c == '\r' && (i + 1 >= rawStr.length() || rawStr.charAt(i + 1) != '\n'))) {
                // Process the line that just ended
                int lineEndIndex = (c == '\r' && i + 1 < rawStr.length() && rawStr.charAt(i + 1) == '\n') 
                        ? i : i;
                String line = rawStr.substring(lineStartIndex, lineEndIndex);
                int lineLength = line.length();
                
                // Check for trailing spaces - only allow 0 or 2 spaces, no other characters
                if (lineLength > 0) {
                    // Find the last non-whitespace character
                    int lastNonWhitespace = lineLength - 1;
                    while (lastNonWhitespace >= 0 && line.charAt(lastNonWhitespace) == ' ') {
                        lastNonWhitespace--;
                    }
                    
                    int trailingStart = lastNonWhitespace + 1;
                    int trailingSpaceCount = lineLength - trailingStart;
                    
                    // Check if there are any non-space trailing characters (like tabs)
                    boolean hasNonSpaceTrailing = false;
                    for (int j = trailingStart; j < lineLength; j++) {
                        if (line.charAt(j) != ' ') {
                            hasNonSpaceTrailing = true;
                            break;
                        }
                    }
                    
                    // Report error if:
                    // 1. Has non-space trailing characters (like tabs)
                    // 2. Has trailing spaces but count is not 0 or 2
                    if (hasNonSpaceTrailing) {
                        String message = buildErrorMessage(
                                "StructureChecker.InvalidTrailingWhitespace",
                                String.format("Line %d has invalid trailing characters. Only 0 or 2 spaces are allowed at line end.", lineNumber),
                                fileId,
                                new org.example.code.checker.checker.markdown.parser.ast.SourceRange(
                                        lineNumber - 1, trailingStart, lineStartIndex + trailingStart, lineLength - trailingStart),
                                documentData.getNodeId(),
                                "DOCUMENT");
                        errors.add(CheckError.builder()
                                .message(message)
                                .severity(CheckError.Severity.ERROR)
                                .build());
                    } else if (trailingSpaceCount > 0 && trailingSpaceCount != 2) {
                        String message = buildErrorMessage(
                                "StructureChecker.InvalidTrailingWhitespace",
                                String.format("Line %d has %d trailing spaces. Only 0 or 2 spaces are allowed at line end.", 
                                        lineNumber, trailingSpaceCount),
                                fileId,
                                new org.example.code.checker.checker.markdown.parser.ast.SourceRange(
                                        lineNumber - 1, trailingStart, lineStartIndex + trailingStart, trailingSpaceCount),
                                documentData.getNodeId(),
                                "DOCUMENT");
                        errors.add(CheckError.builder()
                                .message(message)
                                .severity(CheckError.Severity.ERROR)
                                .build());
                    }
                }
                
                // Check line length (max 120 characters)
                if (lineLength > 120) {
                    String message = buildErrorMessage(
                            "StructureChecker.LineTooLong",
                            String.format("Line %d exceeds maximum length of 120 characters (found %d characters)", 
                                    lineNumber, lineLength),
                            fileId,
                            new org.example.code.checker.checker.markdown.parser.ast.SourceRange(
                                    lineNumber - 1, 0, lineStartIndex, lineLength),
                            documentData.getNodeId(),
                            "DOCUMENT");
                    errors.add(CheckError.builder()
                            .message(message)
                            .severity(CheckError.Severity.ERROR)
                            .build());
                }
                
                // Move to next line
                lineNumber++;
                if (c == '\r' && i + 1 < rawStr.length() && rawStr.charAt(i + 1) == '\n') {
                    lineStartIndex = i + 2; // Skip \r\n
                    i++; // Skip the \n in next iteration
                } else {
                    lineStartIndex = i + 1; // Skip \n
                }
                column = 0;
            } else if (c == '\r' && i + 1 < rawStr.length() && rawStr.charAt(i + 1) == '\n') {
                // This is part of \r\n, will be handled in next iteration
                continue;
            } else {
                column++;
            }
        }
        
        // Process the last line (if file doesn't end with newline)
        if (lineStartIndex < rawStr.length()) {
            String line = rawStr.substring(lineStartIndex);
            int lineLength = line.length();
            
            // Check for trailing spaces - only allow 0 or 2 spaces, no other characters
            if (lineLength > 0) {
                // Find the last non-whitespace character
                int lastNonWhitespace = lineLength - 1;
                while (lastNonWhitespace >= 0 && line.charAt(lastNonWhitespace) == ' ') {
                    lastNonWhitespace--;
                }
                
                int trailingStart = lastNonWhitespace + 1;
                int trailingSpaceCount = lineLength - trailingStart;
                
                // Check if there are any non-space trailing characters (like tabs)
                boolean hasNonSpaceTrailing = false;
                for (int j = trailingStart; j < lineLength; j++) {
                    if (line.charAt(j) != ' ') {
                        hasNonSpaceTrailing = true;
                        break;
                    }
                }
                
                // Report error if:
                // 1. Has non-space trailing characters (like tabs)
                // 2. Has trailing spaces but count is not 0 or 2
                if (hasNonSpaceTrailing) {
                    String message = buildErrorMessage(
                            "StructureChecker.InvalidTrailingWhitespace",
                            String.format("Line %d has invalid trailing characters. Only 0 or 2 spaces are allowed at line end.", lineNumber),
                            fileId,
                            new org.example.code.checker.checker.markdown.parser.ast.SourceRange(
                                    lineNumber - 1, trailingStart, lineStartIndex + trailingStart, lineLength - trailingStart),
                            documentData.getNodeId(),
                            "DOCUMENT");
                    errors.add(CheckError.builder()
                            .message(message)
                            .severity(CheckError.Severity.ERROR)
                            .build());
                } else if (trailingSpaceCount > 0 && trailingSpaceCount != 2) {
                    String message = buildErrorMessage(
                            "StructureChecker.InvalidTrailingWhitespace",
                            String.format("Line %d has %d trailing spaces. Only 0 or 2 spaces are allowed at line end.", 
                                    lineNumber, trailingSpaceCount),
                            fileId,
                            new org.example.code.checker.checker.markdown.parser.ast.SourceRange(
                                    lineNumber - 1, trailingStart, lineStartIndex + trailingStart, trailingSpaceCount),
                            documentData.getNodeId(),
                            "DOCUMENT");
                    errors.add(CheckError.builder()
                            .message(message)
                            .severity(CheckError.Severity.ERROR)
                            .build());
                }
            }
            
            // Check line length
            if (lineLength > 120) {
                String message = buildErrorMessage(
                        "StructureChecker.LineTooLong",
                        String.format("Line %d exceeds maximum length of 120 characters (found %d characters)", 
                                lineNumber, lineLength),
                        fileId,
                        new org.example.code.checker.checker.markdown.parser.ast.SourceRange(
                                lineNumber - 1, 0, lineStartIndex, lineLength),
                        documentData.getNodeId(),
                        "DOCUMENT");
                errors.add(CheckError.builder()
                        .message(message)
                        .severity(CheckError.Severity.ERROR)
                        .build());
            }
        }
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

