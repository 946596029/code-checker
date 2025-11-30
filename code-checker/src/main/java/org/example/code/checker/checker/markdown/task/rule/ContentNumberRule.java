package org.example.code.checker.checker.markdown.task.rule;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.example.code.checker.checker.Checker;
import org.example.code.checker.checker.common.CheckError;
import org.example.code.checker.checker.markdown.task.structure.arguments.Argument;
import org.example.code.checker.checker.markdown.task.structure.arguments.ArgumentList;
import org.example.code.checker.checker.markdown.task.structure.attributes.Attribute;
import org.example.code.checker.checker.markdown.task.structure.attributes.AttributeList;
import org.example.code.checker.checker.markdown.task.structure.title.Title;
import org.example.flow.engine.node.TaskData;

public class ContentNumberRule extends Checker {

    // Pattern to match numbers (integers and decimals)
    // Matches: digits with optional decimal point and more digits
    private static final Pattern NUMBER_PATTERN = Pattern.compile("\\b\\d+(?:\\.\\d+)?\\b");

    // Pattern to match timestamps (ISO 8601, Unix timestamp, etc.)
    // ISO 8601: 2024-01-01, 2024-01-01T12:00:00, 2024-01-01T12:00:00Z, etc.
    // Unix timestamp: 10 or 13 digits (seconds or milliseconds since epoch)
    private static final Pattern TIMESTAMP_PATTERN = Pattern.compile(
            "\\b(?:\\d{4}-\\d{2}-\\d{2}(?:T\\d{2}:\\d{2}:\\d{2}(?:\\.\\d+)?(?:Z|[+-]\\d{2}:\\d{2})?)?|" +
            "\\d{10,13})\\b");

    @Override
    public List<TaskData<?>> task(Map<String, TaskData<?>> input) {
        List<CheckError> errors = new ArrayList<>();

        // Get fileId for error reporting
        TaskData<?> fileIdData = input.get("fileId");
        String fileId = fileIdData != null ? (String) fileIdData.getPayload() : null;

        // Check Title
        TaskData<?> titleData = input.get("titleResult");
        if (titleData != null) {
            @SuppressWarnings("unchecked")
            Title title = (Title) titleData.getPayload();
            if (title != null) {
                checkText("Title.title", title.getTitle(), fileId, errors);
                checkText("Title.description", title.getDescription(), fileId, errors);
            }
        }

        // Check ArgumentList
        TaskData<?> argumentListData = input.get("argumentListResult");
        if (argumentListData != null) {
            @SuppressWarnings("unchecked")
            ArgumentList argumentList = (ArgumentList) argumentListData.getPayload();
            if (argumentList != null) {
                checkText("ArgumentList.title", argumentList.getTitle(), fileId, errors);
                checkText("ArgumentList.description", argumentList.getDescription(), fileId, errors);
                
                if (argumentList.getArguments() != null) {
                    for (int i = 0; i < argumentList.getArguments().size(); i++) {
                        Argument argument = argumentList.getArguments().get(i);
                        checkText(String.format("Argument[%d].description", i), 
                                argument.getDescription(), fileId, errors);
                    }
                }
            }
        }

        // Check AttributeList
        TaskData<?> attributeListData = input.get("attributeListResult");
        if (attributeListData != null) {
            @SuppressWarnings("unchecked")
            AttributeList attributeList = (AttributeList) attributeListData.getPayload();
            if (attributeList != null) {
                checkText("AttributeList.title", attributeList.getTitle(), fileId, errors);
                checkText("AttributeList.description", attributeList.getDescription(), fileId, errors);
                
                if (attributeList.getAttributes() != null) {
                    for (int i = 0; i < attributeList.getAttributes().size(); i++) {
                        Attribute attribute = attributeList.getAttributes().get(i);
                        checkText(String.format("Attribute[%d].description", i), 
                                attribute.getDescription(), fileId, errors);
                    }
                }
            }
        }

        // Return result
        List<TaskData<?>> output = new ArrayList<>();
        if (errors.size() > 0) {
            setErrorList(errors);
            setNeedStop(true);
            return null;
        }

        return output;
    }

