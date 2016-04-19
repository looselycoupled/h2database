/*
 * Default edge schema/definiton for graph construction
 */

package org.h2.graph;

import java.util.List;
import java.util.ArrayList;

import org.h2.engine.Session;
import org.h2.table.Table;
import org.h2.table.Column;
import org.h2.result.Row;
import org.h2.index.Cursor;



/**
 * Default edge schema/definiton for graph construction.  An edge can span
 * multiple joins such as (from the school dataset):
 *
 *   Student -> Registration -> Class -> Room
 *
 */
public class EdgeSchema {

    private Session session;
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
    public EdgeSchema(Session session, Boolean directed, String label) {
        this.session = session;
        this.directed = directed;
        this.label = label;
    }

    /**
     * Constructor accepts a reference to the underlying table and defaults
     * to the edge not being directed
     */
    public EdgeSchema(Session session, String label) {
        this.session = session;
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


    /**
     * Public method to find the matching vertex at the end of an edge relationship.
     *
     * NOTE THAT THIS ASSUMES THE VERTEX HAS ONLY ONE EDGE OF THIS TYPE.
     */
    public Vertex getTargetVertex(Vertex sourceVertex) {
        Boolean init = true;
        int targetId = sourceVertex.getAttributes().get("ID").getInt();
        Row row = null;

        // loop through joined tables
        for (JoinSchema j: joins) {
            if (init) {
                init = false;
            } else {
                targetId = row.getValue(getColumnPosition(j.sourceTable, j.sourceColumn)).getInt();
            }

            // loop through rows of joined tables looking for our target row
            Cursor c = j.targetTable.getScanIndex(session).find(session, null, null);
            while (c.next()) {
                Row cursorRow = c.get();
                if (cursorRow.getValue(getColumnPosition(j.targetTable, j.targetColumn)).getInt() == targetId) {
                    // we found our join row
                    row = cursorRow;
                    break;
                }
            }
        }

        Table t = joins.get(joins.size() - 1).targetTable;
        Vertex endVertex = new Vertex(row, t.getColumns());
        return endVertex;
    }

    public int getColumnPosition(Table table, String name) {
        return getColumnPosition(table, table.getColumn(name.toUpperCase()));
    }

    public int getColumnPosition(Table table, Column column) {
        int counter = 0;
        for (Column c: table.getColumns()) {
            if (c == column) {
                return counter;
            }
            counter += 1;
        }
        return -1;
    }

    public Boolean isDirected() {
        return directed;
    }

    public String getLabel() {
        return label;
    }




}
