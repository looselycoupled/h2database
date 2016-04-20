/*
 * CMSC724 Graph API package
 */
package org.h2.graph;

import java.util.HashMap;
import java.util.Map;
import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;
import java.util.Set;
import org.h2.result.Row;
import org.h2.table.Column;
import org.h2.value.Value;

// import com.tinkerpop.blueprints.*;
import com.tinkerpop.blueprints.Direction;
// import com.tinkerpop.blueprints.VertexQuery;


/**
 * Default vertex implementation.  It basically wraps a Row object.
 */
public class Vertex {
    public Row row;
    private Object id;
    private VertexSchema schema;
    private List<Edge> outEdges;
    private List<Edge> inEdges;

    /**
     * Vertex needs an id, a reference to the underlying data, 
     */
    public Vertex(Object id, Row row, VertexSchema schema) {
        this.row = row;
        this.id = id;
        this.schema = schema;
        outEdges = new ArrayList<Edge>();
        inEdges = new ArrayList<Edge>();
    }

    public Object getId() {
        return id;
    }

    public Edge addEdge(String label, Vertex inVertex) {
        return null;
    }

    public void addEdge(Edge edge, Direction direction) {
        if (direction == Direction.IN){
            inEdges.add(edge);
        }
        else if (direction == Direction.OUT) {
            outEdges.add(edge);
        }
        else if (direction == Direction.BOTH) {
            inEdges.add(edge);
            outEdges.add(edge);
        }
    }

    public Iterable<Edge> getEdges(Direction direction, String... labels) {
        if (direction == Direction.IN){
            return inEdges;
        }
        else if (direction == Direction.OUT) {
            return outEdges;
        }
        else {
            return new ArrayList<Edge>() { { addAll(inEdges); addAll(outEdges); } };
        }
    }

    public Iterable<Vertex> getVertices(Direction direction, String... labels) {
        return null;
    }

    // returns the names of the properties of the node
    public Set<String> getPropertyKeys(){
        return schema.getPropertyKeys();
    }

    // returns the value for a given property of the node
    public Value getProperty(String key){
        Integer index = schema.attributeMapping.get(key);
        Value property = row.getValue(index);
        return property;
    }

    public void print(){
        Set<String> keys = getPropertyKeys();
        for (String key: keys){
            System.out.println(key + ": " + getProperty(key).toString());
        }
    }

    // Old code that should be implemented by methods defined in blueprints
    // public Value[] getValues() {
    //     return row.getValueList();
    // }

    // public HashMap<String, Value> getAttributes() {
    //     return attributesMap;
    // }

    // public String toString() {
    //     return "<Vertex> ID: " + row.getKey();
    // }

    // public String toJSON() throws Exception {
    //     throw new Exception("Not Implemented");
    // }

    



    /**
     * Methods expected by the interface that we don't plan to implement
     */

    // public VertexQuery query() {
    //     return null;
    // }

    // public void remove() {}

    // public <T> T removeProperty(String key){
    //     return null;
    // }

    // public void setProperty(String key, Object value){}


}
