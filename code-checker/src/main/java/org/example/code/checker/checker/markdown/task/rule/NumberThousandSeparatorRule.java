package org.example.code.checker.checker.markdown.task.rule;

import org.example.code.checker.checker.common.BaseCheckRule;
import org.example.code.checker.checker.common.CheckContext;
import org.example.code.checker.checker.common.CheckError;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Rule for checking if large numbers use thousand separators.
 */
public class NumberThousandSeparatorRule extends BaseCheckRule {

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
     * Checks a specific text field for numbers that need thousand separators.
     */
    public List<CheckError> checkText(String fieldName, String text, boolean isBacktickWrapped,
                                      boolean isBoldWrapped, CheckContext context) {
        List<CheckError> errors = new ArrayList<>();
        if (text == null || text.trim().isEmpty()) {
            return errors;
        }

        // Only check if not wrapped in backticks or bold (they might contain formatted numbers)
        if (isBacktickWrapped || isBoldWrapped) {
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

            // Only check integers (no decimal point) and >= 1000
            if (!numberStr.contains(".")) {
                try {
                    long number = Long.parseLong(numberStr);
                    if (number >= 1000) {
                        String formatted = formatWithThousandSeparator(number);
                        if (!numberStr.equals(formatted)) {
                            errors.add(createError(context, "MissingThousandSeparator",
                                    String.format("Number '%s' in %s should use thousand separators: '%s'",
                                            numberStr, fieldName, formatted),
                                    null, null, fieldName));
                        }
                    }
                } catch (NumberFormatException e) {
                    // Ignore if number is too large to parse
                }
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

    private String formatWithThousandSeparator(long number) {
        return String.format("%,d", number);
    }

    @Override
    public String getRuleName() {
        return "NumberThousandSeparator";
    }
}

