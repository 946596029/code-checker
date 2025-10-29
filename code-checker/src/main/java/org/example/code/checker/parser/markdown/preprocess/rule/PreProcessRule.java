package org.example.code.checker.parser.markdown.preprocess.rule;

import org.example.code.checker.parser.markdown.preprocess.IPreProcess;

public class PreProcessRule implements IPreProcess {

    protected String name;

    public PreProcessRule(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    public String preProcess(String text) {
        // not implement
        return text;
    }
}