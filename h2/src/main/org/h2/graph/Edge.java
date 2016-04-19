/*
 * CMSC724 Graph API package
 */
package org.h2.graph;

import java.util.Iterator;
import java.util.Set;
import org.h2.result.Row;
import org.h2.value.Value;
import java.util.HashMap;

import com.tinkerpop.blueprints.*;
import com.tinkerpop.blueprints.Direction;


/**
 * Default edge implementation.  It currently assumes that edges are represented
 * as rows in a join table.
 */
public class Edge implements com.tinkerpop.blueprints.Edge {
    private String label;
    private Row row;

    public Edge(Row row, String label) {
        this.row = row;
        this.label = label;
    }

    public String getLabel() {
        return this.label;
    }

    public Object getId() {
        return null;
    }

    public <T> T removeProperty(String key){
        return null;
    }

    public void setProperty(String key, Object value){}

    public Set<String> getPropertyKeys(){
        return null;
    }

    public <T> T getProperty(String key){
        return null;
    }

    public Vertex getVertex(Direction direction) {
        if (direction == Direction.IN || direction == Direction.OUT || direction == Direction.BOTH) {
            // return the Vertex lazily;
            return null;
        } else {
            throw new IllegalArgumentException("direction must be IN or OUT or BOTH");
        }
    }

    public Value[] getValues() {
        return row.getValueList();
    }

    public HashMap<String, Value> getAttributes() throws Exception {
        throw new Exception("Not Implemented");
    }

    public String toString() {
      return "<Edge> ID: " + row.getKey();
    }

    public String toJSON() throws Exception {
        throw new Exception("Not Implemented");
    }

    /**
     * Methods required by the interface that we don't plan to implement
     */
    public void remove() {}

}
