package org.example;

import org.example.code.checker.parser.markdown.preprocess.PreProcessor;
import org.example.code.checker.parser.markdown.parser.MdAstGenerator;
import org.antlr.v4.runtime.tree.ParseTree;

public class Main {
    public static void main(String[] args) {
        String rawMarkdown = "## Markdown\n"
                + "### Markdown 预处理\n"
                + "" + "\n" // blank line
                + "    - list item with spaces\n"
                + "\t- list item with tab\n"
                + "Text after list";

        System.out.println(System.getProperty("file.encoding"));

        System.out.println("=== Raw ===");
        System.out.println(rawMarkdown);

        PreProcessor preProcessor = new PreProcessor(rawMarkdown);
        String processed = preProcessor.process();

        /**
         * <header:1><space>Markdown
         * <header:2><space>Title 2
         *
         * <indent:4>- list item with spaces
         * <tab>- list item with tab
         * Text after list
         */

        System.out.println("\n=== Processed ===");
        System.out.println(processed);

        ParseTree tree = MdAstGenerator.generateAst(processed);
        System.out.println("\n=== AST (tree) ===");
        printTree(tree, "");
    }

    private static void printTree(ParseTree node, String indent) {
        if (node == null) {
            return;
        }
        for (int i = 0; i < node.getChildCount(); i++) {
            printTree(node.getChild(i), indent + "  ");
        }
    }
}
