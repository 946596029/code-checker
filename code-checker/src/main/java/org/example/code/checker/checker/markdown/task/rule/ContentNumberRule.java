package org.example.code.checker.checker.markdown.task.rule;

import org.example.code.checker.checker.Checker;
import org.example.code.checker.checker.common.CheckContext;
import org.example.code.checker.checker.common.CheckError;
import org.example.code.checker.checker.markdown.parser.ast.MdAstNode;
import org.example.code.checker.checker.markdown.task.structure.arguments.Argument;
import org.example.code.checker.checker.markdown.task.structure.arguments.ArgumentList;
import org.example.code.checker.checker.markdown.task.structure.attributes.Attribute;
import org.example.code.checker.checker.markdown.task.structure.attributes.AttributeList;
import org.example.code.checker.checker.markdown.task.structure.title.Title;
import org.example.code.checker.checker.utils.TreeNode;
import org.example.flow.engine.node.TaskData;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Checker for number formatting rules in content.
 * This checker is special because it needs to check multiple data structures,
 * not just the document AST.
 */
public class ContentNumberRule extends Checker {

    private static final Pattern NUMBER_PATTERN = Pattern.compile("\\b\\d+(?:\\.\\d+)?\\b");

    private final NumberBacktickRule backtickRule = new NumberBacktickRule();
    private final NumberThousandSeparatorRule thousandSeparatorRule = new NumberThousandSeparatorRule();

    @Override
    public List<TaskData<?>> task(Map<String, TaskData<?>> input) {
        List<CheckError> errors = new ArrayList<>();

        // Create a minimal context for error reporting
        TaskData<?> documentData = input.get("originalDocument");
        TreeNode<MdAstNode> document = null;
        if (documentData != null) {
            @SuppressWarnings("unchecked")
            TreeNode<MdAstNode> doc = (TreeNode<MdAstNode>) documentData.getPayload();
            document = doc;
        }

        TaskData<?> fileIdData = input.get("fileId");
        String fileId = fileIdData != null ? (String) fileIdData.getPayload() : null;

        CheckContext context = new CheckContext(document, fileId, input, "ContentNumberRule");

        // Check Title
        Title title = context.getInputData("titleResult", Title.class);
        if (title != null) {
            checkText("Title.title", title.getTitle(), context, errors);
            checkText("Title.description", title.getDescription(), context, errors);
        }

        // Check ArgumentList
        ArgumentList argumentList = context.getInputData("argumentListResult", ArgumentList.class);
        if (argumentList != null) {
            checkText("ArgumentList.title", argumentList.getTitle(), context, errors);
            checkText("ArgumentList.description", argumentList.getDescription(), context, errors);

            if (argumentList.getArguments() != null) {
                for (int i = 0; i < argumentList.getArguments().size(); i++) {
                    Argument argument = argumentList.getArguments().get(i);
                    checkText(String.format("Argument[%d].description", i),
                            argument.getDescription(), context, errors);
                }
            }
        }

        // Check AttributeList
        AttributeList attributeList = context.getInputData("attributeListResult", AttributeList.class);
        if (attributeList != null) {
            checkText("AttributeList.title", attributeList.getTitle(), context, errors);
            checkText("AttributeList.description", attributeList.getDescription(), context, errors);

            if (attributeList.getAttributes() != null) {
                for (int i = 0; i < attributeList.getAttributes().size(); i++) {
                    Attribute attribute = attributeList.getAttributes().get(i);
                    checkText(String.format("Attribute[%d].description", i),
                            attribute.getDescription(), context, errors);
                }
            }
        }

        // Return result
        List<TaskData<?>> output = new ArrayList<>();
        if (errors.size() > 0) {
            setErrorList(errors);
            setNeedStop(true);
            return null;
        }

        return output;
    }

    /**
     * Checks a text string for number formatting issues using rules.
     */
    private void checkText(String fieldName, String text, CheckContext context, List<CheckError> errors) {
        if (text == null || text.trim().isEmpty()) {
            return;
        }

        // First check for backticks (needed for thousand separator check)
        List<CheckError> backtickErrors = backtickRule.checkText(fieldName, text, context);
        errors.addAll(backtickErrors);

        // Check for thousand separators (the rule will check each number individually)
        // We need to check each number to see if it's wrapped
        Matcher numberMatcher = NUMBER_PATTERN.matcher(text);
        while (numberMatcher.find()) {
            int start = numberMatcher.start();
            int end = numberMatcher.end();
            String numberStr = numberMatcher.group();

            boolean isBacktickWrapped = backtickRule.isWrappedInBackticks(text, start, end);
            boolean isBoldWrapped = backtickRule.isWrappedInBold(text, start, end);

            List<CheckError> separatorErrors = thousandSeparatorRule.checkText(
                    fieldName, text, isBacktickWrapped, isBoldWrapped, context);
            errors.addAll(separatorErrors);
        }
    }
}

