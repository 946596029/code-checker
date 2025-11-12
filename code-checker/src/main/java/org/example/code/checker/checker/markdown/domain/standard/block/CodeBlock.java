package org.example.code.checker.checker.markdown.domain.standard.block;

import java.util.Objects;
import java.util.Optional;

import org.example.code.checker.checker.markdown.domain.StdNode;
import org.example.code.checker.checker.markdown.domain.standard.StandardNodeType;
import org.example.code.checker.checker.markdown.parser.ast.SourceRange;

/**
 * Code block (fenced or indented). Language is optional.
 */
public final class CodeBlock extends StdNode {
	private final String content;
	private final String language;

    public CodeBlock(
        String nodeId,
        SourceRange range,
        String parentId,
        String content,
        String language
    ) {
        super(nodeId, range, StandardNodeType.CODE_BLOCK, parentId, null);
        Objects.requireNonNull(content, "content");
        this.content = content;
        this.language = language;
    }

	public String getContent() {
		return content;
	}

	public Optional<String> getLanguage() {
		return Optional.ofNullable(language);
	}
}


