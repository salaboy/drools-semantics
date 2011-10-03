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

package org.drools.semantics.builder.model.inference;

import com.sun.deploy.util.Property;
import org.drools.io.Resource;
import org.drools.runtime.StatefulKnowledgeSession;
import org.drools.semantics.builder.DLUtils;
import org.drools.semantics.builder.model.*;
import org.semanticweb.HermiT.Reasoner;
import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.util.*;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class DelegateInferenceStrategy_backup implements ModelInferenceStrategy {



    private static Map<String, Concept> primitives = new HashMap<String, Concept>();

    {
        Concept con;

        IRI i1 = IRI.create("http://www.w3.org/2001/XMLSchema#string");
        con = new Concept( i1.toQuotedString(), "java.lang.String" );
        primitives.put( i1.toQuotedString(), con );

        IRI i2 = IRI.create("http://www.w3.org/2001/XMLSchema#dateTime");
        con = new Concept( i2.toQuotedString(), "java.lang.Date" );
        primitives.put( i2.toQuotedString(), con );

        IRI i3 = IRI.create("http://www.w3.org/2001/XMLSchema#int");
        con = new Concept( i3.toQuotedString(), "java.lang.Integer" );
        primitives.put( i3.toQuotedString(), con );
    }


    public OntoModel buildModel(OWLOntology ontoDescr, Map<InferenceTask, Resource> theory, StatefulKnowledgeSession kSession) {

        OntoModel model = ModelFactory.newModel(ModelFactory.CompileTarget.BASE);
        OWLDataFactory factory = ontoDescr.getOWLOntologyManager().getOWLDataFactory();

        InferredOntologyGenerator reasoner = initReasoner( ontoDescr );


        reasoner.fillOntology( ontoDescr.getOWLOntologyManager(), ontoDescr );
        boolean dirty = false;




        final Map<OWLProperty, Set<OWLClassExpression> > domains = new HashMap<OWLProperty, Set<OWLClassExpression>>();
        final Map<OWLProperty, Set<OWLClassExpression> > ranges = new HashMap<OWLProperty, Set<OWLClassExpression>>();
        final Map<OWLProperty, Set<OWLDataRange> > dataRanges = new HashMap<OWLProperty, Set<OWLDataRange>>();
        final Map<OWLProperty, PropertyRelation> properties = new HashMap<OWLProperty, PropertyRelation>();


        // Complete missing domains and ranges for properties

        for ( OWLDataProperty dp : ontoDescr.getDataPropertiesInSignature() ) {
            if ( ! dp.isOWLTopDataProperty() && dp.getDomains( ontoDescr ).isEmpty() ) {
                ontoDescr.getOWLOntologyManager().applyChange( new AddAxiom( ontoDescr, factory.getOWLDataPropertyDomainAxiom( dp, factory.getOWLThing() ) ) );
                addDomain( domains, dp, factory.getOWLThing() );
            }
        }

        for ( OWLObjectProperty op : ontoDescr.getObjectPropertiesInSignature() ) {
            if ( ! op.isOWLTopObjectProperty() && op.getDomains( ontoDescr ).isEmpty() ) {
                ontoDescr.getOWLOntologyManager().applyChange( new AddAxiom( ontoDescr, factory.getOWLObjectPropertyDomainAxiom( op, factory.getOWLThing() ) ) );
                addRange( ranges, op, factory.getOWLThing());
            }
        }

        for ( OWLDataProperty dp : ontoDescr.getDataPropertiesInSignature() ) {
            if ( ! dp.isOWLTopDataProperty() && dp.getRanges( ontoDescr ).isEmpty() ) {
                ontoDescr.getOWLOntologyManager().applyChange( new AddAxiom( ontoDescr, factory.getOWLDataPropertyRangeAxiom( dp, factory.getTopDatatype() ) ) );
                addDataRange(dataRanges, dp, factory.getTopDatatype() );
            }
        }

        for ( OWLObjectProperty op : ontoDescr.getObjectPropertiesInSignature() ) {
            if ( ! op.isOWLTopObjectProperty() && op.getRanges( ontoDescr ).isEmpty() ) {
                ontoDescr.getOWLOntologyManager().applyChange( new AddAxiom( ontoDescr, factory.getOWLObjectPropertyRangeAxiom( op, factory.getOWLThing() ) ) );
                addDomain( domains, op, factory.getOWLThing() );

            }
        }




        // Ensure that property domains and ranges are effectively defined --> define anonymous ones



        for ( OWLDataProperty dp : ontoDescr.getDataPropertiesInSignature() ) {
            String propIri = dp.getIRI().toQuotedString();
            String propName = DLUtils.getInstance().buildLowCaseNameFromIri( dp.getIRI() );
            String typeName = DLUtils.getInstance().buildNameFromIri( dp.getIRI() );

            for (OWLClassExpression dom : dp.getDomains( ontoDescr )) {
                for (OWLDataRange ran : dp.getRanges( ontoDescr )) {
                    String domainIri = dom.asOWLClass().getIRI().toQuotedString();
                    String ranIri = ran.toString();
                    if ( dom.isAnonymous() ) {
                        OWLClass domain = factory.getOWLClass(IRI.create( typeName + "Domain") );
                        domainIri = domain.getIRI().toQuotedString();
                        ontoDescr.getOWLOntologyManager().applyChange( new AddAxiom( ontoDescr, factory.getOWLDeclarationAxiom( domain ) ) );
                        ontoDescr.getOWLOntologyManager().applyChange( new AddAxiom(ontoDescr, factory.getOWLEquivalentClassesAxiom(domain, dom)));
                        dirty = true;
                    }

                    model.addProperty( new PropertyRelation( domainIri, propIri, ran.asOWLDatatype().getIRI().toQuotedString(), propName ) );
                    addDomain( domains, dp, dom.asOWLClass() );
                }
            }

        }

        for ( OWLObjectProperty op : ontoDescr.getObjectPropertiesInSignature() ) {
            String propIri = op.getIRI().toQuotedString();
            String propName = DLUtils.getInstance().buildLowCaseNameFromIri( op.getIRI() );
            String typeName = DLUtils.getInstance().buildNameFromIri( op.getIRI() );


            for (OWLClassExpression dom : op.getDomains( ontoDescr )) {
                for (OWLClassExpression ran : op.getRanges( ontoDescr )) {
                    String domainIri = dom.asOWLClass().getIRI().toQuotedString();
                    String rangeIri = ran.asOWLClass().getIRI().toQuotedString();

                    if ( dom.isAnonymous() ) {
                        OWLClass domain = factory.getOWLClass(IRI.create( typeName + "Domain") );
                        domainIri = domain.getIRI().toQuotedString();
                        ontoDescr.getOWLOntologyManager().applyChange( new AddAxiom( ontoDescr, factory.getOWLDeclarationAxiom( domain ) ) );
                        ontoDescr.getOWLOntologyManager().applyChange(new AddAxiom( ontoDescr, factory.getOWLEquivalentClassesAxiom( domain, dom ) ) );
                        dirty = true;
                    }

                    if ( ran.isAnonymous() ) {
                        OWLClass range = factory.getOWLClass(IRI.create( typeName + "Range") );
                        rangeIri = range.getIRI().toQuotedString();
                        ontoDescr.getOWLOntologyManager().applyChange( new AddAxiom( ontoDescr, factory.getOWLDeclarationAxiom( range ) ) );
                        ontoDescr.getOWLOntologyManager().applyChange(new AddAxiom( ontoDescr, factory.getOWLEquivalentClassesAxiom( range, ran ) ) );
                        dirty = true;
                    }

                    model.addProperty( new PropertyRelation( domainIri, propIri, rangeIri, propName ) );
//                    properties.put( op, new PropertyRelation( domainIri, propIri, rangeIri, propName ) );
                    addDomain(domains, op, dom.asOWLClass());

                }
            }

        }



//        for ( OWLClass klass : ontoDescr.getClassesInSignature() ) {
//            for ( OWLClassExpression clax : klass.getSuperClasses( ontoDescr ) ) {
//                clax = clax.getNNF();
//                final OWLClass inKlass = klass;
//                clax.accept( new OWLClassExpressionVisitor() {
//
//                    private void process( OWLClassExpression expr ) {
//                        if ( expr instanceof OWLNaryBooleanClassExpression ) {
//                            for (OWLClassExpression clax : ((OWLNaryBooleanClassExpression) expr).getOperandsAsList() ) {
//                                process( clax );
//                            }
//                        } else if ( expr instanceof OWLQuantifiedObjectRestriction ) {
//                            OWLQuantifiedObjectRestriction rest = (OWLQuantifiedObjectRestriction) expr;
//                            OWLObjectProperty prop = rest.getProperty().asOWLObjectProperty();
//                            addDomain( domains, prop, inKlass );
//                            OWLClassExpression fil = rest.getFiller();
//                            if ( fil instanceof OWLObjectComplementOf ) {
//                                fil = ((OWLObjectComplementOf) fil ).getOperand();
//                            }
//                            addRange( ranges, prop, rest.getFiller().asOWLClass() );
//                            process( fil );
//                        } else if ( expr instanceof  OWLQuantifiedDataRestriction ) {
//                            OWLQuantifiedDataRestriction rest = (OWLQuantifiedDataRestriction) expr;
//                            addDomain(domains, ((OWLQuantifiedDataRestriction) expr).getProperty().asOWLDataProperty(), inKlass);
//                            OWLDataRange fil = rest.getFiller();
//                            if ( fil instanceof OWLDataComplementOf ) {
//                                fil = ((OWLDataComplementOf) rest.getFiller()).getDataRange();
//                            }
//                            if ( fil instanceof OWLDataOneOf ) {
//                                return;
//                            }
//                            addDataRange(dataRanges, ((OWLQuantifiedDataRestriction) expr).getProperty().asOWLDataProperty(), fil.asOWLDatatype());
//                        }
//                        else return;
//                    }
//
//                    public void visit(OWLClass ce) {
//                        process( ce );
//                    }
//
//                    public void visit(OWLObjectIntersectionOf ce) {
//                        process( ce );
//                    }
//
//                    public void visit(OWLObjectUnionOf ce) {
//                        process( ce );
//                    }
//
//                    public void visit(OWLObjectComplementOf ce) {
//                        process( ce );
//                    }
//
//                    public void visit(OWLObjectSomeValuesFrom ce) {
//                        process(ce);
//                    }
//
//                    public void visit(OWLObjectAllValuesFrom ce) {
//                        process(ce);
//                    }
//
//                    public void visit(OWLObjectHasValue ce) {
//                        process(ce);
//                    }
//
//                    public void visit(OWLObjectMinCardinality ce) {
//                        process(ce);
//                    }
//
//                    public void visit(OWLObjectExactCardinality ce) {
//                        process(ce);
//                    }
//
//                    public void visit(OWLObjectMaxCardinality ce) {
//                        process(ce);
//                    }
//
//                    public void visit(OWLObjectHasSelf ce) {
//                        throw new UnsupportedOperationException();
//                    }
//
//                    public void visit(OWLObjectOneOf ce) {
//                        throw new UnsupportedOperationException();
//                    }
//
//                    public void visit(OWLDataSomeValuesFrom ce) {
//                        process(ce);
//                    }
//
//                    public void visit(OWLDataAllValuesFrom ce) {
//                        process(ce);
//                    }
//
//                    public void visit(OWLDataHasValue ce) {
//                        process(ce);
//                    }
//
//                    public void visit(OWLDataMinCardinality ce) {
//                        process(ce);
//                    }
//
//                    public void visit(OWLDataExactCardinality ce) {
//                        process(ce);
//                    }
//
//                    public void visit(OWLDataMaxCardinality ce) {
//                        process(ce);
//                    }
//                });
//                System.err.println(clax);
//                System.err.println();
//            }
//        }
//
//
//
//        for ( OWLProperty prop : domains.keySet() ) {
//            dirty = true;
//            OWLClass dom = factory.getOWLClass(
//                    IRI.create(DLUtils.getInstance().buildNameFromIri(prop.getIRI().toQuotedString() + "Domain")))  ;
//            ontoDescr.getOWLOntologyManager().applyChange(
//                    new AddAxiom( ontoDescr, factory.getOWLDeclarationAxiom( dom ) )
//            );
//            ontoDescr.getOWLOntologyManager().applyChange(
//                    new AddAxiom( ontoDescr, factory.getOWLEquivalentClassesAxiom( dom, factory.getOWLObjectUnionOf( domains.get( prop ) ) ) )
//            );
//        }
//        for ( OWLProperty prop : ranges.keySet() ) {
//            dirty = true;
//            OWLClass dom = factory.getOWLClass(
//                    IRI.create(DLUtils.getInstance().buildNameFromIri(prop.getIRI().toQuotedString() + "Range")))  ;
//            ontoDescr.getOWLOntologyManager().applyChange(
//                    new AddAxiom( ontoDescr, factory.getOWLDeclarationAxiom( dom ) )
//            );
//            ontoDescr.getOWLOntologyManager().applyChange(
//                    new AddAxiom( ontoDescr, factory.getOWLEquivalentClassesAxiom( dom, factory.getOWLObjectUnionOf( ranges.get( prop ) ) ) )
//            );
//
//        }
//        for ( OWLProperty prop : dataRanges.keySet() ) {
//            dirty = true;
//            OWLDatatype dom = factory.getOWLDatatype(
//                    IRI.create(DLUtils.getInstance().buildNameFromIri(prop.getIRI().toQuotedString() + "Range")))  ;
//            ontoDescr.getOWLOntologyManager().applyChange(
//                    new AddAxiom( ontoDescr, factory.getOWLDeclarationAxiom( dom ) )
//            );
//            ontoDescr.getOWLOntologyManager().applyChange(
//                    new AddAxiom( ontoDescr, factory.getOWLDataPropertyRangeAxiom( prop.asOWLDataProperty(), factory.getOWLDataUnionOf( dataRanges.get( prop ) )
//                              ) )
//            );
//
//        }




        if ( dirty ) {
            reasoner = initReasoner( ontoDescr );
            reasoner.fillOntology( ontoDescr.getOWLOntologyManager(), ontoDescr );
        }






        // Build as needed



        Map<String, Concept> cache = new HashMap<String, Concept>();
        for ( OWLClass con : ontoDescr.getClassesInSignature() ) {
//            if ( ! con.isOWLThing() ) {

                Concept concept =  new Concept(
                                    con.getIRI().toQuotedString(),
                                    DLUtils.getInstance().buildNameFromIri( con.getIRI() ) );
                model.addConcept( concept );
                cache.put( con.getIRI().toQuotedString(), concept );
//            }
        }


        for ( OWLClass con : ontoDescr.getClassesInSignature() ) {
//            if ( ! con.isOWLThing() ) {
                Concept concept = cache.get( con.getIRI().toQuotedString() );

                for ( OWLSubClassOfAxiom sc : ontoDescr.getSubClassAxiomsForSubClass( con ) ) {
                    if (
//                            ! sc.getSuperClass().isOWLThing()
//                                    &&
                            ! sc.getSuperClass().isAnonymous()
                        ) {
                        SubConceptOf subcon = new SubConceptOf( sc.getSubClass().asOWLClass().getIRI().toQuotedString(), sc.getSuperClass().asOWLClass().getIRI().toQuotedString() );
                        concept.getSuperConcepts().add( cache.get( subcon.getObject() ) );
                        model.addSubConceptOf( subcon );
                    }
                }
                System.out.println( concept );
//            }
        }

//        for ( OWLObjectProperty op : ontoDescr.getObjectPropertiesInSignature() ) {
//            if ( ! op.isTopEntity() ) {
//                PropertyRelation prop = model.getProperty( op.getIRI().toQuotedString() );
//                Concept con = cache.get( prop.getSubject() );
//                con.getProperties().put( prop, cache.get( prop.getObject() ) );
//            }
//        }
//
//        for ( OWLDataProperty dp : ontoDescr.getDataPropertiesInSignature() ) {
//            if ( ! dp.isTopEntity() ) {
//                PropertyRelation prop = model.getProperty( dp.getIRI().toQuotedString() );
//                Concept con = cache.get( prop.getSubject() );
//                con.getProperties().put( prop, primitives.get( prop.getObject() ) );
//            }
//        }

            for ( PropertyRelation prop : model.getProperties() ) {
                Concept con = cache.get( prop.getSubject() );
                con.getProperties().put( prop, cache.get( prop.getObject() ) );
            }


        return model;
    }

    private void addDataRange(Map<OWLProperty, Set<OWLDataRange>> dataRanges, OWLDataProperty dp, OWLDataRange owlData) {
        Set<OWLDataRange> set = dataRanges.get( dp );
        if ( set == null ) {
            set = new HashSet<OWLDataRange>();
            dataRanges.put( dp, set );
        }
        set.add( owlData );
    }

    private void addRange(Map<OWLProperty, Set<OWLClassExpression>> ranges, OWLProperty rp, OWLClassExpression owlKlass ) {
        Set<OWLClassExpression> set = ranges.get( rp );
        if ( set == null ) {
            set = new HashSet<OWLClassExpression>();
            ranges.put( rp, set );
        }
        set.add( owlKlass );

    }

    private void addDomain(Map<OWLProperty, Set<OWLClassExpression>> domains, OWLProperty dp, OWLClassExpression owlKlass) {
        Set<OWLClassExpression> set = domains.get( dp );
        if ( set == null ) {
            set = new HashSet<OWLClassExpression>();
            domains.put( dp, set );
        }

        set.add( owlKlass );
    }


    private InferredOntologyGenerator initReasoner(OWLOntology ontoDescr) {
        Reasoner hermit = new Reasoner( ontoDescr );
        InferredOntologyGenerator reasoner = new InferredOntologyGenerator( hermit );
            reasoner.addGenerator( new InferredSubClassAxiomGenerator() );
            reasoner.addGenerator( new InferredEquivalentClassAxiomGenerator() );
            reasoner.addGenerator( new InferredClassAssertionAxiomGenerator() );
            reasoner.addGenerator( new InferredSubObjectPropertyAxiomGenerator() );
            reasoner.addGenerator( new InferredObjectPropertyCharacteristicAxiomGenerator() );
            reasoner.addGenerator( new InferredPropertyAssertionGenerator() );
        return reasoner;
    }


}
