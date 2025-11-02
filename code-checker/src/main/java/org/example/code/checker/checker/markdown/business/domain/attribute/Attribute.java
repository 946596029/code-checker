package org.example.code.checker.checker.markdown.business.domain.attribute;

import org.example.code.checker.checker.markdown.business.Domain;
import org.example.code.checker.checker.markdown.business.domain.attribute.object.AttributeObject;

import java.util.List;

public class Attribute extends Domain {
    public String name;
    public String description;

    public List<Domain> subDomainList;

    public AttributeObject customObject;
}
