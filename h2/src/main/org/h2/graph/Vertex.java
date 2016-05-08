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
    private boolean foundEdges = false;

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

    public List<Edge> getEdges(Direction direction, String... labels) {
        if (direction == Direction.IN){
            // currently not running this
            return inEdges;
        }
        else if (direction == Direction.OUT) {
            if (foundEdges == false){
                outEdges = findOutEdges();
                foundEdges = true;
            }
            return outEdges;
        }
        else {
            // also not calling this
            return new ArrayList<Edge>() { { addAll(inEdges); addAll(outEdges); } };
        }
    }

    private List<Edge> findOutEdges() {
        List<Edge> edges = schema.edgeSchema.connectVertex(this);
        // System.out.println(edges);
        return edges;
    }

    public Iterable<Vertex> getVertices(Direction direction, String... labels) {
        Iterable<Edge> edges = getEdges(direction, labels);
        List<Vertex> vertices = new ArrayList<Vertex>();
        for (Edge e: edges) {
            Vertex v = e.getVertex(direction);
            vertices.add(v);
        }
        return vertices;
    }

    public Direction reverseDirection(Direction direction) {
        if (direction == Direction.IN){
            return Direction.OUT;
        }
        else if (direction == Direction.OUT) {
            return Direction.IN;
        }
        else {
            return Direction.BOTH;
        }
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

}
