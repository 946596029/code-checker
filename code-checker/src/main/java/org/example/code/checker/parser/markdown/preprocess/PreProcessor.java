package org.example.code.checker.parser.markdown.preprocess;

import org.example.code.checker.parser.markdown.preprocess.rule.PreProcessRule;

import java.util.ArrayList;
import java.util.List;

public class PreProcessor {

    private String rawText;
    private String resultText;
    private List<PreProcessRule> rules;

    public PreProcessor(String rawText) {
        this.rawText = rawText;
        this.resultText = "";
        this.rules = new ArrayList<>();
    }

    public void addRules(List<PreProcessRule> rules) {
        this.rules.addAll(rules);
    }

    public void addRule(PreProcessRule rule) {
        this.rules.add(rule);
    }

    public void deleteRule(PreProcessRule rule) {
        this.rules.remove(rule);
    }

    public void clearRules() {
        this.rules.clear();
    }

    public List<PreProcessRule> getRules() {
        return this.rules;
    }

    public String getRawText() {
        return this.rawText;
    }

    public String process() {
        String text = this.rawText;
        for (PreProcessRule rule : this.rules) {
            text = rule.preProcess(text);
        }
        this.resultText = text;
        return text;
    }
}