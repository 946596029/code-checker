package org.example.code.checker.checker.markdown.task.rule;

import org.example.code.checker.checker.common.BaseCheckRule;
import org.example.code.checker.checker.common.CheckContext;
import org.example.code.checker.checker.common.CheckError;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Rule for checking if numbers are wrapped in backticks.
 */
public class NumberBacktickRule extends BaseCheckRule {

    private static final Pattern NUMBER_PATTERN = Pattern.compile("\\b\\d+(?:\\.\\d+)?\\b");
    private static final Pattern TIMESTAMP_PATTERN = Pattern.compile(
            "\\b(?:\\d{4}-\\d{2}-\\d{2}(?:T\\d{2}:\\d{2}:\\d{2}(?:\\.\\d+)?(?:Z|[+-]\\d{2}:\\d{2})?)?|" +
                    "\\d{10,13})\\b");

    @Override
    public List<CheckError> check(CheckContext context) {
        List<CheckError> errors = new ArrayList<>();
        // This rule is applied per text field, not at document level
        // It will be called by ContentNumberRule for each text field
        return errors;
    }

    /**
     * Checks a specific text field for numbers that need backtick wrapping.
     */
    public List<CheckError> checkText(String fieldName, String text, CheckContext context) {
        List<CheckError> errors = new ArrayList<>();
        if (text == null || text.trim().isEmpty()) {
            return errors;
        }

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
                errors.add(createError(context, "MissingBacktick",
                        String.format("Number '%s' in %s should be wrapped in backticks (``)",
                                numberStr, fieldName),
                        null, null, fieldName));
            }
        }

        return errors;
    }

    private boolean isTimestamp(String text, int start, int end) {
        int contextStart = Math.max(0, start - 30);
        int contextEnd = Math.min(text.length(), end + 30);
        String context = text.substring(contextStart, contextEnd);

        int relativeStart = start - contextStart;
        int relativeEnd = end - contextStart;

        Matcher timestampMatcher = TIMESTAMP_PATTERN.matcher(context);
        while (timestampMatcher.find()) {
            int tsStart = timestampMatcher.start();
            int tsEnd = timestampMatcher.end();
            if (relativeStart >= tsStart && relativeEnd <= tsEnd) {
                return true;
            }
        }

        return false;
    }

    public boolean isWrappedInBackticks(String text, int start, int end) {
        int backtickStart = -1;
        for (int i = start - 1; i >= 0; i--) {
            char c = text.charAt(i);
            if (c == '`') {
                backtickStart = i;
                break;
            } else if (!Character.isWhitespace(c)) {
                break;
            }
        }

        if (backtickStart == -1) {
            return false;
        }

        for (int i = end; i < text.length(); i++) {
            char c = text.charAt(i);
            if (c == '`') {
                return true;
            } else if (!Character.isWhitespace(c)) {
                break;
            }
        }

        return false;
    }

    public boolean isWrappedInBold(String text, int start, int end) {
        int boldStart = -1;
        for (int i = start - 1; i >= 1; i--) {
            char c = text.charAt(i);
            if (c == '*' && text.charAt(i - 1) == '*') {
                boldStart = i - 1;
                break;
            } else if (!Character.isWhitespace(c) && c != '*') {
                break;
            }
        }

        if (boldStart == -1) {
            return false;
        }

        for (int i = end; i < text.length() - 1; i++) {
            char c = text.charAt(i);
            if (c == '*' && text.charAt(i + 1) == '*') {
                return true;
            } else if (!Character.isWhitespace(c) && c != '*') {
                break;
            }
        }

        return false;
    }

    @Override
    public String getRuleName() {
        return "NumberBacktick";
    }
}

