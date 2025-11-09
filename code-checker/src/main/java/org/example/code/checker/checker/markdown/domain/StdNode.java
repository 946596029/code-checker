package org.example.code.checker.checker.markdown.domain.standard;

import org.example.code.checker.checker.markdown.parser.ast.SourceRange;

/**
 * Base interface for all Standard domain nodes (blocks and inlines).
 * Nodes are immutable and always carry their source range.
 */
public interface StdNode {
	SourceRange getRange();
}


