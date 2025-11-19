package org.example.code.checker.checker.markdown.task.checker.structrue.front.matter

import org.example.code.checker.checker.common.CheckError
import org.example.code.checker.checker.markdown.domain.MdDomain
import org.example.code.checker.checker.markdown.domain.standard.block.Heading
import org.example.code.checker.checker.utils.TreeNode
import org.example.flow.engine.node.TaskData
import org.example.flow.engine.node.TaskDataUtils
import org.example.flow.engine.node.TaskNode

/**
 * Front matter checking and consuming rule.
 *
 * Responsibilities:
 * - Analyse the YAML-like front matter block at the top of the markdown file.
 * - Validate that the description field follows the expected pattern.
 * - Remove front matter blocks from the working document tree so that
 *   downstream rules (e.g. TitleChecker) only see the body.
 *
 * Input:
 * - rawCode:          TaskData<String>             – entire markdown source.
 * - originalDocument: TaskData<TreeNode<MdDomain>> – original document tree (read-only).
 * - workingDocument:  TaskData<TreeNode<MdDomain>> – working document tree to be mutated.
 * - fileId:           TaskData<String>             – file identifier or path.
 *
 * Output:
 * - TaskData<TreeNode<MdDomain>> – updated working document tree.
 * - TaskData<List<CheckError>>   – front-matter related errors.
 */
class FrontMatterChecker : TaskNode() {

    override fun task(input: Map<String, TaskData<*>>?): List<TaskData<*>> {
        if (input == null || input.isEmpty()) {
            throw IllegalArgumentException("input is null or empty")
        }

        val rawCode: String =
            TaskDataUtils.getPayload(input, "rawCode", String::class.java)

        @Suppress("UNCHECKED_CAST")
        val originalDocument: TreeNode<MdDomain> =
            TaskDataUtils.getPayload(input, "originalDocument", TreeNode::class.java) as TreeNode<MdDomain>

        @Suppress("UNCHECKED_CAST")
        val workingDocument: TreeNode<MdDomain> =
            TaskDataUtils.getPayload(input, "workingDocument", TreeNode::class.java) as TreeNode<MdDomain>

        val fileId: String =
            TaskDataUtils.getPayload(input, "fileId", String::class.java)

        val errors = mutableListOf<CheckError>()

        // 1. Text-level validation for front matter (mainly description field).
        errors += checkFrontMatterDescription(fileId, rawCode)

        // 2. Structural consumption: remove front-matter blocks from working tree.
        consumeFrontMatter(workingDocument)

        val now = System.currentTimeMillis()
        val outputs = mutableListOf<TaskData<*>>()

        // Updated working document.
        outputs += TaskData(
            TreeNode::class.java,
            "${FrontMatterChecker::class.java.simpleName}.workingDocument",
            now,
            workingDocument
        )

        // Front-matter check results (can be empty).
        outputs += TaskData(
            List::class.java,
            "${FrontMatterChecker::class.java.simpleName}.errors",
            now,
            errors.toList()
        )

        return outputs
    }

    /**
     * Validate the description block in YAML-like front matter.
     *
     * Expectation (simplified based on doc_rule.md):
     * - There is a front matter block at the very top, delimited by lines "---".
     * - Inside the block there is a description field with the following shape:
     *
     *   description: |-
     *     Use this (data source|resource) to ... within HuaweiCloud.
     */
    private fun checkFrontMatterDescription(
        fileId: String,
        source: String
    ): List<CheckError> {
        val errors = mutableListOf<CheckError>()
        if (source.isEmpty()) {
            return errors
        }

        val lines = source.split('\n')
        if (lines.isEmpty()) {
            return errors
        }

        // Locate front matter block: between first and second '---' at top of file.
        if (!lines[0].trim().startsWith("---")) {
            return errors // no front matter; nothing to check.
        }

        var secondDelimiterIndex = -1
        for (i in 1 until lines.size) {
            if (lines[i].trim().startsWith("---")) {
                secondDelimiterIndex = i
                break
            }
        }
        if (secondDelimiterIndex <= 0) {
            return errors // malformed or incomplete front matter; skip strict checks.
        }

        val frontMatterLines = lines.subList(1, secondDelimiterIndex)

        // Find the 'description: |-' line and the first non-empty line after it.
        var descriptionLineIndex = -1
        for (i in frontMatterLines.indices) {
            if (frontMatterLines[i].trim().startsWith("description:")) {
                descriptionLineIndex = i
                break
            }
        }
        if (descriptionLineIndex < 0) {
            errors += CheckError.builder()
                .ruleId("FrontMatter.MissingDescription")
                .message("Front matter description field is missing")
                .severity(CheckError.Severity.ERROR)
                .fileId(fileId)
                .range(null)
                .nodeId(null)
                .nodeType("FrontMatter")
                .build()
            return errors
        }

        val descHeader = frontMatterLines[descriptionLineIndex].trim()
        if (!descHeader.matches(Regex("""^description:\s*\|-\s*$"""))) {
            errors += CheckError.builder()
                .ruleId("FrontMatter.DescriptionFormat")
                .message("Front matter description must use block scalar syntax: 'description: |-'")
                .severity(CheckError.Severity.ERROR)
                .fileId(fileId)
                .range(null)
                .nodeId(null)
                .nodeType("FrontMatter")
                .build()
            return errors
        }

        var descriptionText: String? = null
        for (i in (descriptionLineIndex + 1) until frontMatterLines.size) {
            val line = frontMatterLines[i]
            if (line.isBlank()) continue
            // Remove leading indentation (commonly two spaces).
            descriptionText = line.trim()
            break
        }

        if (descriptionText == null || descriptionText.isEmpty()) {
            errors += CheckError.builder()
                .ruleId("FrontMatter.EmptyDescription")
                .message("Front matter description block is empty")
                .severity(CheckError.Severity.ERROR)
                .fileId(fileId)
                .range(null)
                .nodeId(null)
                .nodeType("FrontMatter")
                .build()
            return errors
        }

        val normalized = descriptionText.trim()

        // Very lightweight semantic pattern: "Use this (data source|resource) to ... within HuaweiCloud."
        val pattern = Regex(
            pattern = """^Use this\s+(data source|resource)\s+to\s+.+\s+within\s+HuaweiCloud\.\s*$""",
            option = RegexOption.IGNORE_CASE
        )

        if (!pattern.matches(normalized)) {
            errors += CheckError.builder()
                .ruleId("FrontMatter.DescriptionSemantic")
                .message("Front matter description should be 'Use this (data source|resource) to ... within HuaweiCloud.'")
                .severity(CheckError.Severity.WARNING)
                .fileId(fileId)
                .range(null)
                .nodeId(null)
                .nodeType("FrontMatter")
                .build()
        }

        return errors
    }

    /**
     * Remove the leading block-level nodes that represent front matter
     * from the working document tree.
     *
     * Current heuristic:
     * - Front matter is any sequence of top-level blocks before the first
     *   level-1 heading (Heading level == 1).
     * - These nodes are removed from the root's children list.
     */
    private fun consumeFrontMatter(workingDocument: TreeNode<MdDomain>) {
        val children = workingDocument.children ?: return
        if (children.isEmpty()) return

        var firstHeadingIndex = -1
        for (i in children.indices) {
            val data = children[i].data
            if (data is Heading && data.level == 1) {
                firstHeadingIndex = i
                break
            }
        }

        if (firstHeadingIndex <= 0) {
            // No heading or heading already at the top; nothing to consume.
            return
        }

        val toRemove = children.subList(0, firstHeadingIndex).toList()
        for (child in toRemove) {
            workingDocument.removeChild(child)
        }
    }
}


