package org.example.code.checker.checker.markdown.task.checker.structrue.front.matter;

import org.example.code.checker.checker.CheckError;
import org.example.flow.engine.node.TaskData;
import org.example.flow.engine.node.TaskDataUtils;
import org.example.flow.engine.node.TaskNode;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Check FrontMatter block at the beginning of markdown document.
 * <p>
 * Rules:
 * <ul>
 *     <li>Document must contain a YAML FrontMatter block delimited by {@code ---}.</li>
 *     <li>FrontMatter keys must match the attributes defined in
 *     {@code huaweicloud_cdn_ip_information.md}:
 *     {@code subcategory}, {@code layout}, {@code page_title}, {@code description}.</li>
 * </ul>
 * <p>
 * On success this task returns {@code TaskData<FrontMatter>} with parsed values.
 * On failure it returns {@code TaskData<CheckError>} describing the problem.
 */
public class FrontMatterChecker extends TaskNode {

    private static final Set<String> REQUIRED_KEYS = new HashSet<>(
        Arrays.asList("subcategory", "layout", "page_title", "description")
    );

    @Override
    public TaskData<?> task(Map<String, TaskData<?>> input) {
        if (input == null || input.isEmpty()) {
            throw new IllegalArgumentException("input is null or empty");
        }

        String rawCode = TaskDataUtils.getPayload(input, "rawCode", String.class);
        String fileId = TaskDataUtils.getPayload(input, "fileId", String.class);

        Set<String> frontMatterKeys = extractFrontMatterKeys(rawCode);
        if (frontMatterKeys.isEmpty()) {
            CheckError error = new CheckError(
                "FrontMatter.Missing",
                "FrontMatter block is missing in markdown document",
                CheckError.Severity.ERROR,
                fileId,
                null,
                null,
                "FrontMatter"
            );
            return new TaskData<>(
                CheckError.class,
                FrontMatterChecker.class.getSimpleName(),
                System.currentTimeMillis(),
                error
            );
        }

        Set<String> missing = new HashSet<>(REQUIRED_KEYS);
        missing.removeAll(frontMatterKeys);
        if (!missing.isEmpty()) {
            CheckError error = new CheckError(
                "FrontMatter.MissingKeys",
                "FrontMatter keys are not consistent with template. Missing keys: " + missing,
                CheckError.Severity.ERROR,
                fileId,
                null,
                null,
                "FrontMatter"
            );
            return new TaskData<>(
                CheckError.class,
                FrontMatterChecker.class.getSimpleName(),
                System.currentTimeMillis(),
                error
            );
        }

        FrontMatter frontMatter = parseFrontMatter(rawCode);

        return new TaskData<>(
            FrontMatter.class,
            FrontMatterChecker.class.getSimpleName(),
            System.currentTimeMillis(),
            frontMatter
        );
    }

    /**
     * Extract FrontMatter keys from the given markdown text.
     * The FrontMatter is expected to be a YAML block at the beginning:
     *
     * <pre>
     * ---
     * key1: value
     * key2: "value"
     * ...
     * ---
     * </pre>
     *
     * @param markdown full markdown content
     * @return set of key names found in FrontMatter, or empty set if block not found
     */
    private Set<String> extractFrontMatterKeys(String markdown) {
        Set<String> keys = new HashSet<>();
        if (markdown == null || markdown.isEmpty()) {
            return keys;
        }

        String[] lines = markdown.split("\\r?\\n");
        if (lines.length == 0) {
            return keys;
        }

        int idx = 0;
        // FrontMatter must start at the very beginning with '---'
        if (!"---".equals(lines[idx].trim())) {
            return keys;
        }
        idx++;

        for (; idx < lines.length; idx++) {
            String line = lines[idx].trim();
            if ("---".equals(line)) {
                // End of FrontMatter block
                break;
            }
            if (line.isEmpty() || line.startsWith("#")) {
                continue;
            }
            int colon = line.indexOf(':');
            if (colon <= 0) {
                continue;
            }
            String key = line.substring(0, colon).trim();
            if (!key.isEmpty()) {
                keys.add(key);
            }
        }

        return keys;
    }

    /**
     * Parse FrontMatter values from markdown and build {@link FrontMatter}.
     * Only the fields we care about (subcategory, layout, page_title, description)
     * are extracted.
     */
    private FrontMatter parseFrontMatter(String markdown) {
        if (markdown == null || markdown.isEmpty()) {
            return null;
        }

        String[] lines = markdown.split("\\r?\\n");
        if (lines.length == 0) {
            return null;
        }

        int idx = 0;
        if (!"---".equals(lines[idx].trim())) {
            return null;
        }
        idx++;

        String subcategory = null;
        String layout = null;
        String pageTitle = null;
        String description = null;

        boolean inDescriptionBlock = false;
        StringBuilder descBuilder = null;

        for (; idx < lines.length; idx++) {
            String rawLine = lines[idx];
            String line = rawLine.trim();

            if ("---".equals(line)) {
                break;
            }

            if (inDescriptionBlock) {
                if (line.startsWith("---")) {
                    break;
                }
                if (rawLine.startsWith(" ") || rawLine.startsWith("\t")) {
                    if (descBuilder.length() > 0) {
                        descBuilder.append('\n');
                    }
                    descBuilder.append(line);
                    continue;
                } else {
                    break;
                }
            }

            if (line.isEmpty() || line.startsWith("#")) {
                continue;
            }

            int colon = line.indexOf(':');
            if (colon <= 0) {
                continue;
            }
            String key = line.substring(0, colon).trim();
            String valuePart = line.substring(colon + 1).trim();

            if ("subcategory".equals(key)) {
                subcategory = stripYamlQuotes(valuePart);
            } else if ("layout".equals(key)) {
                layout = stripYamlQuotes(valuePart);
            } else if ("page_title".equals(key)) {
                pageTitle = stripYamlQuotes(valuePart);
            } else if ("description".equals(key)) {
                if (valuePart.startsWith("|")) {
                    inDescriptionBlock = true;
                    descBuilder = new StringBuilder();
                } else {
                    description = stripYamlQuotes(valuePart);
                }
            }
        }

        if (description == null && descBuilder != null) {
            description = descBuilder.toString().trim();
        }

        return new FrontMatter(subcategory, layout, pageTitle, description);
    }

    private String stripYamlQuotes(String value) {
        if (value == null || value.isEmpty()) {
            return value;
        }
        if ((value.startsWith("\"") && value.endsWith("\""))
            || (value.startsWith("'") && value.endsWith("'"))) {
            return value.substring(1, value.length() - 1);
        }
        return value;
    }
}
