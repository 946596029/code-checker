package org.example.code.checker.checker.markdown.domain;

import org.example.code.checker.checker.markdown.domain.standard.StdNode;
import org.example.code.checker.checker.markdown.domain.standard.block.Document;

import java.util.function.Predicate;

public class MarkdownDomainTreeHelper {

    public static StdNode findFirst(Document document, Predicate<StdNode> predicate) {
        if (document == null || predicate == null) {
            return null;
        }
        if (predicate.test(document)) {
            return document;
        }
//        if (root.getChildren() != null) {
//            for (MdAstNode child : root.getChildren()) {
//                MdAstNode result = findFirst(child, predicate);
//                if (result != null) {
//                    return result;
//                }
//            }
//        }
        return null;
    }
}
