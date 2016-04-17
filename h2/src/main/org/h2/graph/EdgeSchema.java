/*
 * Default edge schema/definiton for graph construction
 */

package org.h2.graph;

import java.util.List;
import java.util.ArrayList;

import org.h2.graph.EdgeSchema;
import org.h2.table.Table;
import org.h2.table.Column;



/**
 * Default edge schema/definiton for graph construction.  An edge can span
 * multiple joins such as (from the school dataset):
 *
 *   Student -> Registration -> Class -> Room
 *
 */
public class EdgeSchema {

    private String label;
    private Boolean directed;
    private List<JoinSchema> joins = new ArrayList<JoinSchema>();

    /**
     * Utility class to define a join relationship
     */
    private class JoinSchema {
        public Table sourceTable;
        public Column sourceColumn;
        public Table targetTable;
        public Column targetColumn;

        public JoinSchema(Table sourceTable, Column sourceColumn, Table targetTable, Column targetColumn) {
            this.sourceTable = sourceTable;
            this.sourceColumn = sourceColumn;
            this.targetTable = targetTable;
            this.targetColumn = targetColumn;
        }
    }

    /**
     * Constructor accepts a reference to the underlying table and whether
     * the edge should be considered directed
     */
    public EdgeSchema(Boolean directed, String label) {
        this.directed = directed;
        this.label = label;
    }

    /**
     * Constructor accepts a reference to the underlying table and defaults
     * to the edge not being directed
     */
    public EdgeSchema(String label) {
        this.directed = false;
        this.label = label;
    }

    /**
     * Returns the last table in the current set of joins
     */
    private Table getLastTable() {
        return joins.get(joins.size() - 1).targetTable;
    }

    /**
     * Public method to add consecutive joins that as a whole will define the
     * edge relationship.
     */
    public void addJoin(Table sourceTable, Column sourceColumn, Table targetTable,
        Column targetColumn) throws Exception {

        // make sure this join starts with the same table we currently end with
        if (joins.size() > 0) {
            if (sourceTable.getName() != getLastTable().getName()) {
                throw new Exception("SourceTable does not match last table joined.");
            }
        }
        // add new joinschema object
        JoinSchema j = new JoinSchema(sourceTable, sourceColumn, targetTable, targetColumn);
        joins.add(j);
    }


    public Boolean isDirected() {
        return directed;
    }

    public String getLabel() {
        return label;
    }




}
