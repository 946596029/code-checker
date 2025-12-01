package org.example.code.checker.checker.markdown.task.structure;

import org.example.code.checker.checker.RuleBasedChecker;
import org.example.code.checker.checker.common.CheckRule;
import org.example.code.checker.checker.markdown.task.rule.LineFormattingRule;
import org.example.code.checker.checker.markdown.task.rule.SectionOrderRule;

import java.util.List;

/**
 * Structure checker that validates document structure and formatting.
 */
public class StructureChecker extends RuleBasedChecker {

    @Override
    protected String getCheckerName() {
        return "StructureChecker";
    }

    @Override
    protected List<CheckRule> getRules() {
        return List.of(
                new SectionOrderRule(),
                new LineFormattingRule());
    }

}

