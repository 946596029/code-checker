package org.example.code.checker.checker.common;

import java.util.List;

/**
 * Check rule interface.
 * Each specific check rule implements this interface.
 */
public interface CheckRule {
    /**
     * Executes the check.
     *
     * @param context Check context containing document, fileId, etc.
     * @return List of errors found during the check
     */
    List<CheckError> check(CheckContext context);

    /**
     * Rule name for error messages.
     */
    String getRuleName();
}

