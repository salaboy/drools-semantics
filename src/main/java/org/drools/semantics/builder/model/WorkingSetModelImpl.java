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

import org.drools.guvnor.client.rpc.WorkingSetConfigData;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class WorkingSetModelImpl extends ModelImpl implements WorkingSetModel {


    private WorkingSetConfigData root;

//    private Set<String> factz;


    public WorkingSetModelImpl() {
        root  = new WorkingSetConfigData();
        root.setName( "Thing" );
        root.setDescription( " Classes : " );
        root.setValidFacts( new String[] { "Thing" } );

//        factz = new HashSet<String>();
    }

    public WorkingSetConfigData getWorkingSet() {
        return root;
    }




    public void addTrait(String name, Object trait) {
//        if ( ! factz.contains( name ) ) {
//            factz.add( name );
//
//            String[] facts = root.getValidFacts();
//            String[] newFacts = new String[facts.length+1];
//            System.arraycopy(facts, 0, newFacts, 0, facts.length);
//            newFacts[ facts.length ] = name;
//            root.setValidFacts( newFacts );
//
//        }
    }


    public Object getTrait(String name) {
        return getTrait( name, root );
    }

    private Object getTrait(String name, WorkingSetConfigData root) {
        if ( root.getName().equals( name ) ) {
            return root;
        } else {
            if ( root.getWorkingSets().length > 0 ) {
                for ( int j = 0; j < root.getWorkingSets().length; j++ ) {
                    if ( getTrait( name, root.getWorkingSets()[j]) != null ) {
                        return root.getWorkingSets()[j];
                    }
                }
                return null;
            } else {
                return null;
            }
        }
    }


    public Set<String> getTraitNames() {
        List<String> list = Arrays.asList( root.validFacts );
        Set<String> set = new HashSet<String>();
        set.addAll( list );
        return set;
    }

    @Override
    protected String traitsToString() {
        return Arrays.asList( root.validFacts ).toString();
    }



}
