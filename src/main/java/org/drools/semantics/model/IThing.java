package org.drools.semantics.model;


import java.util.Collection;
import java.util.Map;
import java.util.Set;

public interface IThing<K> extends Map<String,Object> {


    public <T> T don(Class<T> type);

    public <T> T cast(Class<T> type);

    public IThing shed(Class<?> type);

    public boolean hasType(Class<?> trait);

    public Set<Class<?>> getTypes();



    public Map<String,Object> getMantle();

    public K getCore();



    public void set(String prop, Object value);


}
