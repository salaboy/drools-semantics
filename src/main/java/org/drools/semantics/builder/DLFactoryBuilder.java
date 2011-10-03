package org.drools.semantics.builder;


public class DLFactoryBuilder {

    public static DLFactory newDLFactoryInstance() {
        return DLFactoryImpl.getInstance();
    }
}
