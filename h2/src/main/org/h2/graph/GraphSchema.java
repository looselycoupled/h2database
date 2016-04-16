/*
 * Default graph schema/definiton.
 */

package org.h2.graph;

import java.util.ArrayList;
// import org.h2.graph.EdgeSchema;
// import org.h2.graph.VertexSchema;



/**
 * Default graph schema/definiton.
 */
public class GraphSchema {

    public String name;
    public ArrayList<EdgeSchema> edgeSchemas = new ArrayList<EdgeSchema>();
    public ArrayList<VertexSchema> vertexSchemas = new ArrayList<VertexSchema>();

}
