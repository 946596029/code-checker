// Generated from MdParser.g4 by ANTLR 4.13.2
 package org.example.code.checker.parser.markdown.parser; 
import org.antlr.v4.runtime.tree.ParseTreeVisitor;

/**
 * This interface defines a complete generic visitor for a parse tree produced
 * by {@link MdParser}.
 *
 * @param <T> The return type of the visit operation. Use {@link Void} for
 * operations with no return type.
 */
public interface MdParserVisitor<T> extends ParseTreeVisitor<T> {
	/**
	 * Visit a parse tree produced by {@link MdParser#document}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitDocument(MdParser.DocumentContext ctx);
	/**
	 * Visit a parse tree produced by {@link MdParser#block}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitBlock(MdParser.BlockContext ctx);
	/**
	 * Visit a parse tree produced by {@link MdParser#listItem}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitListItem(MdParser.ListItemContext ctx);
	/**
	 * Visit a parse tree produced by {@link MdParser#blankBlock}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitBlankBlock(MdParser.BlankBlockContext ctx);
	/**
	 * Visit a parse tree produced by {@link MdParser#paragraph}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitParagraph(MdParser.ParagraphContext ctx);
	/**
	 * Visit a parse tree produced by {@link MdParser#headerBlock}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitHeaderBlock(MdParser.HeaderBlockContext ctx);
	/**
	 * Visit a parse tree produced by {@link MdParser#inlineText}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitInlineText(MdParser.InlineTextContext ctx);
}