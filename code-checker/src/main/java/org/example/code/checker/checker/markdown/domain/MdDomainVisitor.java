package org.example.code.checker.checker.markdown.domain;

import org.example.code.checker.checker.markdown.domain.standard.block.*;
import org.example.code.checker.checker.markdown.domain.standard.inline.*;

public interface MdDomainVisitor<R> {

    R visit(BlockQuote blockQuote);
    R visit(CodeBlock codeBlock);
    R visit(Document document);
    R visit(Heading heading);
    R visit(ListBlock listBlock);
    R visit(ListItem listItem);
    R visit(Paragraph paragraph);
    R visit(ThematicBreak thematicBreak);

    R visit(CodeSpan codeSpan);
    R visit(Emphasis emphasis);
    R visit(Image image);
    R visit(Link link);
    R visit(Strong strong);
    R visit(Text text);
}
