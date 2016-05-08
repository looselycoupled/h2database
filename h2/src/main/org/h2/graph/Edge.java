/*
 * CMSC724 Graph API package
 */
package org.h2.graph;

import java.util.Iterator;
import java.util.Set;
import java.util.List;
import org.h2.result.Row;
import org.h2.value.Value;
import java.util.HashMap;

import com.tinkerpop.blueprints.Direction;


/**
 * Default edge implementation.  It currently assumes that edges are represented
 * as rows in a join table.
 */
public class Edge {
    private EdgeSchema schema;
    private List<Row> rows;
    private Vertex srcV;
    private Vertex dstV;

    public Edge(Vertex srcV, Vertex dstV, List<Row> rows, EdgeSchema schema) {
        this.srcV = srcV;
        this.dstV = dstV;
        this.rows = rows;
        this.schema = schema;
    }

    public String getLabel() {
        return this.schema.getLabel();
    }

    public Vertex getDstVertex() {
        return getVertex(Direction.OUT);
    }

    public Object getId() {
        return null;
    }

    public Value removeProperty(String key){
        return null;
    }

    public void setProperty(String key, Value value){}

    public Set<String> getPropertyKeys(){
        return null;
    }

    public <T> T getProperty(String key){
        return null;
    }

    public Vertex getVertex(Direction direction) {
        if (direction == Direction.OUT) {
            return dstV;
        } else if ( direction == Direction.IN) {
            return srcV;
        } else {
            throw new IllegalArgumentException("direction must be IN or OUT");
        }
    }

    /**
     * Methods required by the interface that we don't plan to implement
     */
    public void remove() {}

}
