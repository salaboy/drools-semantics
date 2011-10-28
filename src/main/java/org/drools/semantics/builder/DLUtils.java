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

package org.drools.semantics.builder;

import org.semanticweb.owlapi.model.IRI;

import java.nio.charset.Charset;
import java.util.StringTokenizer;

public class DLUtils {

    private static DLUtils instance;

    public static DLUtils getInstance() {
        if ( instance == null ) {
            instance = new DLUtils();
        }
        return instance;
    }

    private DLUtils() {

    }


    private String delims = ":/#!.<> _?";
    private final String NEG = "ObjectComplementOf";


    public String iriToPackage( String iri ) {
        StringTokenizer tok = new StringTokenizer( iri, delims );
        String pack = "";
        while ( tok.hasMoreTokens() ) {
            pack += "." + tok.nextToken();
        }
        return pack.substring(1);
    }


    public String compactUpperCase(String s) {
        System.out.println("Try to normalize " + s);
        java.util.StringTokenizer tok = new java.util.StringTokenizer(s);
        StringBuilder sb = new StringBuilder();

        while (tok.hasMoreTokens())
            sb.append(capitalize(tok.nextToken()));

        return sb.toString();
    }

    public String capitalize(String s) {
        return s.substring(0, 1).toUpperCase() + s.substring(1);
    }





    public String negate( String expr ) {
        if ( expr.startsWith(NEG) ) {
            return expr.substring(NEG.length()+1,expr.length()-1);
        } else {
            return NEG + "(" + expr + ")";
        }
    }


    public String buildNameFromIri( IRI iri ) {
        if ( iri.getFragment() != null ) {
            return iri.getFragment();
        }
        return buildNameFromIri( iri.toQuotedString() );
    }

    public String buildLowCaseNameFromIri( IRI iri ) {
        String name = buildNameFromIri( iri );
        return name.substring(0,1).toLowerCase() + name.substring(1);
    }

    public String buildNameFromIri(String iri) {

        iri = iri.substring( 1, iri.length() - 1 );
        StringTokenizer tok = new StringTokenizer( iri, delims );
        String name = "";
        while ( tok.hasMoreTokens() ) {
            name = tok.nextToken();
        }
        return compactUpperCase( name );
    }













    public static String map( String dataType, boolean box ) {
        if ( "xsd:integer".equals( dataType ) ) {
            return "java.math.BigInteger";
        } else if ( "xsd:int".equals( dataType ) ) {

            return box ? "java.lang.Integer" : "int";

        } else if ( "xsd:string".equals( dataType ) ) {
            return "java.lang.String";
        } else if ( "xsd:dateTime".equals( dataType ) ) {
            return "java.util.Date";
        } else if ( "xsd:date".equals( dataType ) ) {
            return "java.util.Date";
        } else if ( "xsd:time".equals( dataType ) ) {
            return "java.util.Date";
        } else if ( "xsd:long".equals( dataType ) ) {

            return box ? "java.lang.Long" : "long";

        } else if ( "xsd:float".equals( dataType ) ) {

            return box ? "java.lang.Float" : "float";

        } else if ( "xsd:double".equals( dataType ) ) {

            return box ? "java.lang.Double" : "double";

        } else if ( "xsd:short".equals( dataType ) ) {

            return box ? "java.lang.Short" : "short";

        } else if ( "xsd:anySimpleType".equals( dataType ) ) {
            return "java.lang.Object";
        } else if ( "xsd:boolean".equals( dataType ) ) {

            return box ? "java.lang.Boolean" : "boolean" ;

        } else if ( "xsd:byte".equals( dataType ) ) {

            return box ? "java.lang.Byte" : "byte";

        } else if ( "xsd:decimal".equals( dataType ) ) {
            return "java.math.BigDecimal";
        } else if ( "xsd:unsignedByte".equals( dataType ) ) {

            return box ? "java.lang.Short" : "short";

        } else if ( "xsd:unsignedShort".equals( dataType ) ) {

            return box ? "java.lang.Integer" : "int";

        } else if ( "xsd:unsignedInt".equals( dataType ) ) {

            return box ? "java.lang.Long" : "long";

        } else {
            return dataType;
        }

    }

}
