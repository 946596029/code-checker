package org.example.code.checker.checker.markdown.parser.ast;

public class SourceRange {
    private final int line;
    private final int column;
    private final int inputIndex;
    private final int length;

    public SourceRange(
        int line,
        int column,
        int inputIndex,
        int length
    ) {
        this.line = line;
        this.column = column;
        this.inputIndex = inputIndex;
        this.length = length;
    }

    public int getLine() {
        return line;
    }

    public int getColumn() {
        return column;
    }

    public int getInputIndex() {
        return inputIndex;
    }

    public int getLength() {
        return length;
    }
}
