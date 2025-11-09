package org.example.code.checker.checker.markdown.domain;

import org.example.code.checker.checker.markdown.parser.ast.SourceRange;

/**
 * Legacy-friendly utility holder for domain helpers.
 * This class intentionally does not depend on parser AST nodes.
 */
public final class Domain {
    private Domain() {}

    /**
     * Slice raw text by {@link SourceRange} offsets.
     * If inputs are invalid, returns an empty string.
     */
    public static String sliceRaw(CharSequence source, SourceRange range) {
        if (source == null || range == null || range.start == null || range.end == null) {
            return "";
        }
        int len = source.length();
        int start = clamp(range.start.offset, 0, len);
        int end = clamp(range.end.offset, 0, len);
        if (end < start) {
            return "";
        }
        return source.subSequence(start, end).toString();
    }

    private static int clamp(int value, int min, int max) {
        if (value < min) return min;
        if (value > max) return max;
        return value;
    }
}
