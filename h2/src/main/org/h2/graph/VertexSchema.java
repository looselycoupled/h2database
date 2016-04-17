/*
 * Default vertex schema/definiton for graph construction
 */

package org.h2.graph;

import java.util.List;
import java.util.ArrayList;
import org.h2.graph.EdgeSchema;
import org.h2.table.Table;



/**
 * Default vertex schema/definiton for graph construction
 */
public class VertexSchema {

    private String label;
    public Table sourceTable;

    public List<EdgeSchema> incomingEdges = new ArrayList<EdgeSchema>();
    public List<EdgeSchema> outgoingEdges = new ArrayList<EdgeSchema>();

    /**
     * Constructor accepts a reference to the underlying table
     */
    public VertexSchema(Table sourceTable, String label) {
        this.sourceTable = sourceTable;
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

}
