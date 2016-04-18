/*
 * Default vertex schema/definiton for graph construction
 */

package org.h2.graph;

import java.util.HashMap;
import org.h2.graph.EdgeSchema;
import org.h2.table.Table;
import org.h2.engine.Session;



/**
 * Default vertex schema/definiton for graph construction
 */
public class VertexSchema {

    private Session session;
    private String label;
    public Table sourceTable;

    public HashMap<String, EdgeSchema> incomingEdges = new HashMap<String, EdgeSchema>();
    public HashMap<String, EdgeSchema> outgoingEdges = new HashMap<String, EdgeSchema>();

    /**
     * Constructor accepts a reference to the underlying table
     */
    public VertexSchema(Session session, Table sourceTable, String label) {
        this.session = session;
        this.sourceTable = sourceTable;
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

}
