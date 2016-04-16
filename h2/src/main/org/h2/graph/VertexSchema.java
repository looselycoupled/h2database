/*
 * Default vertex schema/definiton for graph construction
 */

package org.h2.graph;

import java.util.ArrayList;
import org.h2.graph.EdgeSchema;
import org.h2.table.Table;



/**
 * Default vertex schema/definiton for graph construction
 */
public class VertexSchema {

    public Table sourceTable;

    public ArrayList<EdgeSchema> incomingEdges = new ArrayList<EdgeSchema>();
    public ArrayList<EdgeSchema> outgoingEdges = new ArrayList<EdgeSchema>();

    /**
     * Constructor accepts a reference to the underlying table
     */
    public VertexSchema(Table sourceTable) {
        this.sourceTable = sourceTable;
    }

}
