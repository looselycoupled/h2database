/*
 * CMSC724 Graph API package
 */
package org.h2.graph;

import java.util.Iterator;


public class Graph {

    public static Edge getEdge(Object id) {
        // Return the edge referenced by the provided object identifier.
        return null;
    }

    public static Iterable<Edge> getEdges() {
        //  Return an iterable to all the edges in the graph.
        return null;
    }

    public static Iterable<Edge> getEdges(String key, Object value) {
        //  Return an iterable to all the edges in the graph that have a particular key/value property.
        return null;
    }

    public static Vertex getVertex(Object id) {
        //  Return the vertex referenced by the provided object identifier.
        return null;
    }

    public static Iterable<Vertex> getVertices() {
        //  Return an iterable to all the vertices in the graph.
        return null;
    }

    public static Iterable<Vertex> getVertices(String key, Object value) {
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


}
