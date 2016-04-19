/*
 * Default graph schema/definiton.
 */

package org.h2.graph;

import java.util.HashMap;


/**
 * Default graph schema/definiton.
 */
public class GraphSchema {

    private String name;

    public HashMap<String, EdgeSchema> edgeSchemas = new HashMap<String, EdgeSchema>();
    public HashMap<String, VertexSchema> vertexSchemas = new HashMap<String, VertexSchema>();

    public GraphSchema(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }


}
