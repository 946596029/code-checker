package org.example.code.checker.parser.markdown.parser;

import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.*;
import org.example.code.checker.parser.markdown.parser.MdLexer;
import org.example.code.checker.parser.markdown.parser.MdParser;
import org.antlr.v4.runtime.Token;

public class MdAstGenerator {

    public static ParseTree generateAst(String input) {
        CharStream charStream = CharStreams.fromString(input);
        MdLexer lexer = new MdLexer(charStream);
        CommonTokenStream tokenStream = new CommonTokenStream(lexer);

        System.out.println("\n=== Token Stream ===");
        for (Token token : tokenStream.getTokens()) {
            System.out.println(token.getType() + " " + token.getText());
        }
        
        MdParser parser = new MdParser(tokenStream);
        return parser.document();
    }
}