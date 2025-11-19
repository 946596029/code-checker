package org.example.code.checker.checker.markdown.task.checker.structrue.title

import org.example.code.checker.checker.common.CheckError
import org.example.code.checker.checker.markdown.domain.MdDomain
import org.example.code.checker.checker.markdown.domain.standard.block.Heading
import org.example.code.checker.checker.markdown.domain.standard.block.Paragraph
import org.example.code.checker.checker.markdown.domain.standard.inline.CodeSpan
import org.example.code.checker.checker.markdown.domain.standard.inline.Text
import org.example.code.checker.checker.utils.TreeNode
import org.example.flow.engine.node.TaskData
import org.example.flow.engine.node.TaskDataUtils
import org.example.flow.engine.node.TaskNode

/**
 * Title structure checking rule.
 *
 * This node validates that the "title section" of a document is complete.
 * For a Terraform-style resource document such as `huaweicloud_cdn_ip_information.md`,
 * the title section is defined as:
 *
 * - Starts from the first level‑1 heading (`# ...`).
 * - Ends right before the first level‑2 heading (`## ...`).
 * - Lines starting with `->` inside this section are optional tips and are not required.
 *
 * Rules checked:
 * - A level‑1 heading must exist in the document.
 * - Between the first H1 and the first H2, there must be at least one non‑empty
 *   paragraph, which is treated as the title description.
 *
 * Input:
 * - workingDocument: TaskData<TreeNode<MdDomain>> – document tree after
 *   FrontMatter has been consumed.
 * - fileId:          TaskData<String>              – file identifier or path.
 *
 * Output:
 * - TaskData<List<CheckError>> – all title-related errors (empty if title is complete).
 */
class TitleChecker : TaskNode() {

    override fun task(input: Map<String, TaskData<*>>?): List<TaskData<*>> {
        if (input == null || input.isEmpty()) {
            throw IllegalArgumentException("input is null or empty")
        }

        @Suppress("UNCHECKED_CAST")
        val document: TreeNode<MdDomain> =
            TaskDataUtils.getPayload(input, "workingDocument", TreeNode::class.java) as TreeNode<MdDomain>
        val fileId: String =
            TaskDataUtils.getPayload(input, "fileId", String::class.java)

        val errors = mutableListOf<CheckError>()

        // 1. Find first H1 heading using TreeNode.Query for type-safe filtering.
        val h1Opt = document
            .query()
            .all()
            .filter(Heading::class.java) { it.level == 1 }
            .first()

        if (!h1Opt.isPresent) {
            errors += CheckError.builder()
                .ruleId("Title.MissingH1")
                .message("Level-1 heading (title) is missing in document")
                .severity(CheckError.Severity.ERROR)
                .fileId(fileId)
                .range(document.data.range)
                .nodeId(null)
                .nodeType("Title")
                .build()
        } else {
            val h1Node: TreeNode<MdDomain> = h1Opt.get()

            // Flatten nodes in pre-order to reason about relative positions.
            val allNodes: List<TreeNode<MdDomain>> = document.query().all().list()

            val h1Index = allNodes.indexOf(h1Node)

            // 2. Find first H2 heading after this H1; if not found, the title section
            //    extends to the end of the document.
            var h2Index = -1
            for (i in (h1Index + 1) until allNodes.size) {
                val data = allNodes[i].data
                if (data is Heading && data.level == 2) {
                    h2Index = i
                    break
                }
            }

            val sectionEndExclusive =
                if (h2Index >= 0) h2Index else allNodes.size

            // 3. Search for the first non-empty paragraph between H1 and the first H2.
            var foundDescription = false
            for (i in (h1Index + 1) until sectionEndExclusive) {
                val node = allNodes[i]
                val data = node.data
                if (data is Paragraph) {
                    val text = collectInlineText(node).trim()
                    if (text.isNotEmpty()) {
                        foundDescription = true
                        break
                    }
                }
            }

            if (!foundDescription) {
                errors += CheckError.builder()
                    .ruleId("Title.MissingDescription")
                    .message("Title description paragraph after H1 is missing before the first H2")
                    .severity(CheckError.Severity.ERROR)
                    .fileId(fileId)
                    .range(h1Node.data.range)
                    .nodeId(null)
                    .nodeType("Title")
                    .build()
            }
        }

        val errorsData = TaskData(
            List::class.java,
            TitleChecker::class.java.simpleName,
            System.currentTimeMillis(),
            errors.toList()
        )

        return listOf(errorsData)
    }

    /**
     * Collect inline text from a paragraph (or any inline container) node.
     * Text content and inline code spans are concatenated in reading order.
     */
    private fun collectInlineText(node: TreeNode<MdDomain>): String {
        val sb = StringBuilder()
        appendInlineText(node, sb)
        return sb.toString()
    }

    private fun appendInlineText(node: TreeNode<MdDomain>?, out: StringBuilder) {
        if (node == null) return

        val data = node.data
        when (data) {
            is Text -> out.append(data.content)
            is CodeSpan -> out.append(data.code)
            else -> {
                val children = node.children ?: return
                for (child in children) {
                    appendInlineText(child, out)
                }
            }
        }
    }
}


