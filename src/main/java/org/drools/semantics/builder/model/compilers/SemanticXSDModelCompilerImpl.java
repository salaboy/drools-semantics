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

package org.drools.semantics.builder.model.compilers;

import org.drools.io.ResourceFactory;
import org.drools.semantics.builder.DLTemplateManager;
import org.drools.semantics.builder.DLUtils;
import org.drools.semantics.builder.model.*;
import org.jdom.Element;
import org.mvel2.templates.TemplateRegistry;
import org.mvel2.templates.TemplateRuntime;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class SemanticXSDModelCompilerImpl extends XSDModelCompilerImpl implements SemanticXSDModelCompiler {


    private static final String defaultBindings = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
            "<bindings xmlns=\"http://java.sun.com/xml/ns/jaxb\"\n" +
            "          xmlns:xsi=\"http://www.w3.org/2000/10/XMLSchema-instance\"\n" +
            "          xmlns:xjc=\"http://java.sun.com/xml/ns/jaxb/xjc\"\n" +
            "          xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\"\n" +
            "          xmlns:inheritance=\"http://jaxb2-commons.dev.java.net/basic/inheritance\"\n" +
            "          xsi:schemaLocation=\"http://java.sun.com/xml/ns/jaxb http://java.sun.com/xml/ns/jaxb/bindingschema_2_0.xsd\"\n" +
            "          version=\"2.1\"\n" +
            "          extensionBindingPrefixes=\"xjc\" >\n" +
            "  <bindings>\n" +
            "    <globalBindings localScoping=\"toplevel\" >\n" +
            "      <serializable/>\n" +
            "      <xjc:simple/>\n" +
            "      <xjc:treatRestrictionLikeNewType/>\n" +
            "    </globalBindings>\n" +
            "\n" +
            "  </bindings>\n" +
            "</bindings>";


    private TemplateRegistry registry = DLTemplateManager.getDataModelRegistry( ModelFactory.CompileTarget.XSDX );

    private String semGetterTemplateName = "semGetter.drlt";
    private String semSetterTemplateName = "semSetter.drlt";

    @Override
    public CompiledOntoModel compile(OntoModel model) {

        SemanticXSDModel sxsdModel = (SemanticXSDModel) super.compile(model);
        sxsdModel.setBindings( createBindings( sxsdModel ) );

        return sxsdModel;
    }


    public void setModel(OntoModel model) {
        this.model = (CompiledOntoModel) ModelFactory.newModel( ModelFactory.CompileTarget.XSDX, model );

        ((XSDModel) getModel()).setNamespace( "tns", DLUtils.reverse( model.getPackage() ) );
    }

    private String createBindings( SemanticXSDModel sxsdModel ) {

        try {

            String template = readFile( "bindings.xjb.template" );
            Map<String,Object> vars = new HashMap<String,Object>();
                vars.put( "package", getModel().getPackage() );
                vars.put( "concepts", getModel().getConcepts() );
                vars.put( "flat", this.getCurrentMode().equals( Mode.FLAT ) );
                vars.put( "properties", propCache );
                vars.put( "modelName", getModel().getName() );
                vars.put( "extraCode", prepareCodeExtensions( sxsdModel ) );
            String bindings = TemplateRuntime.eval( template, vars ).toString();


//            System.out.println( bindings );
            return bindings;
        } catch ( IOException ioe ) {
            ioe.printStackTrace();
            return defaultBindings;
        }
    }

    private Map<String,String> prepareCodeExtensions(SemanticXSDModel sxsdModel) {
        Map<String,String> code = new HashMap<String, String>( sxsdModel.getConcepts().size() );
        for ( Concept con : sxsdModel.getConcepts() ) {
            StringBuilder sb = new StringBuilder("");
            
            for ( String propKey : con.getProperties().keySet() ) {
                PropertyRelation prop = con.getProperties().get( propKey );
                if ( prop.isRestricted() ) {

                    sb.append("\n").append("\t public void ");
                }
            }
            
        }
        return code;
    }


    private static String readFile(String name) throws IOException {
        String fullPath = SemanticXSDModelCompiler.class.getPackage().getName().replace(".",File.separator)
                        + File.separatorChar
                        + name;
        
        InputStream stream = ResourceFactory.newClassPathResource( fullPath ).getInputStream();
        try {
              byte[] data = new byte[ stream.available() ];
              stream.read(data);
              return new String( data );
        }
        finally {
            stream.close();
        }
    }
}