    /**
     * Checks a text string for number formatting issues.
     * 
     * @param fieldName The name of the field being checked (for error messages)
     * @param text      The text to check
     * @param fileId    The file identifier
     * @param errors    The list to add errors to
     */
    private void checkText(String fieldName, String text, String fileId, List<CheckError> errors) {
        if (text == null || text.trim().isEmpty()) {
            return;
        }

        // Find all numbers in the text
        Matcher numberMatcher = NUMBER_PATTERN.matcher(text);

        while (numberMatcher.find()) {
            int start = numberMatcher.start();
            int end = numberMatcher.end();
            String numberStr = numberMatcher.group();

            // Skip if this number is part of a timestamp
            if (isTimestamp(text, start, end)) {
                continue;
            }

            // Check if the number is already wrapped in backticks or bold
            boolean isBacktickWrapped = isWrappedInBackticks(text, start, end);
            boolean isBoldWrapped = isWrappedInBold(text, start, end);

            // Check if number needs backtick wrapping
            if (!isBacktickWrapped && !isBoldWrapped) {
                String message = buildErrorMessage(
                        "ContentNumberRule.MissingBacktick",
                        String.format("Number '%s' in %s should be wrapped in backticks (``)", 
                                numberStr, fieldName),
                        fileId,
                        null,
                        null,
                        fieldName);
                errors.add(CheckError.builder()
                        .message(message)
                        .severity(CheckError.Severity.ERROR)
                        .build());
            }

            // Check if number uses thousand separators (for integers >= 1000)
            // Only check if not wrapped in backticks or bold (they might contain formatted numbers)
            if (!isBacktickWrapped && !isBoldWrapped && !numberStr.contains(".")) {
                try {
                    long number = Long.parseLong(numberStr);
                    if (number >= 1000) {
                        String formatted = formatWithThousandSeparator(number);
                        if (!numberStr.equals(formatted)) {
                            String message = buildErrorMessage(
                                    "ContentNumberRule.MissingThousandSeparator",
                                    String.format("Number '%s' in %s should use thousand separators: '%s'", 
                                            numberStr, fieldName, formatted),
                                    fileId,
                                    null,
                                    null,
                                    fieldName);
                            errors.add(CheckError.builder()
                                    .message(message)
                                    .severity(CheckError.Severity.ERROR)
                                    .build());
                        }
                    }
                } catch (NumberFormatException e) {
                    // Ignore if number is too large to parse
                }
            }
        }
    }

    /**
     * Checks if a number at the given position is wrapped in backticks.
     * 
     * @param text  The full text
     * @param start The start position of the number
     * @param end   The end position of the number
     * @return true if the number is wrapped in backticks
     */
    private boolean isWrappedInBackticks(String text, int start, int end) {
        // Look backwards for opening backtick (allow whitespace between backtick and number)
        int backtickStart = -1;
        for (int i = start - 1; i >= 0; i--) {
            char c = text.charAt(i);
            if (c == '`') {
                backtickStart = i;
                break;
            } else if (!Character.isWhitespace(c)) {
                // Found non-whitespace character before backtick, stop looking
                break;
            }
        }
        
        if (backtickStart == -1) {
            return false;
        }
        
        // Look forwards for closing backtick (allow whitespace between number and backtick)
        for (int i = end; i < text.length(); i++) {
            char c = text.charAt(i);
            if (c == '`') {
                return true;
            } else if (!Character.isWhitespace(c)) {
                // Found non-whitespace character after number, stop looking
                break;
            }
        }
        
        return false;
    }

    /**
     * Checks if a number at the given position is wrapped in bold (**).
     * 
     * @param text  The full text
     * @param start The start position of the number
     * @param end   The end position of the number
     * @return true if the number is wrapped in bold
     */
    private boolean isWrappedInBold(String text, int start, int end) {
        // Look backwards for opening ** (allow whitespace between ** and number)
        int boldStart = -1;
        for (int i = start - 1; i >= 1; i--) {
            char c = text.charAt(i);
            if (c == '*' && text.charAt(i - 1) == '*') {
                boldStart = i - 1;
                break;
            } else if (!Character.isWhitespace(c) && c != '*') {
                // Found non-whitespace, non-asterisk character, stop looking
                break;
            }
        }
        
        if (boldStart == -1) {
            return false;
        }
        
        // Look forwards for closing ** (allow whitespace between number and **)
        for (int i = end; i < text.length() - 1; i++) {
            char c = text.charAt(i);
            if (c == '*' && text.charAt(i + 1) == '*') {
                return true;
            } else if (!Character.isWhitespace(c) && c != '*') {
                // Found non-whitespace, non-asterisk character, stop looking
                break;
            }
        }
        
        return false;
    }

    /**
     * Checks if a number at the given position is part of a timestamp.
     * 
     * @param text  The full text
     * @param start The start position of the number
     * @param end   The end position of the number
     * @return true if the number is part of a timestamp
     */
    private boolean isTimestamp(String text, int start, int end) {
        // Check a wider context around the number for timestamp patterns
        int contextStart = Math.max(0, start - 30);
        int contextEnd = Math.min(text.length(), end + 30);
        String context = text.substring(contextStart, contextEnd);
        
        // Adjust the position relative to the context
        int relativeStart = start - contextStart;
        int relativeEnd = end - contextStart;
        
        // Check if there's a timestamp pattern that includes this number
        Matcher timestampMatcher = TIMESTAMP_PATTERN.matcher(context);
        while (timestampMatcher.find()) {
            int tsStart = timestampMatcher.start();
            int tsEnd = timestampMatcher.end();
            // Check if the number position overlaps with the timestamp
            if (relativeStart >= tsStart && relativeEnd <= tsEnd) {
                return true;
            }
        }
        
        return false;
    }

    /**
     * Formats a number with thousand separators (commas).
     * 
     * @param number The number to format
     * @return The formatted string with thousand separators
     */
    private String formatWithThousandSeparator(long number) {
        return String.format("%,d", number);
    }
}

