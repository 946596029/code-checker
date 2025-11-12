package org.example.test.checker.markdown.domain;

import org.example.code.checker.checker.markdown.domain.builder.standard.MarkdownDomainBuilder;
import org.example.code.checker.checker.markdown.domain.standard.StdBlock;
import org.example.code.checker.checker.markdown.domain.standard.StdInline;
import org.example.code.checker.checker.markdown.domain.standard.block.BlockQuote;
import org.example.code.checker.checker.markdown.domain.standard.block.CodeBlock;
import org.example.code.checker.checker.markdown.domain.standard.block.Document;
import org.example.code.checker.checker.markdown.domain.standard.block.Heading;
import org.example.code.checker.checker.markdown.domain.standard.block.ListBlock;
import org.example.code.checker.checker.markdown.domain.standard.block.ListItem;
import org.example.code.checker.checker.markdown.domain.standard.block.Paragraph;
import org.example.code.checker.checker.markdown.domain.standard.block.ThematicBreak;
import org.example.code.checker.checker.markdown.domain.standard.inline.CodeSpan;
import org.example.code.checker.checker.markdown.domain.standard.inline.Emphasis;
import org.example.code.checker.checker.markdown.domain.standard.inline.Strong;
import org.example.code.checker.checker.markdown.domain.standard.inline.Text;
import org.example.code.checker.checker.markdown.parser.MdAstGenerator;
import org.example.code.checker.checker.markdown.parser.ast.MdAstNode;

import java.util.List;
import java.util.Optional;

/**
 * Simple demo program for MarkdownDomainBuilder.
 * It builds the Standard Markdown domain model from an MdAstNode tree
 * and prints the resulting structure for visual inspection.
 */
public class MarkdownDomainBuilderMain {

	public static void main(String[] args) {
		String md = ""
				+ "# Title\n"
				+ "\n"
				+ "Paragraph with some *emphasis*, **strong**, and `code`.\n"
				+ "\n"
				+ "1. First\n"
				+ "2. Second\n"
				+ "\n"
				+ "- A\n"
				+ "- B\n"
				+ "\n"
				+ "> Quote line\n"
				+ "\n"
				+ "---\n"
				+ "\n"
				+ "```java\n"
				+ "System.out.println(\"Hello\");\n"
				+ "```\n";

		MdAstNode root = MdAstGenerator.generateStandardAst(md, "in-memory.md");
		Document doc = MarkdownDomainBuilder.buildDocument(root);
		printDocument(doc);
	}

	private static void printDocument(Document document) {
		System.out.println("Document:");
		List<StdBlock> children = document.getChildren();
		for (StdBlock child : children) {
			printBlock(child, 1);
		}
	}

	private static void printBlock(StdBlock block, int indent) {
		if (block instanceof Heading) {
			Heading h = (Heading) block;
			System.out.println(indent(indent) + "Heading(level=" + h.getLevel() + "): " + inlineText(h.getInlines()));
			return;
		}
		if (block instanceof Paragraph) {
			Paragraph p = (Paragraph) block;
			System.out.println(indent(indent) + "Paragraph: " + inlineText(p.getInlines()));
			return;
		}
		if (block instanceof ListBlock) {
			ListBlock lb = (ListBlock) block;
			System.out.println(indent(indent) + "ListBlock(ordered=" + lb.isOrdered() + ", start=" + lb.getStartNumber() + "):");
			for (ListItem item : lb.getItems()) {
				System.out.println(indent(indent + 1) + "ListItem:");
				for (StdBlock child : item.getChildren()) {
					printBlock(child, indent + 2);
				}
			}
			return;
		}
		if (block instanceof BlockQuote) {
			BlockQuote bq = (BlockQuote) block;
			System.out.println(indent(indent) + "BlockQuote:");
			for (StdBlock child : bq.getChildren()) {
				printBlock(child, indent + 1);
			}
			return;
		}
		if (block instanceof ThematicBreak) {
			System.out.println(indent(indent) + "ThematicBreak");
			return;
		}
		if (block instanceof CodeBlock) {
			CodeBlock cb = (CodeBlock) block;
			Optional<String> lang = cb.getLanguage();
			String[] lines = cb.getContent().split("\\R", -1);
			System.out.println(indent(indent) + "CodeBlock(lang=" + lang.orElse("null") + ", lines=" + lines.length + "):");
			for (String line : lines) {
				System.out.println(indent(indent + 1) + line);
			}
			return;
		}
		// Fallback for any future/unknown block type:
		System.out.println(indent(indent) + block.getClass().getSimpleName());
	}

	private static String inlineText(List<StdInline> inlines) {
		StringBuilder sb = new StringBuilder();
		for (StdInline in : inlines) {
			if (in instanceof Text) {
				sb.append(((Text) in).getContent());
			} else if (in instanceof CodeSpan) {
				sb.append('`').append(((CodeSpan) in).getCode()).append('`');
			} else if (in instanceof Emphasis) {
				sb.append('*').append(inlineText(((Emphasis) in).getChildren())).append('*');
			} else if (in instanceof Strong) {
				sb.append("**").append(inlineText(((Strong) in).getChildren())).append("**");
			} else {
				// Unknown inline types degrade to empty textual representation
			}
		}
		return sb.toString();
	}

	private static String indent(int n) {
		return "  ".repeat(Math.max(0, n));
	}
}


