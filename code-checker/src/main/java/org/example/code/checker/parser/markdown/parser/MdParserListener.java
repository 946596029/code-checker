// Generated from MdParser.g4 by ANTLR 4.13.2
 package org.example.code.checker.parser.markdown.parser; 
import org.antlr.v4.runtime.tree.ParseTreeListener;

/**
 * This interface defines a complete listener for a parse tree produced by
 * {@link MdParser}.
 */
public interface MdParserListener extends ParseTreeListener {
	/**
	 * Enter a parse tree produced by {@link MdParser#document}.
	 * @param ctx the parse tree
	 */
	void enterDocument(MdParser.DocumentContext ctx);
	/**
	 * Exit a parse tree produced by {@link MdParser#document}.
	 * @param ctx the parse tree
	 */
	void exitDocument(MdParser.DocumentContext ctx);
	/**
	 * Enter a parse tree produced by {@link MdParser#block}.
	 * @param ctx the parse tree
	 */
	void enterBlock(MdParser.BlockContext ctx);
	/**
	 * Exit a parse tree produced by {@link MdParser#block}.
	 * @param ctx the parse tree
	 */
	void exitBlock(MdParser.BlockContext ctx);
	/**
	 * Enter a parse tree produced by {@link MdParser#headerBlock}.
	 * @param ctx the parse tree
	 */
	void enterHeaderBlock(MdParser.HeaderBlockContext ctx);
	/**
	 * Exit a parse tree produced by {@link MdParser#headerBlock}.
	 * @param ctx the parse tree
	 */
	void exitHeaderBlock(MdParser.HeaderBlockContext ctx);
	/**
	 * Enter a parse tree produced by {@link MdParser#listItem}.
	 * @param ctx the parse tree
	 */
	void enterListItem(MdParser.ListItemContext ctx);
	/**
	 * Exit a parse tree produced by {@link MdParser#listItem}.
	 * @param ctx the parse tree
	 */
	void exitListItem(MdParser.ListItemContext ctx);
	/**
	 * Enter a parse tree produced by {@link MdParser#blankBlock}.
	 * @param ctx the parse tree
	 */
	void enterBlankBlock(MdParser.BlankBlockContext ctx);
	/**
	 * Exit a parse tree produced by {@link MdParser#blankBlock}.
	 * @param ctx the parse tree
	 */
	void exitBlankBlock(MdParser.BlankBlockContext ctx);
	/**
	 * Enter a parse tree produced by {@link MdParser#paragraph}.
	 * @param ctx the parse tree
	 */
	void enterParagraph(MdParser.ParagraphContext ctx);
	/**
	 * Exit a parse tree produced by {@link MdParser#paragraph}.
	 * @param ctx the parse tree
	 */
	void exitParagraph(MdParser.ParagraphContext ctx);
	/**
	 * Enter a parse tree produced by {@link MdParser#inlineText}.
	 * @param ctx the parse tree
	 */
	void enterInlineText(MdParser.InlineTextContext ctx);
	/**
	 * Exit a parse tree produced by {@link MdParser#inlineText}.
	 * @param ctx the parse tree
	 */
	void exitInlineText(MdParser.InlineTextContext ctx);
}