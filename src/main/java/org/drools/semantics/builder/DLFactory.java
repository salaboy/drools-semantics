package org.drools.semantics.builder;


import org.drools.io.Resource;
import org.drools.runtime.StatefulKnowledgeSession;
import org.drools.semantics.builder.model.OntoModel;
import org.drools.semantics.builder.model.inference.ModelInferenceStrategy;
import org.semanticweb.owlapi.model.OWLOntology;

import java.util.Map;

public interface DLFactory {


    public enum INFERENCE_STRATEGY { INTERNAL, EXTERNAL }
    public enum SupportedReasoners { HERMIT, PELLET }

    public void setInferenceStrategy( INFERENCE_STRATEGY strategy );
    public void setExternalReasoner( SupportedReasoners externalReasoner );




    public OWLOntology parseOntology( Resource resource );





    public String buildTableauRules( OWLOntology ontologyDescr, Resource[] visitor );






    public OntoModel buildModel( String name, OWLOntology ontoDescr, Map<ModelInferenceStrategy.InferenceTask, Resource> theory );

    public OntoModel buildModel( String name, Resource res, StatefulKnowledgeSession kSession );

    public OntoModel buildModel( String name, Resource res );


}
