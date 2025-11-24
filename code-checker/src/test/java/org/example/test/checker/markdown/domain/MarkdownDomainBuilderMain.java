package org.example.test.checker.markdown.domain;

import org.example.code.checker.checker.markdown.domain.MdDomain;
import org.example.code.checker.checker.markdown.domain.builder.MarkdownDomainBuilder;
import org.example.code.checker.checker.markdown.domain.standard.StandardNodeType;
import org.example.code.checker.checker.markdown.domain.standard.block.BlockQuote;
import org.example.code.checker.checker.markdown.domain.standard.block.CodeBlock;
import org.example.code.checker.checker.markdown.domain.standard.block.Heading;
import org.example.code.checker.checker.markdown.domain.standard.block.ListBlock;
import org.example.code.checker.checker.markdown.domain.standard.block.Paragraph;
import org.example.code.checker.checker.markdown.domain.standard.block.ThematicBreak;
import org.example.code.checker.checker.markdown.domain.standard.inline.CodeSpan;
import org.example.code.checker.checker.markdown.domain.standard.inline.Emphasis;
import org.example.code.checker.checker.markdown.domain.standard.inline.Strong;
import org.example.code.checker.checker.markdown.domain.standard.inline.Text;
import org.example.code.checker.checker.markdown.parser.MdAstGenerator;
import org.example.code.checker.checker.markdown.parser.ast.MdAstNode;
import org.example.code.checker.checker.utils.TreeNode;

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

		MdAstNode root = MdAstGenerator.generate(md, "in-memory.md");
		TreeNode<MdDomain> doc = MarkdownDomainBuilder.buildDocument(root);
		printDocument(doc);
	}

	private static void printDocument(TreeNode<MdDomain> document) {
		System.out.println("Document:");
		List<TreeNode<MdDomain>> children = document.getChildren();
		for (TreeNode<MdDomain> child : children) {
			printBlock(child, 1);
		}
	}

	private static void printBlock(TreeNode<MdDomain> block, int indent) {
        MdDomain domain = block.getData();
		if (domain instanceof Heading) {
			Heading h = (Heading) domain;
			System.out.println(indent(indent) + "Heading(level=" + h.getLevel() + "): " + inlineText(block.getChildren()));
			return;
		}
		if (domain instanceof Paragraph) {
			Paragraph p = (Paragraph) domain;
			System.out.println(indent(indent) + "Paragraph: " + inlineText(block.getChildren()));
			return;
		}
		if (domain instanceof ListBlock) {
			ListBlock lb = (ListBlock) domain;
			System.out.println(indent(indent) + "ListBlock(ordered=" + lb.isOrdered() + ", start=" + lb.getStartNumber() + "):");
			for (TreeNode<MdDomain> item : block.getChildren()) {
                if (StandardNodeType.LIST_ITEM == item.getData().getNodeType()) {
                    System.out.println(indent(indent + 1) + "ListItem:");
                    for (TreeNode<MdDomain> child : item.getChildren()) {
                        printBlock(child, indent + 2);
                    }
                }
			}
			return;
		}
		if (domain instanceof BlockQuote) {
			BlockQuote bq = (BlockQuote) domain;
			System.out.println(indent(indent) + "BlockQuote:");
			for (TreeNode<MdDomain> child : block.getChildren()) {
				printBlock(child, indent + 1);
			}
			return;
		}
		if (domain instanceof ThematicBreak) {
			System.out.println(indent(indent) + "ThematicBreak");
			return;
		}
		if (domain instanceof CodeBlock) {
			CodeBlock cb = (CodeBlock) domain;
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

	private static String inlineText(List<TreeNode<MdDomain>> inlines) {
		StringBuilder sb = new StringBuilder();
		for (TreeNode<MdDomain> inline : inlines) {
            MdDomain domain = inline.getData();
			if (domain instanceof Text) {
				sb.append(((Text) domain).getContent());
			} else if (domain instanceof CodeSpan) {
				sb.append('`').append(((CodeSpan) domain).getCode()).append('`');
			} else if (domain instanceof Emphasis) {
				sb.append('*').append(inlineText(inline.getChildren())).append('*');
			} else if (domain instanceof Strong) {
				sb.append("**").append(inlineText(inline.getChildren())).append("**");
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


