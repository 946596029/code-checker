package org.example.code.checker.checker.markdown.domain.standard;

public enum StandardNodeType {

    // Block
    BLOCK_QUOTE("blockQuote"),
    CODE_BLOCK("codeBlock"),
    DOCUMENT("document"),
    HEADING("heading"),
    LIST_BLOCK("listBlock"),
    LIST_ITEM("listItem"),
    PARAGRAPH("paragraph"),
    THEMATIC_BREAK("thematicBreak"),

    // Inline
    CODE_SPAN("codeSpan"),
    EMPHASIS("emphasis"),
    IMAGE("image"),
    LINK("link"),
    STRONG("strong"),
    TEXT("text");

    private String type;

    StandardNodeType(String type) {
        this.type = type;
    }

    public boolean isBlock() {
        return this == BLOCK_QUOTE || this == CODE_BLOCK || this == DOCUMENT || this == HEADING || this == LIST_BLOCK
                || this == LIST_ITEM || this == PARAGRAPH || this == THEMATIC_BREAK;
    }
}
