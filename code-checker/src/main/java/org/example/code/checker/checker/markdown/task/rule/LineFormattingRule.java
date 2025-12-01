package org.example.code.checker.checker.markdown.task.rule;

import org.example.code.checker.checker.common.BaseCheckRule;
import org.example.code.checker.checker.common.CheckContext;
import org.example.code.checker.checker.common.CheckError;
import org.example.code.checker.checker.markdown.parser.ast.MdAstNode;
import org.example.code.checker.checker.markdown.parser.ast.SourceRange;
import org.example.code.checker.checker.utils.TreeNode;

import java.util.ArrayList;
import java.util.List;

/**
 * Rule for checking line formatting: trailing spaces and line length (max 120 characters).
 */
public class LineFormattingRule extends BaseCheckRule {

    private static final int MAX_LINE_LENGTH = 120;

    @Override
    public List<CheckError> check(CheckContext context) {
        List<CheckError> errors = new ArrayList<>();
        TreeNode<MdAstNode> document = context.getDocument();

        MdAstNode documentData = document.getData();
        if (documentData == null) {
            return errors;
        }

        String rawStr = documentData.getRawStr();
        if (rawStr == null) {
            return errors;
        }

        // Process line by line to accurately track positions
        int lineNumber = 1;
        int lineStartIndex = 0;

        for (int i = 0; i < rawStr.length(); i++) {
            char c = rawStr.charAt(i);

            // Check for newline characters
            if (c == '\n' || (c == '\r' && (i + 1 >= rawStr.length() || rawStr.charAt(i + 1) != '\n'))) {
                int lineEndIndex = (c == '\r' && i + 1 < rawStr.length() && rawStr.charAt(i + 1) == '\n')
                        ? i : i;
                String line = rawStr.substring(lineStartIndex, lineEndIndex);
                int lineLength = line.length();

                // Check trailing spaces
                checkTrailingSpaces(line, lineNumber, lineStartIndex, lineLength, documentData, context, errors);

                // Check line length
                checkLineLength(lineLength, lineNumber, lineStartIndex, documentData, context, errors);

                // Move to next line
                lineNumber++;
                if (c == '\r' && i + 1 < rawStr.length() && rawStr.charAt(i + 1) == '\n') {
                    lineStartIndex = i + 2;
                    i++;
                } else {
                    lineStartIndex = i + 1;
                }
            } else if (c == '\r' && i + 1 < rawStr.length() && rawStr.charAt(i + 1) == '\n') {
                continue;
            }
        }

        // Process the last line (if file doesn't end with newline)
        if (lineStartIndex < rawStr.length()) {
            String line = rawStr.substring(lineStartIndex);
            int lineLength = line.length();

            checkTrailingSpaces(line, lineNumber, lineStartIndex, lineLength, documentData, context, errors);
            checkLineLength(lineLength, lineNumber, lineStartIndex, documentData, context, errors);
        }

        return errors;
    }

    private void checkTrailingSpaces(String line, int lineNumber, int lineStartIndex, int lineLength,
                                    MdAstNode documentData, CheckContext context, List<CheckError> errors) {
        if (lineLength == 0) {
            return;
        }

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
            errors.add(createError(context, "InvalidTrailingWhitespace",
                    String.format("Line %d has invalid trailing characters. Only 0 or 2 spaces are allowed at line end.", lineNumber),
                    new SourceRange(lineNumber - 1, trailingStart, lineStartIndex + trailingStart, lineLength - trailingStart),
                    documentData.getNodeId(), "DOCUMENT"));
        } else if (trailingSpaceCount > 0 && trailingSpaceCount != 2) {
            errors.add(createError(context, "InvalidTrailingWhitespace",
                    String.format("Line %d has %d trailing spaces. Only 0 or 2 spaces are allowed at line end.",
                            lineNumber, trailingSpaceCount),
                    new SourceRange(lineNumber - 1, trailingStart, lineStartIndex + trailingStart, trailingSpaceCount),
                    documentData.getNodeId(), "DOCUMENT"));
        }
    }

    private void checkLineLength(int lineLength, int lineNumber, int lineStartIndex,
                                 MdAstNode documentData, CheckContext context, List<CheckError> errors) {
        if (lineLength > MAX_LINE_LENGTH) {
            errors.add(createError(context, "LineTooLong",
                    String.format("Line %d exceeds maximum length of %d characters (found %d characters)",
                            lineNumber, MAX_LINE_LENGTH, lineLength),
                    new SourceRange(lineNumber - 1, 0, lineStartIndex, lineLength),
                    documentData.getNodeId(), "DOCUMENT"));
        }
    }

    @Override
    public String getRuleName() {
        return "LineFormatting";
    }
}

