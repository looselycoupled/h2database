/*
 * CMSC724 Graph API package
 */
package org.h2.graph;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import org.h2.result.Row;
import org.h2.table.Column;
import org.h2.value.Value;

import com.tinkerpop.blueprints.*;
import com.tinkerpop.blueprints.Direction;
import com.tinkerpop.blueprints.VertexQuery;


/**
 * Default vertex implementation.  It basically wraps a Row object.
 */
public class Vertex implements com.tinkerpop.blueprints.Vertex {
    private Row row;
    private Column[] columns;
    private HashMap<String, Value> attributesMap = new HashMap<String, Value>();

    public Vertex(Row row, Column[] columns) {
        this.row = row;
        this.columns = columns;

        int counter = 0;
        for (Column c: columns) {
            attributesMap.put(c.getName(), row.getValue(counter));
            counter = counter + 1;
        }

    }

    public Edge addEdge(String label, com.tinkerpop.blueprints.Vertex inVertex) {
        return null;
    }

    public Iterable<com.tinkerpop.blueprints.Edge> getEdges(Direction direction, String... labels) {
        return null;
    }

    public Iterable<com.tinkerpop.blueprints.Vertex> getVertices(Direction direction, String... labels) {
        return null;
    }

    public VertexQuery query() {
        return null;
    }

    public Value[] getValues() {
        return row.getValueList();
    }

    public HashMap<String, Value> getAttributes() {
        return attributesMap;
    }

    public String toString() {
        return "<Vertex> ID: " + row.getKey();
    }

    public String toJSON() throws Exception {
        throw new Exception("Not Implemented");
    }

    public Object getId() {
        return null;
    }

    public void setProperty(String key, Object value){}

    public Set<String> getPropertyKeys(){
        return null;
    }

    public <T> T getProperty(String key){
        return null;
    }

    /**
     * Methods expected by the interface that we don't plan to implement
     */

    public void remove() {
    }

    public <T> T removeProperty(String key){
        return null;
    }


}
