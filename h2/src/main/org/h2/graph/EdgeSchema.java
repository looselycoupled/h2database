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

import com.tinkerpop.blueprints.Direction;



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
        public Integer sourceColumnID;
        public Table targetTable;
        public Column targetColumn;
        public Integer targetColumnID;

        public JoinSchema(Table sourceTable, Column sourceColumn, Integer srcColID, Table targetTable, Column targetColumn, Integer tgtColID) {
            this.sourceTable = sourceTable;
            this.sourceColumn = sourceColumn;
            this.sourceColumnID = srcColID;
            this.targetTable = targetTable;
            this.targetColumn = targetColumn;
            this.targetColumnID = tgtColID;
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
    public void addJoin(Table sourceTable, Column sourceColumn, Integer srcColID, Table targetTable,
        Column targetColumn, Integer tgtColID) throws Exception {

        // make sure this join starts with the same table we currently end with
        if (joins.size() > 0) {
            if (sourceTable.getName() != getLastTable().getName()) {
                throw new Exception("SourceTable does not match last table joined.");
            }
        }
        // add new joinschema object
        JoinSchema j = new JoinSchema(sourceTable, sourceColumn, srcColID, targetTable, targetColumn, tgtColID);
        joins.add(j);
    }

    // Given two lists of vertices, find all the edges between them
    public List<Edge> connectVertices(List<Vertex> srcVertices, List<Vertex> dstVertices){
        List<Edge> edges = new ArrayList<Edge>();
        for (Vertex srcV: srcVertices){
            // given a vertex, let's find all of its outgoing edges of the EdgeSchema type
            // will need a special case for the last join - let's assume only one join for now
            // we should abstract away the underlying row - give us an attribute that we are joining on
            // for the nodes
            List<ArrayList<Row>> prevRowsList;
            List<ArrayList<Row>> currRowsList = new ArrayList<ArrayList<Row>>();
            ArrayList<Row> srcRows = new ArrayList<Row>();
            srcRows.add(srcV.row);
            currRowsList.add(srcRows);
            int counter = 0;
            for (JoinSchema j: joins) {
                System.out.println("there are " + currRowsList.size() + " candidates");
                prevRowsList = currRowsList;
                currRowsList = new ArrayList<ArrayList<Row>>();
                // loop through all the "candidate" edges
                for (List<Row> rows: prevRowsList) {
                    int src_value = rows.get(rows.size()-1).getValue(getColumnPosition(j.sourceTable, j.sourceColumn)).getInt();
                    // if last join, loop through the dstVertices
                    if (counter == (joins.size()-1)){
                        for (Vertex dstV: dstVertices) {
                            int dst_value = dstV.getProperty(j.targetColumn.getName()).getInt();
                            if (dst_value == src_value){
                                Edge edge = new Edge(srcV, dstV, rows, this);
                                edges.add(edge);
                                srcV.addEdge(edge, Direction.OUT);
                                dstV.addEdge(edge, Direction.IN);
                            }
                        }
                    } else {
                        Cursor c = j.targetTable.getScanIndex(session).find(session, null, null);
                        while (c.next()) {
                            Row cursorRow = c.get();
                            int dst_value = cursorRow.getValue(getColumnPosition(j.targetTable, j.targetColumn)).getInt();
                            if (src_value == dst_value) {
                                // we found our join row
                                System.out.println("found matching row");
                                ArrayList<Row> extRows = new ArrayList<Row>();
                                extRows.addAll(rows);
                                extRows.add(cursorRow);
                                currRowsList.add(extRows);
                            }
                        }
                    }
                }
                counter += 1;
            }
        }
        return edges;
    }


    /**
     * Public method to find the matching vertex at the end of an edge relationship.
     *
     * NOTE THAT THIS ASSUMES THE VERTEX HAS ONLY ONE EDGE OF THIS TYPE.
     */
    public Vertex getTargetVertex(Vertex sourceVertex) {
        return null;
        // Boolean init = true;
        // int targetId = sourceVertex.getPropertyKeys().get("ID").getInt();
        // Row row = null;

        // // loop through joined tables
        // for (JoinSchema j: joins) {
        //     if (init) {
        //         init = false;
        //     } else {
        //         targetId = row.getValue(getColumnPosition(j.sourceTable, j.sourceColumn)).getInt();
        //     }

        //     // loop through rows of joined tables looking for our target row
        //     Cursor c = j.targetTable.getScanIndex(session).find(session, null, null);
        //     while (c.next()) {
        //         Row cursorRow = c.get();
        //         if (cursorRow.getValue(getColumnPosition(j.targetTable, j.targetColumn)).getInt() == targetId) {
        //             // we found our join row
        //             row = cursorRow;
        //             break;
        //         }
        //     }
        // }

        // Table t = joins.get(joins.size() - 1).targetTable;
        // Vertex endVertex = new Vertex(row, t.getColumns());
        // return endVertex;
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
