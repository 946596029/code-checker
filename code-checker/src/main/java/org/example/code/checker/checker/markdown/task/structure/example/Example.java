package org.example.code.checker.checker.markdown.task.structure.example;

import java.util.List;

public class Example {
    
    public static class ExampleItem {
        private String name;
        private String code;

        public ExampleItem(String name, String code) {
            this.name = name;
            this.code = code;
        }

        public String getName() {
            return name;
        }
        
        public String getCode() {
            return code;
        }
    }

    private List<ExampleItem> exampleItems;

    public Example(List<ExampleItem> exampleItems) {
        this.exampleItems = exampleItems;
    }

    public List<ExampleItem> getExampleItems() {
        return exampleItems;
    }
}
