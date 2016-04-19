/*
 * CMSC724 Graph API package
 */
package org.h2.graph;

import java.util.Iterator;

import com.tinkerpop.blueprints.*;
import com.tinkerpop.blueprints.GraphQuery;


public class Graph implements com.tinkerpop.blueprints.Graph {

    public Edge getEdge(Object id) {
        // Return the edge referenced by the provided object identifier.
        return null;
    }

    public Iterable<com.tinkerpop.blueprints.Edge> getEdges() {
        //  Return an iterable to all the edges in the graph.
        return null;
    }

    public Iterable<com.tinkerpop.blueprints.Edge> getEdges(String key, Object value) {
        //  Return an iterable to all the edges in the graph that have a particular key/value property.
        return null;
    }

    public Vertex getVertex(Object id) {
        //  Return the vertex referenced by the provided object identifier.
        return null;
    }

    public Iterable<com.tinkerpop.blueprints.Vertex> getVertices() {
        //  Return an iterable to all the vertices in the graph.
        return null;
    }

    public Iterable<com.tinkerpop.blueprints.Vertex> getVertices(String key, Object value) {
        //  Return an iterable to all the vertices in the graph that have a particular key/value property.
        return null;
    }


    /* The following are suggested stubs for registering the graph definition */
    public void register() {
        // public method to register a graph definition
    }

    public void deregister() {
        // public method to deregister a graph definition
    }

    public void shutdown() {
        deregister();
    }

    

    public Edge addEdge(Object id, com.tinkerpop.blueprints.Vertex outVertex, com.tinkerpop.blueprints.Vertex inVertex, String label){
        return null;
    }

    public Vertex addVertex(Object id) {
        return null;
    }

    /**
     * Methods required by the interface that we don't want to implement for now
     */

    public void removeEdge(com.tinkerpop.blueprints.Edge edge) {}

    public GraphQuery query() {
        return null;
    }

    public void removeVertex(com.tinkerpop.blueprints.Vertex vertex) {}

    public com.tinkerpop.blueprints.Features getFeatures(){
        return null;
    }

}
