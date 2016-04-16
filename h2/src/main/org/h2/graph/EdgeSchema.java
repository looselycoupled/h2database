/*
 * Default edge schema/definiton for graph construction
 */

package org.h2.graph;

import org.h2.graph.EdgeSchema;
import org.h2.table.Table;
import org.h2.table.Column;



/**
 * Default edge schema/definiton for graph construction
 */
public class EdgeSchema {

    public Boolean directed;

    public Table sourceTable;
    public Column sourceColumn;

    public Table targetTable;
    public Column targetColumn;

    /**
     * Constructor accepts a reference to the underlying table and whether
     * the edge should be considered directed
     */
    public EdgeSchema(Boolean directed, Table sourceTable) {
        this.directed = directed;
        this.sourceTable = sourceTable;
    }

    /**
     * Constructor accepts a reference to the underlying table and defaults
     * to the edge not being directed
     */
    public EdgeSchema(Table sourceTable) {
        this.directed = false;
        this.sourceTable = sourceTable;
    }

}
