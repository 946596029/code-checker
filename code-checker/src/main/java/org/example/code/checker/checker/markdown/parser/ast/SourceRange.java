package org.example.code.checker.checker.markdown.parser.ast;

import org.commonmark.node.SourceSpan;

public class SourceRange {
    private final int lineIndex;
    private final int columnIndex;
    private final int finishLine;
    private final int inputIndex;
    private final int length;

    private SourceRange(
        int lineIndex,
        int columnIndex,
        int inputIndex,
        int length,
        int finishLine
    ) {
        this.lineIndex = lineIndex;
        this.columnIndex = columnIndex;
        this.inputIndex = inputIndex;
        this.length = length;
        this.finishLine = finishLine;
    }

    public int getLineIndex() {
        return lineIndex;
    }

    public int getColumnIndex() {
        return columnIndex;
    }

    public int getFinishLine() {
        return finishLine;
    }

    public int getInputIndex() {
        return inputIndex;
    }

    public int getLength() {
        return length;
    }
}
