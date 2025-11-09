package org.example.code.checker.checker.markdown.domain.business;

import org.example.code.checker.checker.markdown.parser.ast.SourceRange;

/**
 * Lightweight contract for anything that can be located in source.
 */
public interface Locatable {
	SourceRange getRange();
}