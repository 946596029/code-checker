package org.example.code.checker.checker.markdown.task.structure.front.matter;

import java.util.Map;

/**
 * Business Object for FrontMatter.
 * Contains the parsed front matter data with four allowed properties.
 */
public class FrontMatter {
    
    /**
     * The subcategory value from front matter.
     */
    private final String subcategory;
    
    /**
     * The layout value from front matter.
     */
    private final String layout;
    
    /**
     * The page_title value from front matter.
     */
    private final String pageTitle;
    
    /**
     * The description value from front matter.
     */
    private final String description;
    
    public FrontMatter(Map<String, Object> data) {
        this.subcategory = extractStringValue(data, "subcategory");
        this.layout = extractStringValue(data, "layout");
        this.pageTitle = extractStringValue(data, "page_title");
        this.description = extractStringValue(data, "description");
    }
    
    /**
     * Safely extracts a string value from the data map.
     *
     * @param data The data map
     * @param key  The key to extract
     * @return The string value, or null if not found or not a string
     */
    private String extractStringValue(Map<String, Object> data, String key) {
        if (data == null) {
            return null;
        }
        Object value = data.get(key);
        if (value == null) {
            return null;
        }
        return value.toString();
    }
    
    public String getSubcategory() {
        return subcategory;
    }
    
    public String getLayout() {
        return layout;
    }
    
    public String getPageTitle() {
        return pageTitle;
    }
    
    public String getDescription() {
        return description;
    }
}

