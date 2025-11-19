package org.example.code.checker.checker.markdown.task.parser

import org.example.code.checker.checker.common.CheckError
import org.example.code.checker.checker.markdown.domain.MdDomain
import org.example.code.checker.checker.markdown.domain.builder.MarkdownDomainBuilder
import org.example.code.checker.checker.markdown.parser.MdAstGenerator
import org.example.code.checker.checker.markdown.parser.ast.MdAstNode
import org.example.code.checker.checker.markdown.parser.ast.SourcePosition
import org.example.code.checker.checker.markdown.parser.ast.SourceRange
import org.example.code.checker.checker.utils.FileUtils
import org.example.code.checker.checker.utils.TreeNode
import org.example.code.checker.checker.utils.TreeNodeUtils
import org.example.flow.engine.node.TaskData
import org.example.flow.engine.node.TaskDataUtils
import org.example.flow.engine.node.TaskNode

/**
 * First markdown task node implemented in Kotlin.
 *
 * Responsibilities:
 * - Read markdown source from a file path.
 * - Generate AST and convert it to markdown domain tree.
 * - Produce two domain trees:
 *   - originalDocument: immutable view for cross‑rule querying.
 *   - workingDocument: mutable copy that downstream rules can consume.
 * - Check trailing whitespace and blank‑line groups.
 * - Output the trees, raw source and collected [CheckError] list.
 *
 * Input:
 * - filePath: TaskData<String> – absolute or relative file path.
 *
 * Output:
 * - TaskData<String>             – raw markdown source (rawCode).
 * - TaskData<TreeNode<MdDomain>> – original document domain tree.
 * - TaskData<TreeNode<MdDomain>> – working document domain tree.
 * - TaskData<List<CheckError>>   – whitespace check results.
 */
class DocumentParser : TaskNode() {

    override fun task(input: Map<String, TaskData<*>>?): List<TaskData<*>> {
        if (input == null || input.isEmpty()) {
            throw IllegalArgumentException("input is null or empty")
        }

        val filePath =
            TaskDataUtils.getPayload(input, "filePath", String::class.java)
        val fileId = filePath

        val rawCode = try {
            FileUtils.getFileContent(filePath)
        } catch (e: Exception) {
            throw IllegalStateException(
                "Failed to read markdown file: $filePath",
                e
            )
        }

        val root: MdAstNode =
            MdAstGenerator.generateStandardAst(rawCode, fileId)
        val originalDocument: TreeNode<MdDomain> =
            MarkdownDomainBuilder.buildDocument(root)
        val workingDocument: TreeNode<MdDomain> =
            TreeNodeUtils.deepCopy(originalDocument)

        val errors: List<CheckError> = checkWhitespace(fileId, rawCode)

        val now = System.currentTimeMillis()
        val outputs = mutableListOf<TaskData<*>>()

        // Raw markdown source for text-based rules (e.g. FrontMatter).
        outputs += TaskData(
            String::class.java,
            "${DocumentParser::class.java.simpleName}.rawCode",
            now,
            rawCode
        )

        // Original, immutable view of the document.
        outputs += TaskData(
            TreeNode::class.java,
            "${DocumentParser::class.java.simpleName}.originalDocument",
            now,
            originalDocument
        )

        // Working copy that downstream rules can consume / modify.
        outputs += TaskData(
            TreeNode::class.java,
            "${DocumentParser::class.java.simpleName}.workingDocument",
            now,
            workingDocument
        )

        // Whitespace-related warnings.
        outputs += TaskData(
            List::class.java,
            "${DocumentParser::class.java.simpleName}.whitespaceErrors",
            now,
            errors
        )

        return outputs
    }

    /**
     * Scan markdown source for trailing whitespace and blank-line groups
     * and convert them into [CheckError] instances.
     *
     * Rules:
     * - Consecutive blank lines: only when there are more than one blank line
     *   in a row, lines after the first one are reported as errors.
     * - Trailing spaces: for non-blank lines, only 1 or more than 2 trailing
     *   whitespace characters are reported as errors; 0 or exactly 2 are allowed.
     */
    private fun checkWhitespace(fileId: String, source: String): List<CheckError> {
        if (source.isEmpty()) {
            return emptyList()
        }

        val errors = mutableListOf<CheckError>()
        var offset = 0
        var blankRunLength = 0

        source.splitToSequence('\n').forEachIndexed { index, line ->
            val lineNumber = index + 1
            val lineStart = offset
            val lineEnd = offset + line.length

            // Out-of-range guard, should never happen if offsets are computed correctly.
            if (lineStart <= lineEnd && lineStart >= 0) {
                val isBlank = line.isEmpty() || line.all { it.isWhitespace() }

                if (isBlank) {
                    blankRunLength += 1
                    if (blankRunLength > 1) {
                        // Only report when there are more than one blank lines in a row.
                        errors += buildWhitespaceError(
                            fileId = fileId,
                            ruleId = "Whitespace.EmptyLineGroup",
                            message = "Line $lineNumber is part of a multiple blank-line group",
                            startOffset = lineStart,
                            endOffset = lineEnd,
                            lineNumber = lineNumber
                        )
                    }
                } else {
                    // Reset blank-line counter once we hit a non-blank line.
                    blankRunLength = 0

                    // Trailing whitespace check for non-blank lines.
                    var lastNonWs = -1
                    for (i in line.indices) {
                        if (!line[i].isWhitespace()) {
                            lastNonWs = i
                        }
                    }
                    if (lastNonWs >= 0 && lastNonWs < line.length - 1) {
                        val trailingLength = line.length - 1 - lastNonWs
                        // Allow 0 or exactly 2 trailing whitespace characters.
                        if (trailingLength != 2) {
                            val startOffset = lineStart + lastNonWs + 1
                            val endOffset = lineEnd
                            errors += buildWhitespaceError(
                                fileId = fileId,
                                ruleId = "Whitespace.TrailingSpaces",
                                message = "Line $lineNumber has invalid trailing whitespace",
                                startOffset = startOffset,
                                endOffset = endOffset,
                                lineNumber = lineNumber
                            )
                        }
                    }
                }
            }

            // Move offset to the beginning of the next line.
            offset = lineEnd + 1
        }

        return errors
    }

    private fun buildWhitespaceError(
        fileId: String,
        ruleId: String,
        message: String,
        startOffset: Int,
        endOffset: Int,
        lineNumber: Int
    ): CheckError {
        val start = SourcePosition().apply {
            offset = startOffset
            line = lineNumber
            column = 0
        }
        val end = SourcePosition().apply {
            offset = endOffset
            line = lineNumber
            column = (endOffset - startOffset).coerceAtLeast(0)
        }
        val range = SourceRange().apply {
            this.start = start
            this.end = end
        }

        return CheckError.builder()
            .ruleId(ruleId)
            .message(message)
            .severity(CheckError.Severity.WARNING)
            .fileId(fileId)
            .range(range)
            .build()
    }
}


