/*
 * Copyright 2011 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.semantics.builder.model;

import org.drools.definition.type.Position;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Concept {

    @Position(0)    private     String              iri;
    @Position(1)    private     String              name;
    @Position(2)    private     Set<Concept>        superConcepts;
    @Position(3)    private     Map                 properties;
    @Position(4)    private     Set<Concept>        equivalentConcepts;

                    private     boolean             primitive               = false;



    public Concept(String iri, String name) {
        this.iri = iri;
        this.name = name;
        this.superConcepts = new HashSet();
        this.properties = new HashMap();
        this.equivalentConcepts = new HashSet();
    }

    public Concept(String iri, String name, Set superConcepts, Map properties, Set equivalentConcepts ) {
        this.iri = iri;
        this.name = name;
        this.superConcepts = superConcepts;
        this.properties = properties;
        this.equivalentConcepts = equivalentConcepts;
    }



    @Override
    public String toString() {
        return name;
    }


    public String toFullString() {
        String supers = "[";
        for ( Object o : superConcepts ) {
            Concept con = (Concept) o;
            supers += con.iri + ",";
        }
        supers += "]";
        return "Concept{" +
                "iri='" + iri + '\'' +
                ", name='" + name + '\'' +
                supers +
//                ", properties=" + properties +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Concept concept = (Concept) o;

        if (iri != null ? !iri.equals(concept.iri) : concept.iri != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return iri != null ? iri.hashCode() : 0;
    }


    public String getIri() {
        return iri;
    }

    public void setIri(String iri) {
        this.iri = iri;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set<Concept> getSuperConcepts() {
        return superConcepts;
    }

    public void setSuperConcepts(Set<Concept> superConcepts) {
        this.superConcepts = superConcepts;
    }

    public Map getProperties() {
        return properties;
    }

    public void setProperties(Map properties) {
        this.properties = properties;
    }

    public Set<Concept> getEquivalentConcepts() {
        return equivalentConcepts;
    }

    public void setEquivalentConcepts(Set<Concept> equivalentConcepts) {
        this.equivalentConcepts = equivalentConcepts;
    }

    public boolean isPrimitive() {
        return primitive;
    }

    public void setPrimitive(boolean primitive) {
        this.primitive = primitive;
    }
}


