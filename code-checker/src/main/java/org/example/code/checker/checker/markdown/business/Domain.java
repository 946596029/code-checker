package org.example.code.checker.checker.markdown.business;

import org.example.code.checker.checker.markdown.parser.ast.MdAstNode;
import org.example.code.checker.checker.markdown.parser.ast.SourceRange;

import java.util.List;

public class Domain {
    public List<MdAstNode> sourceNodes;
    public SourceRange sourceRange;
    public List<Domain> children;

    public String getRaw() {
        StringBuilder raw = new StringBuilder();
        for (MdAstNode node : sourceNodes) {
            raw.append(node.getRawStr());
            raw.append("\n");
        }
        return raw.toString().trim();
    }
}
