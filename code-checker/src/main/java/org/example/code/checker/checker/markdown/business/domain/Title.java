package org.example.code.checker.checker.markdown.business.domain;

import org.example.code.checker.checker.markdown.business.Domain;
import org.example.code.checker.checker.markdown.parser.ast.MdAstNode;

public class Title extends Domain {
    public String name;
    public String description;

    public static Title from(MdAstNode node) {
        return null;
    }
}
