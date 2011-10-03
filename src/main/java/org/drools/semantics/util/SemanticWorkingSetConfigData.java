package org.drools.semantics.util;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


public class SemanticWorkingSetConfigData implements Serializable {
    private static final long serialVersionUID = 510l;

    public String name;
    public String description;

    public String[] validFacts;
    public SemanticWorkingSetConfigData[] workingSets;



    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }


    public String[] getValidFacts() {
        return validFacts;
    }

    public void setValidFacts(String[] validFacts) {
        this.validFacts = validFacts;
    }

    public SemanticWorkingSetConfigData[] getWorkingSets() {
        return workingSets;
    }

    public void setWorkingSets(SemanticWorkingSetConfigData[] workingSets) {
        this.workingSets = workingSets;
    }


}


