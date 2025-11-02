package org.example.code.checker.checker.markdown.business.domain.arguement;

import org.example.code.checker.checker.markdown.business.Domain;

import java.util.List;

public class Argument extends Domain {
    public String name;
    public List<String> tags;
    public String description;

    public List<Domain> subDomainList;
}
