/*
 * CMSC724 Graph API package
 */
package org.h2.graph;

import java.util.Iterator;
import java.util.ArrayList;
import java.util.List;


public class Graph {

    private List<Vertex> vertices;

    public Graph() {
        vertices = new ArrayList<Vertex>();
    }

    public Iterable<Vertex> getVertices() {
        //  Return an iterable to all the vertices in the graph.
        return vertices;
    }

    public Vertex addVertex(Object id) {
        return vertices.get(0);
    }

    public Vertex addVertex(Vertex vertex) {
        vertices.add(vertex);
        return vertex;
    }

    public void addVertices(List<Vertex> v){
        vertices.addAll(v);
    }

    public Edge getEdge(Object id) {
        // Return the edge referenced by the provided object identifier.
        return null;
    }

    public Iterable<Edge> getEdges() {
        //  Return an iterable to all the edges in the graph.
        return null;
    }

    public Iterable<Edge> getEdges(String key, Object value) {
        //  Return an iterable to all the edges in the graph that have a particular key/value property.
        return null;
    }

    public Vertex getVertex(Object id) {
        //  Return the vertex referenced by the provided object identifier.
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

    

    public Edge addEdge(Object id, Vertex outVertex, Vertex inVertex, String label){
        return null;
    }

    

    /**
     * Methods required by the interface that we don't want to implement for now
     */

    // public void removeEdge(Edge edge) {}

    // public GraphQuery query() {
    //     return null;
    // }

    // public void removeVertex(Vertex vertex) {}

    // public Features getFeatures(){
    //     return null;
    // }

}
