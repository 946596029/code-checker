package org.example.code.checker.checker.markdown.business.domain.attribute.object;

import org.example.code.checker.checker.markdown.business.Domain;
import org.example.code.checker.checker.markdown.business.domain.attribute.Attribute;

import java.util.List;

public class AttributeObject extends Domain {

    public Attribute linkRef;
    public String anchor;
    public String description;

    public List<Attribute> attributes;
}
